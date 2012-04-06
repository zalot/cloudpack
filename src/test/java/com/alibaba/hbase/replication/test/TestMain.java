package com.alibaba.hbase.replication.test;

import junit.framework.Assert;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.junit.BeforeClass;
import org.junit.Test;

import com.alibaba.hbase.replication.hlog.HLogEntryPoolZookeeperPersistence;
import com.alibaba.hbase.replication.hlog.HLogService;
import com.alibaba.hbase.replication.producer.HLogGroupZookeeperScanner;
import com.alibaba.hbase.replication.producer.crossidc.HReplicationProducer;
import com.alibaba.hbase.replication.producer.crossidc.HReplicationRejectRecoverScanner;
import com.alibaba.hbase.replication.protocol.HDFSFileAdapter;
import com.alibaba.hbase.replication.protocol.ProtocolHead;
import com.alibaba.hbase.replication.utility.ZKUtil;
import com.alibaba.hbase.replication.zookeeper.NothingZookeeperWatch;
import com.alibaba.hbase.replication.zookeeper.RecoverableZooKeeper;

/**
 * 主流成测试
 * 
 * 类TestMain.java的实现描述：TODO 类实现描述 
 * @author zalot.zhaoh Mar 19, 2012 11:26:29 AM
 */
public class TestMain extends TestBase {

    @BeforeClass
    public static void init() throws Exception {
        init1();
        // init2();
    }

    public void testReplicationRunInfomation() throws Exception {
        HLogService service = new HLogService(conf1);
        RecoverableZooKeeper zk = ZKUtil.connect(conf1, new NothingZookeeperWatch());

        HLogEntryPoolZookeeperPersistence dao = new HLogEntryPoolZookeeperPersistence(conf1, zk);
        HDFSFileAdapter ad = new HDFSFileAdapter();
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
    public void testReplicationAndDFSFileAdapter() throws Exception {
        HLogService service = new HLogService(conf1);
        RecoverableZooKeeper zookeeper = ZKUtil.connect(conf1, new NothingZookeeperWatch());

        HLogEntryPoolZookeeperPersistence dao = new HLogEntryPoolZookeeperPersistence(conf1, zookeeper);
        dao.setZookeeper(zookeeper);
        
        HDFSFileAdapter adapter = new HDFSFileAdapter();
        adapter.init(conf1);

        HLogGroupZookeeperScanner scan = new HLogGroupZookeeperScanner(conf1);
        scan.setHlogEntryPersistence(dao);
        scan.setHlogService(service);
        scan.setZooKeeper(zookeeper);

        HReplicationProducer producer = new HReplicationProducer(conf1);
        producer.setAdapter(adapter);
        producer.setHlogEntryPersistence(dao);
        producer.setHlogService(service);

        HReplicationRejectRecoverScanner recover = new HReplicationRejectRecoverScanner(conf1);
        recover.setHlogService(service);
        recover.setZooKeeper(zookeeper);
        recover.setAdapter(adapter);
        
        
        
        // insert and run
        insertData(pool1, TABLEA, COLA, "test", 1000);
        scan.doScan();
        producer.doProducer();

        // check
        FileStatus[] fss = service.getFileSystem().listStatus(adapter.getTargetPath());
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

        FileStatus[] rejFss = service.getFileSystem().listStatus(adapter.getRejectPath());
        Assert.assertTrue(rejFss.length == 0);
        
        ProtocolHead head = adapter.validataFileName(firstTarget.getName());
        adapter.reject(head);

        fss = service.getFileSystem().listStatus(adapter.getTargetPath());
        for (FileStatus fs : fss) {
            if (!fs.isDir()) {
                Assert.assertTrue(false);
            }
        }
        
        rejFss = service.getFileSystem().listStatus(adapter.getRejectPath());
        Assert.assertTrue(rejFss.length > 0);
        
        recover.doRecover();
        
        fss = service.getFileSystem().listStatus(adapter.getTargetPath());
        Assert.assertTrue(fss.length > 0);
    }
}
