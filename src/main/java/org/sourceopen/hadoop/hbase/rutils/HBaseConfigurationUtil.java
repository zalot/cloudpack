package org.sourceopen.hadoop.hbase.rutils;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.zookeeper.ZKConfig;

public class HBaseConfigurationUtil {

    public static String getZooKeeperURL(Configuration conf) {
        return ZKConfig.getZKQuorumServersString(conf);
    }
}
