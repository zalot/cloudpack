package com.alibaba.hbase.replication.test;

import org.apache.hadoop.hbase.zookeeper.RecoverableZooKeeper;
import org.apache.hadoop.hbase.zookeeper.ZKUtil;
import org.junit.BeforeClass;
import org.junit.Test;

import com.alibaba.hbase.replication.hlog.DefaultHLogOperator;
import com.alibaba.hbase.replication.hlog.HLogOperator;
import com.alibaba.hbase.replication.producer.CrossIDCHBaseReplicationSink;
import com.alibaba.hbase.replication.producer.HLogGroupZookeeperScanner;
import com.alibaba.hbase.replication.protocol.FileAdapter;
import com.alibaba.hbase.replication.zookeeper.HLogZookeeperPersistence;
import com.alibaba.hbase.replication.zookeeper.ReplicationZookeeperWatch;

public class TestHLogCrossIDCReplicationManager extends BaseReplicationTest {

    @BeforeClass
    public static void init() throws Exception {
        init1();
//         init2();
    }

    @Test
    public void testReplication() throws Exception {
        HLogOperator operator = new DefaultHLogOperator(conf1);
        
        RecoverableZooKeeper zk = ZKUtil.connect(conf1, new ReplicationZookeeperWatch());
        HLogZookeeperPersistence dao = new HLogZookeeperPersistence();
        dao.setZookeeper(zk);
        dao.init(conf1);
        
        FileAdapter ad = new FileAdapter();
        ad.init(conf1);
        
        HLogGroupZookeeperScanner scan = new HLogGroupZookeeperScanner(conf1, operator.getFileSystem(), dao);
        scan.start();
        
        CrossIDCHBaseReplicationSink sink  = new CrossIDCHBaseReplicationSink(dao, operator, ad);
        sink.start();
        
        while(true){
            insertData(pool1, TABLEA, COLA, "test", 1000);
            printDFS(operator.getFileSystem(), operator.getRootDir().getParent());
            Thread.sleep(10000);
        }
    }
}
