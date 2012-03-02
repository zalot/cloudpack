package com.alibaba.hbase.replication.test;

import java.util.Random;
import java.util.UUID;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTablePool;

public class BaseReplicationTest {

    protected static final int           zkClusterCount    = 2;
    protected static final int           hbaseClusterCount = 2;

    protected static HBaseTestingUtility util1;
    protected static Configuration       conf1;
    protected static HTablePool          pool1;

    protected static HBaseTestingUtility util2;
    protected static Configuration       conf2;
    protected static HTablePool          pool2;

    protected static final String        TABLEA            = "testTableA";
    protected static final String        TABLEB            = "testTableB";
    protected static final String        TABLEC            = "testTableC";
    protected static final String        COLA              = "colA";
    protected static final String        COLB              = "colB";

    public static void createTable(Configuration conf) throws Exception {
        HBaseAdmin admin = new HBaseAdmin(conf);
        HTableDescriptor htable = new HTableDescriptor(TABLEA);
        HColumnDescriptor hcolumnA = new HColumnDescriptor(COLA);
        HColumnDescriptor hcolumnB = new HColumnDescriptor(COLB);
        htable.addFamily(hcolumnA);
        htable.addFamily(hcolumnB);
        admin.createTable(htable);

        htable = new HTableDescriptor(TABLEB);
        htable.addFamily(hcolumnA);
        htable.addFamily(hcolumnB);
        admin.createTable(htable);

        htable = new HTableDescriptor(TABLEC);
        htable.addFamily(hcolumnA);
        htable.addFamily(hcolumnB);
        admin.createTable(htable);
        admin.close();
    }

    public static void init1() throws Exception {
        conf1 = HBaseConfiguration.create();
        conf1.setBoolean("hbase.regionserver.info.port.auto", true);
        conf1.setLong("hbase.regionserver.hlog.blocksize", 1024);
        conf1.setInt("hbase.regionserver.maxlogs", 2);
        conf1.set(HConstants.ZOOKEEPER_ZNODE_PARENT, "/1");
        conf1.setBoolean("dfs.support.append", true);
        conf1.setInt("hbase.master.info.port", 60010);

        util1 = new HBaseTestingUtility(conf1);
        util1.startMiniZKCluster(zkClusterCount);
        util1.startMiniCluster(1, hbaseClusterCount);
        createTable(conf1);
        pool1 = new HTablePool(conf1, 10);
    }

    public static void init2() throws Exception {

        conf2 = HBaseConfiguration.create(conf1);
        conf2.set(HConstants.ZOOKEEPER_ZNODE_PARENT, "/2");
        conf2.setBoolean("dfs.support.append", true);
        conf2.setBoolean("hbase.regionserver.info.port.auto", true);
        conf2.setLong("hbase.regionserver.hlog.blocksize", 1024);
        conf2.setInt("hbase.master.info.port", 60011);

        util2 = new HBaseTestingUtility(conf2);
        util2.startMiniZKCluster(zkClusterCount);
        util2.startMiniCluster(1, hbaseClusterCount);
        pool2 = new HTablePool(conf2, 10);

        Configuration conf1to2 = HBaseConfiguration.create(conf2);
        conf1to2.set("hbase.rootdir", conf1.get("hbase.rootdir")); // 读取 1
        conf1to2.set("fs.default.name", conf1.get("fs.default.name")); // 读取 1
        conf1to2.set("hbase.zookeeper.quorum", conf2.get("hbase.zookeeper.quorum"));
        conf1to2.set("zookeeper.znode.parent", conf2.get("zookeeper.znode.parent"));
        conf1to2.set("hbase.master.port", conf2.get("hbase.master.port"));
    }

    protected Random rnd = new Random();

    protected String getRndString(String table) {
        return table + UUID.randomUUID().toString().substring(0, 10);
    }
}
