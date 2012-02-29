/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.hbase.replication.consumer;

/**
 * 类Constant.java的实现描述：常量数据
 * 
 * @author dongsh 2012-2-29 上午10:25:47
 */
public class Constants {

    // common
    public static final String FILE_SEPERATOR = "/";
    public static final String CHANNEL_NAME   = "channel_";

    // conf key
    public static final String REP_FILE_DIR   = "replication.file.dir";
    public static final String PRODUCER_FS    = "replication.producer.fs";
    public static final String ZK_QUORUM      = "replication.consumer.zookeeper.quorum";
    public static final String REP_ZNODE_ROOT = "replication.consumer.zookeeper.znoderoot";

    // zk node
    public static final String ZK_CURRENT     = "cur";
    public static final String ZK_QUEUE       = "queue";
    public static final int    ZK_ANY_VERSION = -1;

}
