/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.hbase.replication.consumer;

import java.util.UUID;

/**
 * 类Constant.java的实现描述：常量数据
 * 
 * @author dongsh 2012-2-29 上午10:25:47
 */
public class Constants {

    // common
    public static final String FILE_SEPERATOR   = "/";
    public static final String CHANNEL_NAME     = "channel_";
    // slave集群数据特有的clusterID
    public static final UUID   SLAVE_CLUSTER_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    public static final int    WAIT_MILLIS        = 500;

    // conf key
    public static final String REP_FILE_DIR     = "replication.file.dir";
    public static final String PRODUCER_FS      = "replication.producer.fs";
    public static final String ZK_QUORUM        = "replication.consumer.zookeeper.quorum";
    public static final String REP_ZNODE_ROOT   = "replication.consumer.zookeeper.znoderoot";

    // zk node
    public static final String ZK_CURRENT       = "cur";
    public static final String ZK_QUEUE         = "queue";
    public static final int    ZK_ANY_VERSION   = -1;

}
