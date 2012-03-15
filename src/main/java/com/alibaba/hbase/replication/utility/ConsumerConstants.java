/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.hbase.replication.utility;

import java.util.UUID;

import org.apache.hadoop.hbase.HConstants;

/**
 * 类Constant.java的实现描述：常量数据
 * 
 * @author dongsh 2012-2-29 上午10:25:47
 */
public class ConsumerConstants {

    // common
    public static final String FILE_SEPERATOR                      = "/";
    public static final String CHANNEL_NAME                        = "channel_";
    public static final String CONSUMER_CONFIG_FILE                = "META-INF/consumer-configuration.xml";

    // slave集群数据特有的clusterID
    public static final UUID   SLAVE_CLUSTER_ID                    = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    public static final int    WAIT_MILLIS                         = 500;
    public static final String MD5_DIR                             = "md5";
    public static final String MD5_SUFFIX                          = "_md5";

    // conf key
    public static final String CONFKEY_PRODUCER_FS                 = "com.alibaba.hbase.replication.producer.fs";
    public static final String CONFKEY_ZK_QUORUM                   = "com.alibaba.hbase.replication.consumer.zookeeper.quorum";
    public static final String CONFKEY_REP_ZNODE_ROOT              = "com.alibaba.hbase.replication.consumer.zookeeper.znoderoot";
    public static final String CONFKEY_REP_FILE_CHANNEL_POOL_SIZE  = "com.alibaba.hbase.replication.consumer.fileChannelPoolSize";
    public static final String CONFKEY_REP_DATA_LAODING_POOL_SIZE  = "com.alibaba.hbase.replication.consumer.dataLoadingPoolSize";
    public static final String CONFKEY_REP_DATA_LAODING_BATCH_SIZE = "com.alibaba.hbase.replication.consumer.dataLoadingBatchSize";
    public static final String CONFKEY_THREADPOOL_SIZE             = "com.alibaba.hbase.replication.consumer.threadpool.queuesize";
    public static final String CONFKEY_THREADPOOL_KEEPALIVE_TIME   = "com.alibaba.hbase.replication.consumer.threadpool.keepAliveTime";
    public static final String CONFKEY_TMPFILE_TARGETPATH          = "com.alibaba.hbase.replication.tmpfile.targetPath";
    public static final String TMPFILE_TARGETPATH                  = "/targetsources";
    public static final String CONFKEY_TMPFILE_TARGETTMPPATH       = "com.alibaba.hbase.replication.tmpfile.targetTmpPath";
    public static final String TMPFILE_TARGETTMPPATH               = "/tmptargetsources";
    public static final String CONFKEY_TMPFILE_OLDPATH             = "com.alibaba.hbase.replication.tmpfile.oldPath";
    public static final String TMPFILE_OLDPATH                     = "/oldtargetsources";
    public static final String CONFKEY_TMPFILE_REJECTPATH          = "com.alibaba.hbase.replication.tmpfile.rejectPath";
    public static final String TMPFILE_REJECTPATH                  = "/rejecttargetsrouces";

    // zk node
    public static final String ZK_CURRENT                          = "cur";
    public static final String ZK_QUEUE                            = "queue";
    public static final int    ZK_ANY_VERSION                      = -1;

}
