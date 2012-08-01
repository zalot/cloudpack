package com.alibaba.hbase.exec.alireplication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;

import org.sourceopen.hadoop.hbase.replication.utility.ProducerConstants;

/**
 * 用于压力测试
 * 
 * 类WriteDataToProducer.java的实现描述：TODO 类实现描述 
 * @author zalot.zhaoh Mar 27, 2012 11:33:16 AM
 */
public class WriteDataToProducer {

    public static void main(String[] args) throws IOException {
        Configuration conf = HBaseConfiguration.create();
        conf.addResource(ProducerConstants.COMMON_CONFIG_FILE);
        conf.addResource(ProducerConstants.PRODUCER_CONFIG_FILE);
        HTable htable = new HTable(conf, args[0]);
        Put put;
        byte[] family = Bytes.toBytes(args[1]);
        byte[] qualifier = Bytes.toBytes(args[2]);
        Random rnd = new Random();
        int count = Integer.parseInt(args[3]);
        int curCount = 0;
        while (true) {
            int rndInt = rnd.nextInt(1000);
            curCount = curCount + rndInt;
            if (curCount > count) break;
            List<Put> ls = new ArrayList<Put>();
            for (int x = 0; x < rndInt; x++) {
                put = new Put(UUID.randomUUID().toString().getBytes());
                put.add(family, qualifier, Bytes.toBytes(UUID.randomUUID().toString()));
                ls.add(put);
            }
            htable.put(ls);
        }
    }
}
