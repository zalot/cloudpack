package com.alibaba.hbase.replication.producer;

import java.util.UUID;

import org.apache.hadoop.hbase.HConstants;

public class UuidService {

    public static UUID getLocalUUID() {
        return HConstants.DEFAULT_CLUSTER_ID;
    }
}
