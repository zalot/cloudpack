package org.sourceopen.test.hadoop.zookeeper;

import org.apache.zookeeper.ZooKeeper;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sourceopen.analyze.hadoop.hbase.TestHBase;
import org.sourceopen.hadoop.hbase.rutils.HBaseConfigurationUtil;
import org.sourceopen.hadoop.zookeeper.connect.NothingZookeeperWatch;
import org.sourceopen.hadoop.zookeeper.connect.ZookeeperFactory;
import org.sourceopen.hadoop.zookeeper.core.ZNodeProxy;
import org.sourceopen.hadoop.zookeeper.core.ZNodeProxyFactory;

public class TestZNodeProxy extends TestHBase {

    @BeforeClass
    public static void init() throws Exception {
        startHBaseClusterA(0, 3);
        String zkUrl = HBaseConfigurationUtil.getZooKeeperURL(_confA);
        ZooKeeper zk = ZookeeperFactory.createRecoverableZooKeeper(zkUrl, 1000, 10, new NothingZookeeperWatch());
        ZNodeProxyFactory.setZooKeeperProxy(zk);
    }

    @Test
    public void testZNodeProxy() throws Exception {
        ZNodeProxy zn = ZNodeProxyFactory.createZNodeProxy(null, "a", true, new NothingZookeeperWatch());
     }
}
