package com.alibaba.hbase.test.alireplication;

import java.io.IOException;
import java.util.Random;

import junit.framework.Assert;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.zookeeper.KeeperException;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.sourceopen.TestBase;
import org.sourceopen.hadoop.hbase.replication.hlog.HLogEntryPoolZookeeperPersistence;
import org.sourceopen.hadoop.hbase.replication.hlog.HLogService;
import org.sourceopen.hadoop.hbase.replication.hlog.domain.HLogEntryGroup;
import org.sourceopen.hadoop.hbase.replication.hlog.domain.HLogEntryGroups;
import org.sourceopen.hadoop.hbase.replication.producer.HLogGroupZookeeperScanner;
import org.sourceopen.hadoop.hbase.replication.utility.HLogUtil;
import org.sourceopen.hadoop.hbase.replication.utility.ProducerConstants;
import org.sourceopen.hadoop.hbase.replication.utility.ZKUtil;
import org.sourceopen.hadoop.zookeeper.connect.NothingZookeeperWatch;
import org.sourceopen.hadoop.zookeeper.connect.RecoverableZooKeeper;

/**
 * 类TestHLogScanner.java的实现描述：TODO 类实现描述
 * 
 * @author zalot.zhaoh Mar 22, 2012 1:50:42 PM
 */
public class TestHLogScanner extends TestBase {

    public final static int regionServerNum = 3;

    @BeforeClass
    public static void init() throws Exception {
        initClusterA();
        startHBaseClusterA(regionServerNum, 3);
        createTable(_confA, new String[] {}, new String[] {});
    }

    @Test
    public void testHLogGroupScan() throws Exception {
        HLogService service = new HLogService(_confA);

        HLogGroupZookeeperScanner scan;
        RecoverableZooKeeper zk1 = ZKUtil.connect(_confA, new NothingZookeeperWatch());
        HLogEntryPoolZookeeperPersistence dao1 = new HLogEntryPoolZookeeperPersistence(_confA, zk1);
        dao1.setZookeeper(zk1);
        dao1.init(_confA);

        FileSystem fs = _util1.getTestFileSystem();
        scan = new HLogGroupZookeeperScanner(_confA);
        scan.setHlogEntryPersistence(dao1);
        scan.setHlogService(service);
        scan.setZooKeeper(zk1);

        int count = 0;
        while (true) {
            insertRndData(_poolA, "testA", "colA", "test", rnd.nextInt(1000));
            Thread.sleep(10000 * 2);
            HLogEntryGroups groups = new HLogEntryGroups();
            groups.put(HLogUtil.getHLogsByHDFS(fs, service.getHLogDir()));
            groups.put(HLogUtil.getHLogsByHDFS(fs, service.getOldHLogDir()));
            Assert.assertTrue(dao1.listGroupName().size() == groups.getGroups().size());
            for (HLogEntryGroup group : groups.getGroups()) {
                Assert.assertTrue(dao1.listEntry(group.getGroupName()).size() == group.getEntrys().size());
            }
            System.out.println("checkok -- " + count + " groups [ " + groups.getGroups().size() + " ] ");
            if (count > 10) return;
            count++;
        }
    }

    @Test
    public void testMThreadScan() throws Exception {
        HLogService service = new HLogService(_confA);
        final HLogGroupZookeeperScanner scan1;
        final HLogGroupZookeeperScanner scan2;

        RecoverableZooKeeper zk1 = ZKUtil.connect(_confA, new NothingZookeeperWatch());
        final HLogEntryPoolZookeeperPersistence dao1 = new HLogEntryPoolZookeeperPersistence(_confA, zk1);
        dao1.setZookeeper(zk1);
        dao1.init(_confA);

        RecoverableZooKeeper zk2 = ZKUtil.connect(_confA, new NothingZookeeperWatch());
        final HLogEntryPoolZookeeperPersistence dao2 = new HLogEntryPoolZookeeperPersistence(_conf2, zk2);
        dao2.setZookeeper(zk2);
        dao2.init(_confA);

        RecoverableZooKeeper zk3 = ZKUtil.connect(_confA, new NothingZookeeperWatch());
        HLogEntryPoolZookeeperPersistence dao3 = new HLogEntryPoolZookeeperPersistence(_confA, zk3);
        dao3.setZookeeper(zk3);
        dao3.init(_confA);

        FileSystem fs = _util1.getTestFileSystem();

        scan1 = new HLogGroupZookeeperScanner(_confA);
        scan2 = new HLogGroupZookeeperScanner(_confA);

        Path hlogPath = service.getHLogDir();
        Path oldPath = service.getOldHLogDir();

        int count = 0;

        Thread rndShutDownScan = new Thread() {

            @Override
            public void run() {
                super.run();
                while (true) {
                    try {
                        HLogGroupZookeeperScanner scan = rndShutDownScan(scan1, scan2);
                        // if (!scan.isAlive()) {
                        // System.out.println("start scan [" + scan.getName() + "] ...");
                        // scan.start();
                        // }
                        // Thread.sleep(AliHBaseConstants.ZOO_SCAN_LOCK_RETRYTIME * 2);
                        Thread.sleep(ProducerConstants.ZOO_SCAN_LOCK_RETRYTIME * 2);
                    } catch (Exception e) {
                    }
                }
            }
        };

        Thread rndShutDownDao = new Thread() {

            @Override
            public void run() {
                super.run();
                while (true) {
                    try {
                        // HLogZookeeperPersistence dao = rndShutDownDaoZk(dao1, dao2);
                        // Thread.sleep(1000);
                        // RecoverableZooKeeper zk = ZKUtil.connect(conf1, new ReplicationZookeeperWatch());
                        // dao.setZookeeper(zk);
                        // System.out.println("start [dao] " + dao.getName());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        rndShutDownDao.start();

        while (true) {
            insertRndData(_poolA, "testA", "colA", "test", 1000);
            Thread.sleep(10000 * 2);
            HLogEntryGroups groups = new HLogEntryGroups();
            groups.put(HLogUtil.getHLogsByHDFS(fs, hlogPath));
            groups.put(HLogUtil.getHLogsByHDFS(fs, oldPath));
            Assert.assertTrue(dao3.listGroupName().size() == groups.getGroups().size());
            for (HLogEntryGroup group : groups.getGroups()) {
                Assert.assertTrue(dao3.listEntry(group.getGroupName()).size() == group.getEntrys().size());
            }
            System.out.println("checkok -- " + count + " groups [ " + groups.getGroups().size() + " ] ");
            if (count > 10) return;
            count++;
        }
    }

    static Random rnd = new Random();

    public static HLogGroupZookeeperScanner rndShutDownScan(HLogGroupZookeeperScanner scan1,
                                                            HLogGroupZookeeperScanner scan2) throws IOException,
                                                                                            KeeperException,
                                                                                            InterruptedException {
        HLogGroupZookeeperScanner canStopScan;
        if (rnd.nextBoolean()) {
            canStopScan = scan1;
        } else {
            canStopScan = scan2;
        }

        // if (canStopScan.isAlive()) {
        // try {
        // canStopScan.stop();
        // System.out.println("stop scan [" + canStopScan.getName() + "] ...");
        // } catch (Exception e) {
        //
        // }
        // }
        return canStopScan;
    }

    // private static HLogZookeeperPersistence rndShutDownDaoZk(HLogZookeeperPersistence dao1,
    // HLogZookeeperPersistence dao2)
    // throws InterruptedException,
    // IOException {
    // HLogZookeeperPersistence canStop;
    // if (rnd.nextBoolean()) {
    // canStop = dao1;
    // } else {
    // canStop = dao2;
    // }
    //
    // canStop.getZookeeper().close();
    // System.out.println("stop [dao] " + canStop.getName());
    // return canStop;
    // }
}
