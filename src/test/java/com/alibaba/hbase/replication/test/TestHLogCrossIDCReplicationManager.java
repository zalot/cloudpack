package com.alibaba.hbase.replication.test;

import org.apache.hadoop.hbase.zookeeper.RecoverableZooKeeper;
import org.apache.hadoop.hbase.zookeeper.ZKUtil;
import org.junit.BeforeClass;
import org.junit.Test;

import com.alibaba.hbase.replication.hlog.DefaultHLogOperator;
import com.alibaba.hbase.replication.protocol.FileAdapter;
import com.alibaba.hbase.replication.zookeeper.HLogZookeeperPersistence;
import com.alibaba.hbase.replication.zookeeper.NothingZookeeperWatch;

public class TestHLogCrossIDCReplicationManager extends BaseReplicationTest {

    @BeforeClass
    public static void init() throws Exception {
        init1();
        // init2();
    }

    @Test
    public void testReplication() throws Exception {
        DefaultHLogOperator operator = new DefaultHLogOperator(conf1);

        RecoverableZooKeeper zk = ZKUtil.connect(conf1, new NothingZookeeperWatch());
        HLogZookeeperPersistence dao = new HLogZookeeperPersistence(conf1);

        FileAdapter ad = new FileAdapter();
        ad.init(conf1);

        while (true) {
            insertData(pool1, TABLEA, COLA, "test", 1000);
            printDFS(operator.getFileSystem(), operator.getRootDir().getParent());
            Thread.sleep(10000);
        }
    }
}
