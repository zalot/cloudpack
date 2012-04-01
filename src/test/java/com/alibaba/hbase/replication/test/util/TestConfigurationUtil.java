package com.alibaba.hbase.replication.test.util;

import org.apache.hadoop.conf.Configuration;

import com.alibaba.hbase.replication.server.ReplicationConf;
import com.alibaba.hbase.replication.utility.ProducerConstants;

/**
 * 类TestConfigurationUtil.java的实现描述：TODO 类实现描述 
 * @author zalot.zhaoh Apr 1, 2012 11:37:56 AM
 */
public class TestConfigurationUtil {

    public static void setProducer(Configuration confA, Configuration confB, ReplicationConf confProducer) {
        confProducer.set("fs.default.name", confA.get("fs.default.name"));
        confProducer.set(ProducerConstants.CONFKEY_HDFS_HBASE_ROOT, confA.get("hbase.rootdir"));
        confProducer.set("hbase.zookeeper.quorum", confA.get("hbase.zookeeper.quorum"));
        confProducer.set("hbase.zookeeper.property.clientPort", confA.get("hbase.zookeeper.property.clientPort"));
        confProducer.set("com.alibaba.hbase.replication.protocol.adapter.hdfs.fs", confB.get("fs.default.name"));
    }

    public static String getZkString(Configuration conf) {
        String quorum = conf.get("hbase.zookeeper.quorum");
        String clientPort = conf.get("hbase.zookeeper.property.clientPort");
        return quorum + ":" + clientPort;
    }
}
