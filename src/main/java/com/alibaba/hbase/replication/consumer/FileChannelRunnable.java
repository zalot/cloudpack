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
import java.util.ArrayList;
import java.util.Comparator;
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
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.NodeExistsException;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;

import com.alibaba.hbase.replication.protocol.ProtocolBodyV1;
import com.alibaba.hbase.replication.protocol.DefaultHDFSFileAdapter;
import com.alibaba.hbase.replication.protocol.ProtocolHead;
import com.alibaba.hbase.replication.protocol.MetaData;
import com.alibaba.hbase.replication.protocol.exception.FileParsingException;
import com.alibaba.hbase.replication.protocol.exception.FileReadingException;
import com.alibaba.hbase.replication.protocol.protobuf.SerBody.Edit;
import com.alibaba.hbase.replication.utility.ConsumerConstants;
import com.alibaba.hbase.replication.zookeeper.RecoverableZooKeeper;

/**
 * 类FileChannelRunnableRunnable.java的实现描述：执行文件同步的任务类
 * 
 * @author dongsh 2012-2-29 下午03:42:49
 * 
 * @author zalot.zhaoh 
 * ------------------------------------------
 * 1.取消了部分注入，采用原始的 set方法, 以及init
 * 2.去掉了FileNofound的错误判断，因为资源的不正确设置会导致这个错误，但暂未有时间解决 v2 将会替换该类
 * 3.此版本仅支持 Body1 Protocol 的支持
 * ------------------------------------------
 */
public class FileChannelRunnable implements Runnable {

    protected static final Log     LOG = LogFactory.getLog(FileChannelRunnable.class);

    protected RecoverableZooKeeper zoo;

    public RecoverableZooKeeper getZoo() {
        return zoo;
    }

    public void setZoo(RecoverableZooKeeper zoo) {
        this.zoo = zoo;
    }

    public FileSystem getFs() {
        return fs;
    }

    public void setFs(FileSystem fs) {
        this.fs = fs;
    }

    protected Configuration          conf;
    protected FileSystem             fs;
    protected String                 name          = ConsumerConstants.CHANNEL_NAME
                                                     + UUID.randomUUID().toString().substring(0, 8);
    protected DataLoadingManager     dataLoadingManager;
    protected DefaultHDFSFileAdapter fileAdapter;
    protected List<ConsumerZNode>    errorNodeList = new ArrayList<ConsumerZNode>();
    protected AtomicBoolean          stopflag;

    public FileChannelRunnable(Configuration conf, DataLoadingManager dataLoadingManager,
                               DefaultHDFSFileAdapter fileAdapter, AtomicBoolean stopflag) throws IOException{
        this.fileAdapter = fileAdapter;
        this.conf = conf;
        this.stopflag = stopflag;
        this.dataLoadingManager = dataLoadingManager;
    }

    @Override
    public void run() {
        while (!stopflag.get()) {
            ConsumerZNode currentNode = tryToMakeACurrentNode();
            if (currentNode != null) {
                ProtocolHead fileHead = DefaultHDFSFileAdapter.validataFileName(currentNode.getFileName());
                if (fileHead == null) {
                    if (LOG.isErrorEnabled()) {
                        LOG.error("validataFileName fail. fileName: " + currentNode.getFileName());
                    }
                } else {
                    MetaData metaData = null;
                    try {
                        // TODO: ERROR 操作失败 资源容易出问题
                        metaData = fileAdapter.read(fileHead, fs);
                    } catch (FileReadingException e) {
                        // 文件不存在、文件读取失败 => 跳过
                        // if (LOG.isWarnEnabled()) {
                        // LOG.warn(e);
                        // }
                        // TODO :
                    } catch (FileParsingException e) {
                        // 文件解析出错,退回重做
                        if (LOG.isErrorEnabled()) {
                            LOG.error("error while parsing file: " + currentNode.getFileName(), e);
                        }
                        try {
                            fileAdapter.reject(fileHead, fs);
                            errorNodeList.add(currentNode);
                        } catch (Exception e1) {
                            // 文件退回出错，只能重做了
                            if (LOG.isErrorEnabled()) {
                                LOG.error("reject file failed. file name" + currentNode.getFileName(), e);
                            }
                            String groupRoot = conf.get(ConsumerConstants.CONFKEY_REP_ZNODE_ROOT)
                                               + ConsumerConstants.FILE_SEPERATOR + currentNode.getGroupName();
                            String cur = groupRoot + ConsumerConstants.FILE_SEPERATOR + ConsumerConstants.ZK_CURRENT;
                            try {
                                // 清除zk上对此group的加锁
                                zoo.delete(cur, ConsumerConstants.ZK_ANY_VERSION);
                            } catch (Exception e2) {
                                if (LOG.isWarnEnabled()) {
                                    LOG.warn("error while deleting cur from zk . cur: " + cur, e);
                                }
                            }
                        }
                        // 跳出此次循环，尝试获取下一个consumerNode
                        continue;
                    }
                    if (metaData != null && metaData.getBody() != null) {
                        if (metaData.getBody() instanceof ProtocolBodyV1) {
                            ProtocolBodyV1 body = (ProtocolBodyV1) metaData.getBody();
                            if (MapUtils.isNotEmpty(body.getEditMap())) {
                                // 对于能够解析出body数据的进行加载
                                Map<String, List<Edit>> editMap = body.getEditMap();
                                for (String tableName : editMap.keySet()) {
                                    // 按表并发加载数据
                                    dataLoadingManager.batchLoad(tableName, editMap.get(tableName));
                                }
                                // 执行完成后清除相关文件
                                try {
                                    fileAdapter.clean(fileHead, fs);
                                } catch (IOException e) {
                                    if (LOG.isWarnEnabled()) {
                                        LOG.warn("clean failed.", e);
                                    }
                                }
                            }
                        }
                    }
                    // 清除zk上对此group的加锁
                    String groupRoot = conf.get(ConsumerConstants.CONFKEY_REP_ZNODE_ROOT)
                                       + ConsumerConstants.FILE_SEPERATOR + currentNode.getGroupName();
                    String cur = groupRoot + ConsumerConstants.FILE_SEPERATOR + ConsumerConstants.ZK_CURRENT;
                    try {
                        zoo.delete(cur, ConsumerConstants.ZK_ANY_VERSION);
                    } catch (Exception e) {
                        if (LOG.isWarnEnabled()) {
                            LOG.warn("delete completed consumerNode failed.cur: " + cur, e);
                        }
                    }
                }
            } else {
                // 没分配到任务的sleep再重试一段时间
                try {
                    Thread.sleep(ConsumerConstants.WAIT_MILLIS);
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
                String groupRoot = conf.get(ConsumerConstants.CONFKEY_REP_ZNODE_ROOT)
                                   + ConsumerConstants.FILE_SEPERATOR + node.getGroupName();
                String queue = groupRoot + ConsumerConstants.FILE_SEPERATOR + ConsumerConstants.ZK_QUEUE;
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
            groupList = zoo.getChildren(conf.get(ConsumerConstants.CONFKEY_REP_ZNODE_ROOT), false);
        } catch (Exception e) {
            if (LOG.isWarnEnabled()) {
                LOG.warn("no group found at zk. " + conf.get(ConsumerConstants.CONFKEY_REP_ZNODE_ROOT), e);
            }
        }
        if (CollectionUtils.isNotEmpty(groupList)) {
            for (String group : groupList) {
                String groupRoot = conf.get(ConsumerConstants.CONFKEY_REP_ZNODE_ROOT)
                                   + ConsumerConstants.FILE_SEPERATOR + group;
                String cur = groupRoot + ConsumerConstants.FILE_SEPERATOR + ConsumerConstants.ZK_CURRENT;
                String queue = groupRoot + ConsumerConstants.FILE_SEPERATOR + ConsumerConstants.ZK_QUEUE;
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
                    } catch (NodeExistsException e) {
                        continue;
                    } catch (Exception e) {
                        LOG.error("lock group error", e);
                    }
                    try {
                        statZkCur = zoo.exists(cur, false);
                        TreeSet<String> ftsSet = getQueueData(queue);
                        if (CollectionUtils.isEmpty(ftsSet)) {
                            continue;
                        }
                        String fileName = ftsSet.first();
                        ProtocolHead fileHead = DefaultHDFSFileAdapter.validataFileName(fileName);
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
                            zoo.delete(cur, ConsumerConstants.ZK_ANY_VERSION);
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
        ProtocolHead r = DefaultHDFSFileAdapter.validataFileName(retry);
        ProtocolHead o = DefaultHDFSFileAdapter.validataFileName(orig);
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
        Object ftsArray = oi.readObject();
        if (ftsArray == null) {
            return null;
        }
        TreeSet<String> ftsSet = new TreeSet<String>(new Comparator<String>() {

            @Override
            public int compare(String o1, String o2) {
                // 二次排序，优先fileTimestamp，然后是headTimestamp
                ProtocolHead o1Head = DefaultHDFSFileAdapter.validataFileName(o1);
                ProtocolHead o2Head = DefaultHDFSFileAdapter.validataFileName(o2);
                if (o1Head.getFileTimestamp() > o2Head.getFileTimestamp()) return 1;
                if (o1Head.getFileTimestamp() < o2Head.getFileTimestamp()) return -1;
                if (o1Head.getHeadTimestamp() > o2Head.getHeadTimestamp()) return 1;
                if (o1Head.getHeadTimestamp() < o2Head.getHeadTimestamp()) return -1;
                if (LOG.isWarnEnabled()) {
                    LOG.warn("same timestamp with " + o1 + " and " + o2);
                }
                return 0;
            }
        });
        for (String elem : (ArrayList<String>) ftsArray) {
            ftsSet.add(elem);
        }
        return ftsSet;
    }

}
