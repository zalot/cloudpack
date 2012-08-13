package com.alibaba.hbase.test.alireplication;

import junit.framework.Assert;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.junit.BeforeClass;
import org.junit.Test;

import org.sourceopen.TestBase;
import org.sourceopen.hadoop.hbase.replication.core.HBaseService;
import org.sourceopen.hadoop.hbase.replication.producer.HLogScanner;
import org.sourceopen.hadoop.hbase.replication.producer.ReplicationTransfer;
import org.sourceopen.hadoop.hbase.replication.producer.RejectRecoverScanner;
import org.sourceopen.hadoop.hbase.replication.producer.v2.ZNodeHLogPersistence;
import org.sourceopen.hadoop.hbase.replication.protocol.HDFSFileAdapter;
import org.sourceopen.hadoop.hbase.replication.protocol.ProtocolHead;
import org.sourceopen.hadoop.hbase.replication.utility.ZKUtil;
import org.sourceopen.hadoop.zookeeper.connect.NothingZookeeperWatch;
import org.sourceopen.hadoop.zookeeper.connect.RecoverableZooKeeper;

/**
 * 主流成测试 类TestMain.java的实现描述：TODO 类实现描述
 * 
 * @author zalot.zhaoh Mar 19, 2012 11:26:29 AM
 */
public class TestMain extends TestBase {

    @BeforeClass
    public static void init() throws Exception {
        init();
        startHBaseClusterA(3, 3);
        createDefTable(_confA);
    }

    public void testReplicationRunInfomation() throws Exception {
        HBaseService service = new HBaseService(_confA);
        RecoverableZooKeeper zk = ZKUtil.connect(_confA, new NothingZookeeperWatch());

        ZNodeHLogPersistence dao = new ZNodeHLogPersistence(_confA, zk);
        HDFSFileAdapter ad = new HDFSFileAdapter();
        ad.init(_confA);

        HLogScanner scan = new HLogScanner(_confA);
        scan.setHLogPersistence(dao);
        scan.setHBaseService(service);
        scan.setZooKeeper(zk);

        Thread threadscan = new Thread(scan);
        threadscan.start();

        ReplicationTransfer pro = new ReplicationTransfer(_confA);
        pro.setAdapter(ad);
        pro.setHLogPersistence(dao);
        pro.setHBaseService(service);

        Thread threadpro = new Thread(pro);
        threadpro.start();

        while (true) {
            insertRndData(_poolA, "testA", "colA", "test", 1000);
            printDFS(service.getFileSystem(), service.getHBaseRootDir().getParent());
            Thread.sleep(10000);
        }
    }

    @Test
    public void testReplicationAndDFSFileAdapter() throws Exception {
        HBaseService service = new HBaseService(_confA);
        RecoverableZooKeeper zookeeper = ZKUtil.connect(_confA, new NothingZookeeperWatch());

        ZNodeHLogPersistence dao = new ZNodeHLogPersistence(_confA, zookeeper);
        dao.setZookeeper(zookeeper);

        HDFSFileAdapter adapter = new HDFSFileAdapter();
        adapter.init(_confA);

        HLogScanner scan = new HLogScanner(_confA);
        scan.setHLogPersistence(dao);
        scan.setHBaseService(service);
        scan.setZooKeeper(zookeeper);

        ReplicationTransfer producer = new ReplicationTransfer(_confA);
        producer.setAdapter(adapter);
        producer.setHLogPersistence(dao);
        producer.setHBaseService(service);

        RejectRecoverScanner recover = new RejectRecoverScanner(_confA);
        recover.setHBaseService(service);
        recover.setZooKeeper(zookeeper);
        recover.setAdapter(adapter);

        // insert and run
        insertRndData(_poolA, "testA", "colA", "test", 1000);
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
