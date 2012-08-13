package org.sourceopen.test.hbase.replication;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;
import org.sourceopen.analyze.hadoop.hbase.TestHBase;
import org.sourceopen.hadoop.hbase.replication.core.HBaseService;
import org.sourceopen.hadoop.hbase.replication.producer.HLogScanner;
import org.sourceopen.hadoop.hbase.replication.producer.RejectRecoverScanner;
import org.sourceopen.hadoop.hbase.replication.producer.ReplicationTransfer;
import org.sourceopen.hadoop.hbase.replication.producer.ZkHLogPersistence;
import org.sourceopen.hadoop.hbase.replication.protocol.ProtocolAdapter;
import org.sourceopen.hadoop.hbase.utils.HRepConfigUtil;
import org.sourceopen.hadoop.zookeeper.connect.AdvZooKeeper;
import org.sourceopen.hadoop.zookeeper.connect.NothingZookeeperWatch;
import org.sourceopen.hadoop.zookeeper.core.ZNode;

/**
 * 主流成测试 类TestMain.java的实现描述：TODO 类实现描述
 * 
 * @author zalot.zhaoh Mar 19, 2012 11:26:29 AM
 */
public class TestMain extends TestHBase {

    @BeforeClass
    public static void init() throws Exception {
        startHBaseClusterA(3, 3);
    }

    @Test
    public void testReplicationAndDFSFileAdapter() throws Exception {
        HBaseService hb = new HBaseService(_confA);
        AdvZooKeeper zk1 = HRepConfigUtil.createAdvZooKeeperByHBaseConfig(_confA, new NothingZookeeperWatch());
        ZkHLogPersistence dao1 = new ZkHLogPersistence(_confA, zk1);

        ZNode root = HRepConfigUtil.getRootZNode(_confA);
        long sleepTime = 3000;
        long lockTime = 500;
        HLogScanner scan = HLogScanner.newInstance(zk1, root, dao1, hb, lockTime, sleepTime);
        ProtocolAdapter adapter = ProtocolAdapter.getAdapter(_confA);
        ReplicationTransfer producer = ReplicationTransfer.newInstance(_confA, adapter, dao1, hb);

        RejectRecoverScanner recover = RejectRecoverScanner.newInstance(_confA, zk1, hb, adapter);

        // insert and run
        createDefTable(_confA);
        insertRndData(_poolA, "testA", "colA", "test", 1000);
        scan.doScan();
        producer.doTransfer();
        Assert.assertTrue(adapter.listHead().size() == 1);
        Assert.assertTrue(adapter.listRejectHead().size() == 0);

        adapter.reject(adapter.listHead().get(0));
        Assert.assertTrue(adapter.listHead().size() == 0);
        Assert.assertTrue(adapter.listRejectHead().size() == 1);

        recover.doRecover();
        Assert.assertTrue(adapter.listHead().size() == 1);
        Assert.assertTrue(adapter.listRejectHead().size() == 0);
    }
}
