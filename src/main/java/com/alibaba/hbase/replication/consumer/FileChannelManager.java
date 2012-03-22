/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.hbase.replication.consumer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.collections.MapUtils;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.hbase.replication.protocol.DefaultHDFSFileAdapter;
import com.alibaba.hbase.replication.protocol.Head;
import com.alibaba.hbase.replication.server.ReplicationConf;
import com.alibaba.hbase.replication.utility.ConsumerConstants;
import com.alibaba.hbase.replication.utility.ZKUtil;
import com.alibaba.hbase.replication.zookeeper.NothingZookeeperWatch;
import com.alibaba.hbase.replication.zookeeper.RecoverableZooKeeper;

/**
 * 类Manager.java的实现描述：持有consumer端的中间文件同步线程池
 * 
 * @author dongsh 2012-2-28 上午11:17:17
 */
@Service("fileChannelManager")
public class FileChannelManager {

    private static final Logger      LOG      = LoggerFactory.getLogger(FileChannelManager.class);

    protected AtomicBoolean          stopflag = new AtomicBoolean(false);
    protected ThreadPoolExecutor     fileChannelPool;
    protected FileSystem             fs;
    protected RecoverableZooKeeper   zoo;

    @Autowired
    protected ReplicationConf        conf;

    @Autowired
    protected DataLoadingManager     dataLoadingManager;
    @Autowired
    protected DefaultHDFSFileAdapter fileAdapter;

    @PostConstruct
    public void init() throws IOException, KeeperException, InterruptedException {
        if (LOG.isInfoEnabled()) {
            LOG.info("FileChannelManager is pendding to start.");
        }
        conf.addResource(ConsumerConstants.CONSUMER_CONFIG_FILE);
        fileAdapter.init(conf);
        fileChannelPool = new ThreadPoolExecutor(
                                                 conf.getInt(ConsumerConstants.CONFKEY_REP_FILE_CHANNEL_POOL_SIZE, 10),
                                                 conf.getInt(ConsumerConstants.CONFKEY_REP_FILE_CHANNEL_POOL_SIZE, 10),
                                                 conf.getInt(ConsumerConstants.CONFKEY_THREADPOOL_KEEPALIVE_TIME, 100),
                                                 TimeUnit.SECONDS,
                                                 new ArrayBlockingQueue<Runnable>(conf.getInt(ConsumerConstants.CONFKEY_THREADPOOL_SIZE,
                                                                                              100)));
        fs = FileSystem.get(URI.create(conf.get(ConsumerConstants.CONFKEY_PRODUCER_FS)), conf);
        zoo = ZKUtil.connect(conf, new NothingZookeeperWatch());
        Stat statZkRoot = zoo.exists(conf.get(ConsumerConstants.CONFKEY_REP_ZNODE_ROOT), false);
        if (statZkRoot == null) {
            zoo.create(conf.get(ConsumerConstants.CONFKEY_REP_ZNODE_ROOT), null, Ids.OPEN_ACL_UNSAFE,
                       CreateMode.PERSISTENT);
        }
        if (LOG.isInfoEnabled()) {
            LOG.info("FileChannelManager init.");
        }
    }

    public void start() throws IOException {
        scanProducerFilesAndAddToZK();
        FileChannelRunnable runn;
        dataLoadingManager.setConf(conf);
        dataLoadingManager.init();
        for (int i = 1; i < conf.getInt(ConsumerConstants.CONFKEY_REP_FILE_CHANNEL_POOL_SIZE, 10); i++) {
            runn = new FileChannelRunnable(conf, dataLoadingManager, fileAdapter, stopflag);
            runn.setZoo(zoo);
            runn.setFs(fs);
            fileChannelPool.execute(runn);
        }
        while (true) {
            try {
                Thread.sleep(5000);
                scanProducerFilesAndAddToZK();
            } catch (IOException e) {
                if (LOG.isErrorEnabled()) {
                    LOG.error("scanProducerFilesAndAddToZK error while looping.", e);
                }
            } catch (InterruptedException e) {
                LOG.warn("FileChannelManager sleep interrupted.Break the loop!");
                break;
            }
        }
    }

    @PreDestroy
    public void stop() {
        if (LOG.isInfoEnabled()) {
            LOG.info("FileChannelManager is pendding to stop.");
        }
        stopflag.set(true);
        fileChannelPool.shutdown();
        Thread.interrupted();
    }

    /**
     * @throws IOException
     */
    private void scanProducerFilesAndAddToZK() throws IOException {
        // s1. scanProducerFiles
        // <group,filename set>
        Map<String, ArrayList<String>> fstMap = new HashMap<String, ArrayList<String>>();
        Path targetPath = new Path(conf.get(ConsumerConstants.CONFKEY_PRODUCER_FS),
                                   conf.get(ConsumerConstants.CONFKEY_TMPFILE_TARGETPATH));
        FileStatus[] fstList = fs.listStatus(targetPath);
        if (fstList == null || fstList.length < 1) {
            if (LOG.isWarnEnabled()) {
                LOG.warn("Can not find any target File at targetPath");
            }
            return;
        }
        for (FileStatus fst : fstList) {
            if (!fst.isDir()) {
                String fileName = fst.getPath().getName();
                Head fileHead = DefaultHDFSFileAdapter.validataFileName(fileName);
                if (fileHead == null && LOG.isErrorEnabled()) {
                    LOG.error("validataFileName fail. path: " + fst.getPath());
                    continue;
                }
                String group = fileHead.getGroupName();
                ArrayList<String> ftsSet = fstMap.get(group);
                if (ftsSet == null) {
                    ftsSet = new ArrayList<String>();
                    fstMap.put(group, ftsSet);
                }
                ftsSet.add(fileName);
            }
        }
        // s2. update ZK
        if (MapUtils.isNotEmpty(fstMap)) {
            for (String group : fstMap.keySet()) {
                String groupRoot = conf.get(ConsumerConstants.CONFKEY_REP_ZNODE_ROOT)
                                   + ConsumerConstants.FILE_SEPERATOR + group;
                String queue = groupRoot + ConsumerConstants.FILE_SEPERATOR + ConsumerConstants.ZK_QUEUE;
                int queueVer;
                try {
                    Stat statZkRoot = zoo.exists(groupRoot, false);
                    if (statZkRoot == null) {
                        zoo.create(groupRoot, null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                    }
                    Stat statZkQueue = zoo.exists(queue, false);
                    if (statZkQueue == null) {
                        zoo.create(queue, null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                    }
                    statZkQueue = zoo.exists(queue, false);
                    queueVer = statZkQueue.getVersion();
                } catch (Exception e) {
                    LOG.error("Consumer create znode of group failed. Znode: " + groupRoot, e);
                    continue;
                }
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(fstMap.get(group));
                oos.flush();
                try {
                    zoo.setData(queue, baos.toByteArray(), queueVer);
                } catch (Exception e) {
                    LOG.error("Consumer update znode of queue failed. Znode: " + queue, e);
                }
            }
        }
    }

}
