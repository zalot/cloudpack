package com.alibaba.hbase.replication.utility;

import java.io.IOException;
import java.util.Properties;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.zookeeper.ZKConfig;
import org.apache.zookeeper.Watcher;

import com.alibaba.hbase.replication.zookeeper.RecoverableZooKeeper;

/**
 * 类ZKUtil.java的实现描述：TODO 类实现描述 
 * @author zalot.zhaoh Mar 16, 2012 11:07:50 AM
 */
public class ZKUtil {

    public static RecoverableZooKeeper connect(Configuration conf, Watcher watcher) throws IOException {
        Properties properties = ZKConfig.makeZKProps(conf);
        String ensemble = ZKConfig.getZKQuorumServersString(properties);
        return connect(conf, ensemble, watcher);
    }

    public static RecoverableZooKeeper connect(Configuration conf, String ensemble, Watcher watcher) throws IOException {
        return connect(conf, ensemble, watcher, "");
    }

    public static RecoverableZooKeeper connect(Configuration conf, String ensemble, Watcher watcher,
                                               final String descriptor) throws IOException {
        if (ensemble == null) {
            throw new IOException("Unable to determine ZooKeeper ensemble");
        }
        int timeout = conf.getInt("zookeeper.session.timeout", 180 * 1000);
        int retry = conf.getInt("zookeeper.recovery.retry", 3);
        int retryIntervalMillis = conf.getInt("zookeeper.recovery.retry.intervalmill", 1000);
        int zkDumpConnectionTimeOut = conf.getInt("zookeeper.dump.connection.timeout", 1000);
        return new RecoverableZooKeeper(ensemble, timeout, watcher, retry, retryIntervalMillis);
    }
}
