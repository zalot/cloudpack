package com.alibaba.hbase.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.HTablePool;
import org.apache.hadoop.hbase.client.Put;

/**
 * 集群基础测试类<BR>
 * 类BaseReplicationTest.java的实现描述：TODO 类实现描述
 * 
 * @author zalot.zhaoh Mar 5, 2012 6:19:57 PM
 */
public class HBaseTestBase {

    protected static Random              rnd = new Random();
    protected static HBaseTestingUtility util1;
    protected static Configuration       conf1;
    protected static HTablePool          pool1;

    protected static HBaseTestingUtility util2;
    protected static Configuration       conf2;
    protected static HTablePool          pool2;

    public static void init() throws Exception {
        init1();
        init2();
    }

    public static void init1() {
        conf1 = HBaseConfiguration.create();
        conf1.setBoolean("hbase.regionserver.info.port.auto", true);
        conf1.setLong("hbase.regionserver.hlog.blocksize", 1024 * 200);
        conf1.setInt("hbase.regionserver.maxlogs", 2);
        conf1.set(HConstants.ZOOKEEPER_ZNODE_PARENT, "/1");
        conf1.setBoolean("dfs.support.append", true);
        conf1.setInt("hbase.master.info.port", 60010);
    }

    public static void start1(int hbaseNum, int zkNum) throws Exception {
        util1 = new HBaseTestingUtility(conf1);
        util1.startMiniZKCluster(zkNum);
        if (hbaseNum > 0) util1.startMiniCluster(1, hbaseNum);
        pool1 = new HTablePool(conf1, 20);
    }

    public static void init2() {
        conf2 = HBaseConfiguration.create();
        conf2.set(HConstants.ZOOKEEPER_ZNODE_PARENT, "/2");
        conf2.setBoolean("dfs.support.append", true);
        conf2.setBoolean("hbase.regionserver.info.port.auto", true);
        conf2.setLong("hbase.regionserver.hlog.blocksize", 1024);
        conf2.setInt("hbase.master.info.port", 60011);
    }

    public static void start2(int hbaseNum, int zkNum) throws Exception {
        util2 = new HBaseTestingUtility(conf2);
        util2.startMiniZKCluster(zkNum);
        if (hbaseNum > 0) util2.startMiniCluster(1, hbaseNum);
        pool1 = new HTablePool(conf2, 20);
    }

    public static String getRndString(String table) {
        return table + UUID.randomUUID().toString().substring(0, 10);
    }

    public static void createTable(Configuration conf, String[] tables, String[] familys) throws IOException {
        HBaseAdmin admin = new HBaseAdmin(conf);
        for (String tableInfo : tables) {
            HTableDescriptor htableDes = new HTableDescriptor(tableInfo);
            for (String family : familys) {
                htableDes.addFamily(new HColumnDescriptor(family));
            }
            admin.createTable(htableDes);
        }
    }

    public static void insertRndData(HTablePool pool, String table, String family, String qualifier, int size)
                                                                                                              throws IOException {
        HTableInterface htable = pool.getTable(table);
        List<Put> puts = new ArrayList<Put>();
        for (int x = 0; x < size; x++) {
            Put put = new Put(getRndString(table).getBytes());
            put.add(family.getBytes(), qualifier.getBytes(), getRndString(null).getBytes());
            puts.add(put);
        }
        htable.put(puts);
        System.out.println("insert Data " + size);
    }

    public static void printDFS(FileSystem fs, Path path) throws IOException {
        if (fs.isFile(path)) {
            System.out.println(path.toString() + " - len = " + fs.getFileStatus(path).getLen());
        } else {
            for (FileStatus fss : fs.listStatus(path)) {
                printDFS(fs, fss.getPath());
            }
        }
    }
}
