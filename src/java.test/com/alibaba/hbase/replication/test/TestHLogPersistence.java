package com.alibaba.hbase.replication.test;

import junit.framework.Assert;

import org.apache.hadoop.hbase.zookeeper.RecoverableZooKeeper;
import org.apache.hadoop.hbase.zookeeper.ZKUtil;
import org.junit.BeforeClass;
import org.junit.Test;

import com.alibaba.hbase.replication.domain.HLogEntry;
import com.alibaba.hbase.replication.domain.HLogEntryGroup;
import com.alibaba.hbase.replication.zookeeper.HLogZookeeperPersistence;
import com.alibaba.hbase.replication.zookeeper.ReplicationZookeeperWatch;

public class TestHLogPersistence extends BaseReplicationTest {

    @BeforeClass
    public static void init() throws Exception {
        init1();
        // init2();
    }

    @Test
    public void testZookeeperHLogPersistenceSimple() throws Exception {
        RecoverableZooKeeper zk1 = ZKUtil.connect(conf1, new ReplicationZookeeperWatch());
        HLogZookeeperPersistence dao1 = new HLogZookeeperPersistence();
        dao1.setZookeeper(zk1);
        dao1.init(conf1);

        RecoverableZooKeeper zk2 = ZKUtil.connect(conf1, new ReplicationZookeeperWatch());
        HLogZookeeperPersistence dao2 = new HLogZookeeperPersistence();
        dao2.setZookeeper(zk2);
        dao2.init(conf1);

        HLogEntryGroup group = new HLogEntryGroup("test");
        dao1.createGroup(group, false);

        Assert.assertNotNull(dao2.getGroupByName(group.getGroupName(), false));

        dao2.deleteGroup(group);
        Assert.assertNull(dao1.getGroupByName(group.getGroupName(), false));

        HLogEntry entry = new HLogEntry("abcd.1234");
        group.put(entry);
        dao1.createGroup(group, true);

        Assert.assertNull(dao1.getHLogEntry(entry.getGroupName(), entry.getName()));

        entry = new HLogEntry("test.1234");
        group.put(entry);
        dao2.deleteGroup(group);
        dao1.createGroup(group, true);
        Assert.assertNotNull(dao1.getHLogEntry(entry.getGroupName(), entry.getName()));
        
        Assert.assertTrue(dao1.lockGroup(group.getGroupName()));
        Assert.assertTrue(dao2.isLockGroup(group.getGroupName()));
        Assert.assertFalse(dao2.lockGroup(group.getGroupName()));
        
        zk1.close();
        Assert.assertFalse(dao2.isLockGroup(group.getGroupName()));
        Assert.assertTrue(dao2.lockGroup(group.getGroupName()));
        
        zk1 = ZKUtil.connect(conf1, new ReplicationZookeeperWatch());
        dao1.setZookeeper(zk1);
        
    }
}
