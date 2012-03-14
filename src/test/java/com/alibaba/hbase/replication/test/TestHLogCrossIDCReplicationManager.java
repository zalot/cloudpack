package com.alibaba.hbase.replication.test;

import org.apache.hadoop.hbase.zookeeper.RecoverableZooKeeper;
import org.apache.hadoop.hbase.zookeeper.ZKUtil;
import org.junit.BeforeClass;
import org.junit.Test;

import com.alibaba.hbase.replication.hlog.HLogEntryZookeeperPersistence;
import com.alibaba.hbase.replication.hlog.HLogService;
import com.alibaba.hbase.replication.producer.HLogGroupZookeeperScanner;
import com.alibaba.hbase.replication.producer.crossidc.HBaseReplicationProducer;
import com.alibaba.hbase.replication.protocol.FileAdapter;
import com.alibaba.hbase.replication.zookeeper.NothingZookeeperWatch;

public class TestHLogCrossIDCReplicationManager extends BaseReplicationTest {

    @BeforeClass
    public static void init() throws Exception {
        init1();
        // init2();
    }

    @Test
    public void testReplicationRunInfomation() throws Exception {
        HLogService service = new HLogService(conf1);
        RecoverableZooKeeper zk = ZKUtil.connect(conf1, new NothingZookeeperWatch());

        HLogEntryZookeeperPersistence dao = new HLogEntryZookeeperPersistence(conf1, zk);
        FileAdapter ad = new FileAdapter();
        ad.init(conf1);

        HLogGroupZookeeperScanner scan = new HLogGroupZookeeperScanner(conf1);
        scan.setHlogEntryPersistence(dao);
        scan.setHlogService(service);
        scan.setZooKeeper(zk);

        Thread threadscan = new Thread(scan);
        threadscan.start();

        HBaseReplicationProducer pro = new HBaseReplicationProducer(conf1);
        pro.setAdapter(ad);
        pro.setHlogEntryPersistence(dao);
        pro.setHlogService(service);

        Thread threadpro = new Thread(pro);
        threadpro.start();

        while (true) {
            insertData(pool1, TABLEA, COLA, "test", 1000);
            printDFS(service.getFileSystem(), service.getHBaseRootDir().getParent());
            Thread.sleep(10000);
        }
    }
}
