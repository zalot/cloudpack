package org.sourceopen.test.hbase.replication;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import junit.framework.Assert;

import org.apache.zookeeper.KeeperException;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sourceopen.base.HBaseBase;
import org.sourceopen.hadoop.hbase.replication.core.HBaseService;
import org.sourceopen.hadoop.hbase.replication.core.hlog.domain.HLogGroup;
import org.sourceopen.hadoop.hbase.replication.producer.HLogScanner;
import org.sourceopen.hadoop.hbase.replication.producer.HLogScanner.GEMap;
import org.sourceopen.hadoop.hbase.replication.producer.ZkHLogPersistence;
import org.sourceopen.hadoop.hbase.utils.HRepConfigUtil;
import org.sourceopen.hadoop.zookeeper.connect.AdvZooKeeper;
import org.sourceopen.hadoop.zookeeper.connect.NothingZookeeperWatch;
import org.sourceopen.hadoop.zookeeper.core.ZNode;

/**
 * 类TestHLogScanner.java的实现描述：TODO 类实现描述
 * 
 * @author zalot.zhaoh Mar 22, 2012 1:50:42 PM
 */
public class TestHLogScanner extends HBaseBase {

    static HBaseService hb;

    @BeforeClass
    public static void init() throws Exception {
        startHBaseClusterA(3, 3);
        createDefTable(_confA);
        hb = new HBaseService(_confA);
    }

    @Test
    public void testHLogScannerStop() throws Exception {
        AdvZooKeeper zk1 = HRepConfigUtil.createAdvZooKeeperByHBaseConfig(_confA, new NothingZookeeperWatch());
        ZkHLogPersistence dao1 = new ZkHLogPersistence(_confA, zk1);

        ZNode root = HRepConfigUtil.getRootZNode(_confA);
        long sleepTime = 3000;
        long lockTime = 500;
        HLogScanner scan = HLogScanner.newInstance(zk1, root, dao1, hb, lockTime, sleepTime);
        scan.start();

        scan.shutdown();
        Thread.sleep(2000);
        System.out.println(scan.isAlive());
        scan.start();
        Thread.sleep(20000);
    }

    @Test
    public void testSThreadHLogScanner() throws Exception {
        AdvZooKeeper zk1 = HRepConfigUtil.createAdvZooKeeperByHBaseConfig(_confA, new NothingZookeeperWatch());
        ZkHLogPersistence dao1 = new ZkHLogPersistence(_confA, zk1);

        ZNode root = HRepConfigUtil.getRootZNode(_confA);
        long sleepTime = 3000;
        long lockTime = 500;
        HLogScanner scan = HLogScanner.newInstance(zk1, root, dao1, hb, lockTime, sleepTime);
        scan.start();
        int count = 0;
        Thread.sleep(lockTime * 2);
        while (true) {
            insertRndData(_poolA, "testA", "colA", "test", rnd.nextInt(1000));
            Thread.sleep(sleepTime * 2);
            GEMap groups = new GEMap();
            groups.put(hb.getAllHLogs());
            groups.put(hb.getAllOldHLogs());
            Assert.assertTrue(dao1.listGroupName().size() == groups.getGroups().size());
            for (HLogGroup group : groups.getGroups()) {
                Assert.assertTrue(dao1.listEntry(group.getGroupName()).size() == group.getEntrys().size());
            }
            System.out.println("checkok -- " + count + " groups [ " + groups.getGroups().size() + " ] ");
            if (count > 10) break;
            count++;
        }
        scan.shutdown();
    }

    @Test
    public void testMThreadHLogScanner() throws Exception {
        ZNode root = HRepConfigUtil.getRootZNode(_confA);
        long sleepTime = 3000;
        final long tryLockTime = 5000;

        final AdvZooKeeper zk1 = HRepConfigUtil.createAdvZooKeeperByHBaseConfig(_confA, new NothingZookeeperWatch());
        final ZkHLogPersistence dao1 = new ZkHLogPersistence(_confA, zk1);
        final HLogScanner scan1 = HLogScanner.newInstance(zk1, root, dao1, hb, tryLockTime, sleepTime);

        final AdvZooKeeper zk2 = HRepConfigUtil.createAdvZooKeeperByHBaseConfig(_confA, new NothingZookeeperWatch());
        final ZkHLogPersistence dao2 = new ZkHLogPersistence(_confA, zk2);
        final HLogScanner scan2 = HLogScanner.newInstance(zk2, root, dao2, hb, tryLockTime, sleepTime);

        final AdvZooKeeper zk3 = HRepConfigUtil.createAdvZooKeeperByHBaseConfig(_confA, new NothingZookeeperWatch());
        final ZkHLogPersistence dao3 = new ZkHLogPersistence(_confA, zk3);

        int count = 0;

        scan1.start();
        scan2.start();
        Thread.sleep(tryLockTime * 2);

        final AtomicBoolean ab = new AtomicBoolean(true);
        final Thread rndShutDownScan = new Thread() {

            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(10000);
                        rndShutDownScan(scan1, scan2);
                    } catch (Exception e) {
                    }
                }
            }
        };

        rndShutDownScan.start();

        final Thread rndCloseZK = new Thread() {

            @Override
            public void run() {
                super.run();
                while (true) {
                    if (!ab.get()) {
                        try {
                            Thread.sleep(5000);
                            ZkHLogPersistence dao = rndShutDownDaoZk(dao1, dao2);
                            AdvZooKeeper zk = HRepConfigUtil.createAdvZooKeeperByHBaseConfig(_confA,
                                                                                             new NothingZookeeperWatch());
                            dao.setZoo(zk);
                            System.out.println("start [dao-zk] " + dao.getName());
                        } catch (Exception e) {
                        }
                        ab.set(!ab.get());
                    }
                }
            }
        };
        // rndCloseZK.start();
        Thread.sleep(tryLockTime * 2);
        while (true) {
            insertRndData(_poolA, "testA", "colA", "test", rnd.nextInt(1000));
            Thread.sleep(sleepTime * 10);
            GEMap groups = new GEMap();
            groups.put(hb.getAllHLogs());
            groups.put(hb.getAllOldHLogs());
            Assert.assertTrue(dao3.listGroupName().size() == groups.getGroups().size());
            for (HLogGroup group : groups.getGroups()) {
                Assert.assertTrue(dao3.listEntry(group.getGroupName()).size() == group.getEntrys().size());
            }
            System.out.println("checkok -- " + count + " groups [ " + groups.getGroups().size() + " ] ");
            if (count > 10) return;
            count++;
        }
    }

    static Random rnd = new Random();

    public static HLogScanner rndShutDownScan(HLogScanner scan1, HLogScanner scan2) throws IOException,
                                                                                   KeeperException,
                                                                                   InterruptedException {
        HLogScanner canYieldScan;
        if (rnd.nextBoolean()) {
            canYieldScan = scan1.isRunning() ? scan1 : scan2;
        } else {
            canYieldScan = scan2.isRunning() ? scan2 : scan1;
        }
        canYieldScan.yieldzt();
        System.out.println("yield scan [" + canYieldScan.getName() + "] ...");
        return canYieldScan;
    }

    private static ZkHLogPersistence rndShutDownDaoZk(ZkHLogPersistence dao1, ZkHLogPersistence dao2)
                                                                                                     throws InterruptedException,
                                                                                                     IOException {
        ZkHLogPersistence canStop;
        if (rnd.nextBoolean()) {
            canStop = dao1;
        } else {
            canStop = dao2;
        }
        canStop.getZoo().close();
        System.out.println("stop [dao] " + canStop.getName());
        return canStop;
    }
}
