package com.alibaba.hbase.replication.test;

import java.io.IOException;
import java.util.Random;

import junit.framework.Assert;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.zookeeper.RecoverableZooKeeper;
import org.apache.hadoop.hbase.zookeeper.ZKUtil;
import org.apache.zookeeper.KeeperException;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.hbase.replication.hlog.domain.HLogEntryGroup;
import com.alibaba.hbase.replication.hlog.domain.HLogEntryGroups;
import com.alibaba.hbase.replication.producer.HLogGroupZookeeperScanner;
import com.alibaba.hbase.replication.utility.ProducerConstants;
import com.alibaba.hbase.replication.utility.HLogUtil;
import com.alibaba.hbase.replication.zookeeper.HLogZookeeperPersistence;
import com.alibaba.hbase.replication.zookeeper.ReplicationZookeeperWatch;

public class TestHLogScanner extends BaseReplicationTest {

    private static final Logger LOG = LoggerFactory.getLogger(TestHLogScanner.class);
    @BeforeClass
    public static void init() throws Exception {
        LOG.error("bbb");
        init1();
        LOG.error("aaa");
    }

    @Test
    public void testSThreadScan() throws Exception {
        HLogGroupZookeeperScanner scan;
        RecoverableZooKeeper zk1 = ZKUtil.connect(conf1, new ReplicationZookeeperWatch());
        HLogZookeeperPersistence dao1 = new HLogZookeeperPersistence();
        dao1.setZookeeper(zk1);
        dao1.init(conf1);

        FileSystem fs = util1.getTestFileSystem();
        scan = new HLogGroupZookeeperScanner(conf1, fs, dao1);
        scan.start();

        int count = 0;
        while (true) {
            insertData(pool1, TABLEA, COLA, "test", 1000);
            Thread.sleep(scan.getFlushSleepTime() * 2);
            HLogEntryGroups groups = new HLogEntryGroups();
            groups.put(HLogUtil.getHLogsByHDFS(fs, scan.getHlogPath()));
            groups.put(HLogUtil.getHLogsByHDFS(fs, scan.getOldHlogPath()));
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
        final HLogGroupZookeeperScanner scan1;
        final HLogGroupZookeeperScanner scan2;

        RecoverableZooKeeper zk1 = ZKUtil.connect(conf1, new ReplicationZookeeperWatch());
        final HLogZookeeperPersistence dao1 = new HLogZookeeperPersistence();
        dao1.setZookeeper(zk1);
        dao1.init(conf1);

        RecoverableZooKeeper zk2 = ZKUtil.connect(conf1, new ReplicationZookeeperWatch());
        final HLogZookeeperPersistence dao2 = new HLogZookeeperPersistence();
        dao2.setZookeeper(zk2);
        dao2.init(conf1);

        RecoverableZooKeeper zk3 = ZKUtil.connect(conf1, new ReplicationZookeeperWatch());
        HLogZookeeperPersistence dao3 = new HLogZookeeperPersistence();
        dao3.setZookeeper(zk3);
        dao3.init(conf1);

        FileSystem fs = util1.getTestFileSystem();

        scan1 = new HLogGroupZookeeperScanner(conf1, fs, dao1);
        scan2 = new HLogGroupZookeeperScanner(conf1, fs, dao2);
        scan1.start();
        scan2.start();

        Path hlogPath = scan1.getHlogPath();
        Path oldPath = scan1.getOldHlogPath();

        int count = 0;

        Thread rndShutDownScan = new Thread() {

            @Override
            public void run() {
                super.run();
                while (true) {
                    try {
                        HLogGroupZookeeperScanner scan = rndShutDownScan(scan1, scan2);
                        if (!scan.isAlive()) {
                            System.out.println("start scan [" + scan.getName() + "] ...");
                            scan.start();
                        }
//                        Thread.sleep(AliHBaseConstants.ZOO_SCAN_LOCK_RETRYTIME * 2);
                        Thread.sleep(ProducerConstants.ZOO_SCAN_LOCK_TRYLOCKTIME * 2);
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
                        HLogZookeeperPersistence dao = rndShutDownDaoZk(dao1, dao2);
                        Thread.sleep(1000);
                        RecoverableZooKeeper zk = ZKUtil.connect(conf1, new ReplicationZookeeperWatch());
                        dao.setZookeeper(zk);
                        System.out.println("start [dao] " + dao.getName());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        rndShutDownDao.start();

        while (true) {
            insertData(pool1, TABLEA, COLA, "test", 1000);
            Thread.sleep(scan1.getFlushSleepTime() * 2);
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

        if (canStopScan.isAlive()) {
            try {
                canStopScan.stop();
                System.out.println("stop scan [" + canStopScan.getName() + "] ...");
            } catch (Exception e) {

            }
        }
        return canStopScan;
    }

    private static HLogZookeeperPersistence rndShutDownDaoZk(HLogZookeeperPersistence dao1,
                                                             HLogZookeeperPersistence dao2)
                                                                                           throws InterruptedException,
                                                                                           IOException {
        HLogZookeeperPersistence canStop;
        if (rnd.nextBoolean()) {
            canStop = dao1;
        } else {
            canStop = dao2;
        }

        canStop.getZookeeper().close();
        System.out.println("stop [dao] " + canStop.getName());
        return canStop;
    }
}
