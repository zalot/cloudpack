package org.sourceopen.hadoop.hbase.utils;

import java.io.IOException;
import java.util.Properties;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.zookeeper.ZKConfig;
import org.apache.zookeeper.Watcher;
import org.sourceopen.hadoop.hbase.replication.utility.ProducerConstants;
import org.sourceopen.hadoop.zookeeper.connect.AdvZooKeeper;
import org.sourceopen.hadoop.zookeeper.connect.ZookeeperFactory;
import org.sourceopen.hadoop.zookeeper.core.ZNode;
import org.sourceopen.hadoop.zookeeper.core.ZNodeFactory;

public class HRepConfigUtil {

    public static void setProducerConfig(Configuration confA, Configuration confB, Configuration producerConfiguration) {
        producerConfiguration.set("fs.default.name", confA.get("fs.default.name"));
        producerConfiguration.set(ProducerConstants.CONFKEY_ROOT_HBASE_HDFS, confA.get("hbase.rootdir"));
        producerConfiguration.set("hbase.zookeeper.quorum", confA.get("hbase.zookeeper.quorum"));
        producerConfiguration.set("hbase.zookeeper.property.clientPort",
                                  confA.get("hbase.zookeeper.property.clientPort"));
        producerConfiguration.set("org.sourceopen.hadoop.hbase.replication.protocol.adapter.hdfs.fs",
                                  confB.get("fs.default.name"));
    }

    public static String getZKStringV1(Configuration conf) {
        return ZKConfig.getZKQuorumServersString(conf);
    }

    public static String getZkStringV2(Configuration conf) {
        String quorum = conf.get("hbase.zookeeper.quorum");
        String clientPort = conf.get("hbase.zookeeper.property.clientPort");
        return quorum + ":" + clientPort;
    }

    public static AdvZooKeeper createAdvZooKeeperByHBaseConfig(Configuration conf, Watcher watcher) throws Exception {
        Properties properties = ZKConfig.makeZKProps(conf);
        String ensemble = ZKConfig.getZKQuorumServersString(properties);
        if (ensemble == null) {
            throw new IOException("Unable to determine ZooKeeper ensemble");
        }
        int timeout = conf.getInt(HConstants.ZK_SESSION_TIMEOUT, HConstants.DEFAULT_ZK_SESSION_TIMEOUT);
        int retry = conf.getInt("zookeeper.recovery.retry", 3);
        int rim = conf.getInt("zookeeper.recovery.retry.intervalmill", 1000);
        return ZookeeperFactory.createRecoverableZooKeeper(ensemble, timeout, watcher, retry, rim);
    }

    public static ZNode getRootZNode(Configuration conf) {
        String root = conf.get(ProducerConstants.CONFKEY_ROOT_ZOO, ProducerConstants.ROOT_ZOO);
        ZNode znode = new ZNode(root);
        return znode;
    }
}
