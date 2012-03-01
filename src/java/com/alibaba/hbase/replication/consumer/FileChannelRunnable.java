/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.hbase.replication.consumer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.hbase.zookeeper.RecoverableZooKeeper;
import org.apache.hadoop.hbase.zookeeper.ZKUtil;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;

import com.alibaba.hbase.replication.protocol.Body.Edit;
import com.alibaba.hbase.replication.protocol.FileAdapter;
import com.alibaba.hbase.replication.protocol.Head;
import com.alibaba.hbase.replication.protocol.MetaData;
import com.alibaba.hbase.replication.protocol.exception.FileNotFoundException;
import com.alibaba.hbase.replication.protocol.exception.FileParsingException;

/**
 * 类FileChannelRunnableRunnable.java的实现描述：执行文件同步的任务类
 * 
 * @author dongsh 2012-2-29 下午03:42:49
 */
public class FileChannelRunnable implements Runnable {

    protected static final Log     LOG           = LogFactory.getLog(FileChannelRunnable.class);

    protected RecoverableZooKeeper zoo;
    protected Configuration        conf;
    protected FileSystem           fs;
    protected String               name          = Constants.CHANNEL_NAME
                                                   + UUID.randomUUID().toString().substring(0, 8);
    protected DataLoadingManager   dataLoadingManager;
    protected List<ConsumerZNode>  errorNodeList = new ArrayList<ConsumerZNode>();
    protected AtomicBoolean        stopflag;

    public FileChannelRunnable(Configuration conf, DataLoadingManager dataLoadingManager, AtomicBoolean stopflag)
                                                                                                                 throws IOException{
        this.conf = conf;
        this.stopflag = stopflag;
        this.dataLoadingManager = dataLoadingManager;
        zoo = ZKUtil.connect(conf, new ReplicationZookeeperWatcher());
        fs = FileSystem.get(URI.create(conf.get(Constants.PRODUCER_FS)), conf);
    }

    @Override
    public void run() {
        while (!stopflag.get()) {
            ConsumerZNode currentNode = tryToMakeACurrentNode();
            if (currentNode != null) {
                Head fileHead = FileAdapter.validataFileName(currentNode.getFileName());
                if (fileHead == null) {
                    if (LOG.isErrorEnabled()) {
                        LOG.error("validataFileName fail. fileName: " + currentNode.getFileName());
                    }
                } else {
                    try {
                        MetaData metaData = FileAdapter.read(fileHead, fs);
                        // 文件不存在的跳过
                        if (metaData != null && metaData.getBody() != null
                            && MapUtils.isNotEmpty(metaData.getBody().getEditMap())) {
                            Map<String, List<Edit>> editMap = metaData.getBody().getEditMap();
                            for (String tableName : editMap.keySet()) {
                                // 按表并发加载数据
                                dataLoadingManager.batchLoad(tableName, editMap.get(tableName));
                            }
                            // 执行完成后清除相关文件
                            FileAdapter.clean(fileHead, fs);
                        }
                        // 清除zk上对此group的加锁
                        String groupRoot = conf.get(Constants.REP_ZNODE_ROOT) + Constants.FILE_SEPERATOR
                                           + currentNode.getGroupName();
                        String cur = groupRoot + Constants.FILE_SEPERATOR + Constants.ZK_CURRENT;
                        zoo.delete(cur, Constants.ZK_ANY_VERSION);
                    } catch (FileParsingException e) {
                        // 文件解析出错,退回重做
                        if (LOG.isErrorEnabled()) {
                            LOG.error("error while parsing file: " + currentNode.getFileName(), e);
                        }
                        try {
                            FileAdapter.reject(fileHead, fs);
                            errorNodeList.add(currentNode);
                        } catch (Exception e1) {
                            // 文件退回出错，只能重做了
                            if (LOG.isErrorEnabled()) {
                                LOG.error("reject file failed. file name" + currentNode.getFileName(), e);
                            }
                            String groupRoot = conf.get(Constants.REP_ZNODE_ROOT) + Constants.FILE_SEPERATOR
                                               + currentNode.getGroupName();
                            String cur = groupRoot + Constants.FILE_SEPERATOR + Constants.ZK_CURRENT;
                            try {
                                // 清除zk上对此group的加锁
                                zoo.delete(cur, Constants.ZK_ANY_VERSION);
                            } catch (Exception e2) {
                                if (LOG.isWarnEnabled()) {
                                    LOG.warn("error while deleting cur from zk . cur: " + cur, e);
                                }
                            }
                        }
                    } catch (Exception e) {
                        if (LOG.isWarnEnabled()) {
                            LOG.warn("error while processing loading . file: " + currentNode.getFileName(), e);
                        }
                    }
                }
            } else {
                // 没分配到任务的sleep再重试一段时间
                try {
                    Thread.sleep(Constants.WAIT_MILLIS);
                } catch (InterruptedException e) {
                    if (LOG.isWarnEnabled()) {
                        LOG.warn("wait Interrupted. Name: " + this.name, e);
                    }
                }
            }
        }

    }

    /**
     * 尝试从zk中获取一个group去处理，并返回其创建的currentNode
     * 
     * @return
     */
    protected ConsumerZNode tryToMakeACurrentNode() {
        // 查看異常隊列中是否由更新，優先處理
        if (CollectionUtils.isNotEmpty(errorNodeList)) {
            ConsumerZNode orig = null;
            String retry = null;
            for (ConsumerZNode node : errorNodeList) {
                String groupRoot = conf.get(Constants.REP_ZNODE_ROOT) + Constants.FILE_SEPERATOR + node.getGroupName();
                String queue = groupRoot + Constants.FILE_SEPERATOR + Constants.ZK_QUEUE;
                try {
                    TreeSet<String> ftsSet = getQueueData(queue);
                    for (String fileName : ftsSet) {
                        if (isRetry(fileName, node.getFileName())) {
                            orig = node;
                            retry = fileName;
                            break;
                        }
                    }
                    if (retry != null) {
                        break;
                    }
                } catch (ClassNotFoundException e) {
                    if (LOG.isErrorEnabled()) {
                        LOG.error("queue ClassNotFoundException. queue:" + queue, e);
                    }
                } catch (Exception e) {
                    if (LOG.isWarnEnabled()) {
                        LOG.warn("Can't get queue from zk. queue:" + queue, e);
                    }
                }
            }
            if (retry != null) {
                errorNodeList.remove(orig);
                orig.setFileName(retry);
                return orig;
            }
        }
        // 没有可重做的異常節點
        List<String> groupList = null;
        try {
            groupList = zoo.getChildren(conf.get(Constants.REP_ZNODE_ROOT), false);
        } catch (Exception e) {
            if (LOG.isWarnEnabled()) {
                LOG.warn("no group found at zk. " + conf.get(Constants.REP_ZNODE_ROOT), e);
            }
        }
        if (CollectionUtils.isNotEmpty(groupList)) {
            for (String group : groupList) {
                String groupRoot = conf.get(Constants.REP_ZNODE_ROOT) + Constants.FILE_SEPERATOR + group;
                String cur = groupRoot + Constants.FILE_SEPERATOR + Constants.ZK_CURRENT;
                String queue = groupRoot + Constants.FILE_SEPERATOR + Constants.ZK_QUEUE;
                Stat statZkCur;
                try {
                    statZkCur = zoo.exists(cur, false);
                } catch (Exception e) {
                    if (LOG.isWarnEnabled()) {
                        LOG.warn("zk connection error while trying znode of cur: " + cur, e);
                    }
                    continue;
                }
                if (statZkCur == null) {
                    // 此group未被处理过
                    try {
                        zoo.create(cur, null, Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
                    } catch (Exception e) {
                        if (LOG.isInfoEnabled()) {
                            LOG.info("znode of cur create failed: " + cur, e);
                        }
                        continue;
                    }
                    try {
                        statZkCur = zoo.exists(cur, false);
                        TreeSet<String> ftsSet = getQueueData(queue);
                        if (CollectionUtils.isEmpty(ftsSet)) {
                            continue;
                        }
                        String fileName = ftsSet.first();
                        Head fileHead = FileAdapter.validataFileName(fileName);
                        if (fileHead == null && LOG.isErrorEnabled()) {
                            throw new RuntimeException("validataFileName fail. fileName: " + fileName);
                        }
                        ConsumerZNode tmpNode = new ConsumerZNode();
                        tmpNode.setFileLastModified(fileHead.getFileTimestamp());
                        tmpNode.setFileName(fileName);
                        tmpNode.setGroupName(group);
                        tmpNode.setProcessorName(this.name);
                        tmpNode.setStatusLastModified(System.currentTimeMillis());
                        tmpNode.setVersion(statZkCur.getVersion());
                        statZkCur = zoo.setData(cur, tmpNode.toByteArray(), statZkCur.getVersion());
                        tmpNode.setVersion(statZkCur.getVersion());
                        return tmpNode;
                    } catch (Exception e) {
                        if (LOG.isWarnEnabled()) {
                            LOG.warn("error while creating znode of cur: " + cur, e);
                        }
                        try {
                            zoo.delete(cur, Constants.ZK_ANY_VERSION);
                        } catch (Exception e1) {
                            if (LOG.isErrorEnabled()) {
                                LOG.error("error while deleting znode of cur: " + cur, e);
                            }
                        }
                        continue;
                    }
                }
            }
        }
        return null;
    }

    /**
     * @param fileName
     * @param fileName2
     * @return
     */
    protected boolean isRetry(String retry, String orig) {
        Head r = FileAdapter.validataFileName(retry);
        Head o = FileAdapter.validataFileName(orig);
        if (r != null && o != null && StringUtils.equals(r.getGroupName(), o.getGroupName())
            && r.getFileTimestamp() == o.getFileTimestamp() && r.getHeadTimestamp() == o.getHeadTimestamp()
            && r.getRetry() > o.getRetry()) {
            return true;
        }
        return false;
    }

    protected TreeSet<String> getQueueData(String queuePath) throws KeeperException, InterruptedException, IOException,
                                                            ClassNotFoundException {
        byte[] data = zoo.getData(queuePath, null, null);
        ByteArrayInputStream bi = new ByteArrayInputStream(data);
        ObjectInputStream oi = new ObjectInputStream(bi);
        Object ftsSet = oi.readObject();
        if (ftsSet == null) {
            return null;
        }
        return (TreeSet<String>) ftsSet;
    }

}
