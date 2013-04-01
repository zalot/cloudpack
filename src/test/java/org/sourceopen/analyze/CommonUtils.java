package org.sourceopen.analyze;

import org.apache.hadoop.conf.Configuration;

public class CommonUtils {

    public static Configuration getH0ClusterConfiguration() {
        Configuration conf = new Configuration();
        conf.set("fs.default.name", "hdfs://h0:9100");
        return conf;
    }
}
