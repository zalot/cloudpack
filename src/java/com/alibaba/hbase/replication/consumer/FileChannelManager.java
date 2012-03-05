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

import org.apache.commons.collections.MapUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.zookeeper.RecoverableZooKeeper;
import org.apache.hadoop.hbase.zookeeper.ZKUtil;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.alibaba.hbase.replication.protocol.FileAdapter;
import com.alibaba.hbase.replication.protocol.Head;

/**
 * 类Manager.java的实现描述：持有consumer端的中间文件同步线程池
 * 
 * @author dongsh 2012-2-28 上午11:17:17
 */
@Service("fileChannelManager")
public class FileChannelManager {

    private static final Logger    LOG      = LoggerFactory.getLogger(FileChannelManager.class);

    protected AtomicBoolean        stopflag = new AtomicBoolean(false);
    protected ThreadPoolExecutor   fileChannelPool;
    protected FileSystem           fs;
    protected RecoverableZooKeeper zoo;
    @Autowired
    @Qualifier("consumerConf")
    protected Configuration        conf;
    @Autowired
    protected DataLoadingManager   dataLoadingManager;
    @Autowired
    protected FileAdapter          fileAdapter;

    // @PostConstruct
    public void start() throws IOException, KeeperException, InterruptedException {
        if (LOG.isInfoEnabled()) {
            LOG.info("FileChannelManager is pendding to start.");
        }
        fileChannelPool = new ThreadPoolExecutor(
                                                 conf.getInt(Constants.REP_FILE_CHANNEL_POOL_SIZE, 10),
                                                 conf.getInt(Constants.REP_FILE_CHANNEL_POOL_SIZE, 10),
                                                 conf.getInt(Constants.THREADPOOL_KEEPALIVE_TIME, 100),
                                                 TimeUnit.SECONDS,
                                                 new ArrayBlockingQueue<Runnable>(
                                                                                  conf.getInt(Constants.THREADPOOL_SIZE,
                                                                                              100)));
        fs = FileSystem.get(URI.create(conf.get(Constants.PRODUCER_FS)), conf);
        zoo = ZKUtil.connect(conf, new ReplicationZookeeperWatcher());
        Stat statZkRoot = zoo.exists(conf.get(Constants.REP_ZNODE_ROOT), false);
        if (statZkRoot == null) {
            zoo.create(conf.get(Constants.REP_ZNODE_ROOT), null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
        if (LOG.isInfoEnabled()) {
            LOG.info("FileChannelManager starts.");
        }
        scanProducerFilesAndAddToZK();
        for (int i = 1; i < conf.getInt(Constants.REP_FILE_CHANNEL_POOL_SIZE, 10); i++) {
            fileChannelPool.execute(new FileChannelRunnable(conf, dataLoadingManager, fileAdapter, stopflag));
        }
        while(true){
            Thread.sleep(5000);
            scanProducerFilesAndAddToZK();
        }
    }

    public void stop() {
        if (LOG.isInfoEnabled()) {
            LOG.info("FileChannelManager is pendding to stop.");
        }
        stopflag.set(true);
        fileChannelPool.shutdown();
    }

    /**
     * @throws IOException
     */
    private void scanProducerFilesAndAddToZK() throws IOException {
        // s1. scanProducerFiles
        // <group,filename set>
        Map<String, ArrayList<String>> fstMap = new HashMap<String, ArrayList<String>>();
        for (FileStatus fst : fs.listStatus(new Path(conf.get(Constants.PRODUCER_FS),
                                                     conf.get(Constants.TMPFILE_FILEPATH)))) {
            if (!fst.isDir()) {
                String fileName = fst.getPath().getName();
                Head fileHead = FileAdapter.validataFileName(fileName);
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
            } else if (LOG.isWarnEnabled()) {
                LOG.warn("Dir occurs in " + conf.get(Constants.TMPFILE_FILEPATH) + " .path: " + fst.getPath());
            }
        }
        // s2. update ZK
        if (MapUtils.isNotEmpty(fstMap)) {
            for (String group : fstMap.keySet()) {
                String groupRoot = conf.get(Constants.REP_ZNODE_ROOT) + Constants.FILE_SEPERATOR + group;
                String queue = groupRoot + Constants.FILE_SEPERATOR + Constants.ZK_QUEUE;
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
