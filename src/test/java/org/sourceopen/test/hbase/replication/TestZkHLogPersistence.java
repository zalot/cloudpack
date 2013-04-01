package org.sourceopen.test.hbase.replication;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;
import org.sourceopen.base.HBaseBase;
import org.sourceopen.hadoop.hbase.replication.core.hlog.domain.HLogEntry;
import org.sourceopen.hadoop.hbase.replication.core.hlog.domain.HLogGroup;
import org.sourceopen.hadoop.hbase.replication.producer.ZkHLogPersistence;
import org.sourceopen.hadoop.hbase.utils.HRepConfigUtil;
import org.sourceopen.hadoop.zookeeper.connect.AdvZooKeeper;
import org.sourceopen.hadoop.zookeeper.connect.NothingZookeeperWatch;

/**
 * 类TestHLogPersistence.java的实现描述：TODO 类实现描述
 * 
 * @author zalot.zhaoh Mar 22, 2012 1:50:48 PM
 */
public class TestZkHLogPersistence extends HBaseBase {

    @BeforeClass
    public static void init() throws Exception {
        startHBaseClusterA(-1, 3);
    }

    @Test
    public void testZookeeperHLogPersistenceSimple() throws Exception {
        AdvZooKeeper zk1 = HRepConfigUtil.createAdvZooKeeperByHBaseConfig(_confA, new NothingZookeeperWatch());
        final ZkHLogPersistence dao1 = new ZkHLogPersistence(_confA, zk1);

        AdvZooKeeper zk2 = HRepConfigUtil.createAdvZooKeeperByHBaseConfig(_confA, new NothingZookeeperWatch());
        final ZkHLogPersistence dao2 = new ZkHLogPersistence(_confA, zk2);

        HLogGroup group = new HLogGroup("test");
        dao1.createGroup(group, false);
        Assert.assertNotNull(dao2.getGroupByName(group.getGroupName(), false));
        Assert.assertNotNull(dao1.getGroupByName(group.getGroupName(), false));

        HLogEntry entry = new HLogEntry("abcd.1234");
        group.put(entry);
        dao1.createOrUpdateGroup(group, true);

        Assert.assertNull(dao1.getEntry(entry.getGroupName(), entry.getName()));
        Assert.assertNull(dao2.getEntry(entry.getGroupName(), entry.getName()));

        entry = new HLogEntry("test.1234");
        group.put(entry);
        dao1.createOrUpdateGroup(group, true);
        Assert.assertNotNull(dao1.getEntry(entry.getGroupName(), entry.getName()));
        Assert.assertNotNull(dao2.getEntry(entry.getGroupName(), entry.getName()));

        Assert.assertTrue(dao1.lockGroup(group.getGroupName()));
        Assert.assertTrue(dao2.isLockGroup(group.getGroupName()));
        Assert.assertFalse(dao2.lockGroup(group.getGroupName()));

        zk1.close();
        Assert.assertFalse(dao2.isLockGroup(group.getGroupName()));
        Assert.assertTrue(dao2.lockGroup(group.getGroupName()));
        zk2.close();
    }

    @Test
    public void testMuThreadPer() throws Exception {
        final HLogGroup group = new HLogGroup("testB");

        AdvZooKeeper zk1 = HRepConfigUtil.createAdvZooKeeperByHBaseConfig(_confA, new NothingZookeeperWatch());
        final ZkHLogPersistence dao1 = new ZkHLogPersistence(_confA, zk1);

        AdvZooKeeper zk2 = HRepConfigUtil.createAdvZooKeeperByHBaseConfig(_confA, new NothingZookeeperWatch());
        final ZkHLogPersistence dao2 = new ZkHLogPersistence(_confA, zk2);

        dao1.createGroup(group, false);

        Runnable run1 = new Runnable() {

            @Override
            public void run() {
                int checkCount = 0;
                while (true) {
                    try {
                        if (dao1.lockGroup(group.getGroupName())) {
                            Thread.sleep(300);
                            Assert.assertTrue(dao1.isLockGroup(group.getGroupName()));
                            Assert.assertTrue(dao2.isLockGroup(group.getGroupName()));
                            Assert.assertFalse(dao2.lockGroup(group.getGroupName()));
                            Assert.assertFalse(dao2.isMeLockGroup(group.getGroupName()));
                            dao1.unlockGroup(group.getGroupName());
                            Assert.assertFalse(dao1.isMeLockGroup(group.getGroupName()));
                            checkCount++;
                            if (checkCount > 500) {
                                return;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Assert.assertTrue(false);
                    }
                }
            }
        };

        Runnable run2 = new Runnable() {

            @Override
            public void run() {
                while (true) {
                    int checkCount = 0;
                    try {
                        if (dao2.lockGroup(group.getGroupName())) {
                            Thread.sleep(300);
                            Assert.assertTrue(dao2.isLockGroup(group.getGroupName()));
                            Assert.assertTrue(dao1.isLockGroup(group.getGroupName()));
                            Assert.assertFalse(dao1.lockGroup(group.getGroupName()));
                            Assert.assertFalse(dao1.isMeLockGroup(group.getGroupName()));
                            dao2.unlockGroup(group.getGroupName());
                            Assert.assertFalse(dao2.isMeLockGroup(group.getGroupName()));
                            checkCount++;
                            if (checkCount > 500) {
                                return;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Assert.assertTrue(false);
                    }

                }
            }
        };

        Thread td1 = new Thread(run1);
        td1.start();

        Thread td2 = new Thread(run2);
        td2.start();
        Thread.sleep(600000);
    }
}
