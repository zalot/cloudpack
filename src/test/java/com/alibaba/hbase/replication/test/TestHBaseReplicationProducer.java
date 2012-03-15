package com.alibaba.hbase.replication.test;

import junit.framework.Assert;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.junit.BeforeClass;
import org.junit.Test;

import com.alibaba.hbase.replication.hlog.HLogEntryZookeeperPersistence;
import com.alibaba.hbase.replication.hlog.HLogService;
import com.alibaba.hbase.replication.producer.HLogGroupZookeeperScanner;
import com.alibaba.hbase.replication.producer.crossidc.HReplicationProducer;
import com.alibaba.hbase.replication.producer.crossidc.HReplicationRejectRecoverScanner;
import com.alibaba.hbase.replication.protocol.DefaultHDFSFileAdapter;
import com.alibaba.hbase.replication.protocol.Head;
import com.alibaba.hbase.replication.utility.ZKUtil;
import com.alibaba.hbase.replication.zookeeper.NothingZookeeperWatch;
import com.alibaba.hbase.replication.zookeeper.RecoverableZooKeeper;

public class TestHBaseReplicationProducer extends BaseReplicationTest {

    @BeforeClass
    public static void init() throws Exception {
        init1();
        // init2();
    }

    public void testReplicationRunInfomation() throws Exception {
        HLogService service = new HLogService(conf1);
        RecoverableZooKeeper zk = ZKUtil.connect(conf1, new NothingZookeeperWatch());

        HLogEntryZookeeperPersistence dao = new HLogEntryZookeeperPersistence(conf1, zk);
        DefaultHDFSFileAdapter ad = new DefaultHDFSFileAdapter();
        ad.init(conf1);

        HLogGroupZookeeperScanner scan = new HLogGroupZookeeperScanner(conf1);
        scan.setHlogEntryPersistence(dao);
        scan.setHlogService(service);
        scan.setZooKeeper(zk);

        Thread threadscan = new Thread(scan);
        threadscan.start();

        HReplicationProducer pro = new HReplicationProducer(conf1);
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

    @Test
    public void testReplicationRunInfomation2() throws Exception {
        HLogService service = new HLogService(conf1);
        RecoverableZooKeeper zk = ZKUtil.connect(conf1, new NothingZookeeperWatch());

        HLogEntryZookeeperPersistence dao = new HLogEntryZookeeperPersistence(conf1, zk);
        DefaultHDFSFileAdapter ad = new DefaultHDFSFileAdapter();
        ad.init(conf1);

        HLogGroupZookeeperScanner scan = new HLogGroupZookeeperScanner(conf1);
        scan.setHlogEntryPersistence(dao);
        scan.setHlogService(service);
        scan.setZooKeeper(zk);

        HReplicationProducer producer = new HReplicationProducer(conf1);
        producer.setAdapter(ad);
        producer.setHlogEntryPersistence(dao);
        producer.setHlogService(service);

        HReplicationRejectRecoverScanner recover = new HReplicationRejectRecoverScanner(conf1);
        recover.setHlogService(service);
        recover.setZooKeeper(zk);
        recover.setAdapter(ad);
        
        // insert and run
        insertData(pool1, TABLEA, COLA, "test", 1000);
        scan.doScan();
        producer.doProducer();

        // check
        FileStatus[] fss = service.getFileSystem().listStatus(ad.getTargetPath());
        FileStatus file = null;
        Assert.assertTrue(fss != null && fss.length >= 1);
        for (FileStatus fs : fss) {
            if (!fs.isDir()) {
                file = fs;
                break;
            }
        }

        Assert.assertTrue(file != null);

        Path firstTarget = file.getPath();
        long firstSize = file.getLen();
        Assert.assertTrue(firstSize > 0);

        FileStatus[] rejFss = service.getFileSystem().listStatus(ad.getRejectPath());
        Assert.assertTrue(rejFss.length == 0);
        
        Head head = ad.validataFileName(firstTarget.getName());
        ad.reject(head, service.getFileSystem());

        fss = service.getFileSystem().listStatus(ad.getTargetPath());
        for (FileStatus fs : fss) {
            if (!fs.isDir()) {
                Assert.assertTrue(false);
            }
        }
        
        rejFss = service.getFileSystem().listStatus(ad.getRejectPath());
        Assert.assertTrue(rejFss.length > 0);
        
        recover.doRecover();
        
        fss = service.getFileSystem().listStatus(ad.getTargetPath());
        Assert.assertTrue(fss.length > 0);
    }
}
