package org.sourceopen.test.hadoop.zookeeper;

import org.apache.zookeeper.ZooKeeper;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sourceopen.analyze.hadoop.hbase.TestHBase;
import org.sourceopen.hadoop.hbase.rutils.HBaseConfigurationUtil;
import org.sourceopen.hadoop.zookeeper.connect.NothingZookeeperWatch;
import org.sourceopen.hadoop.zookeeper.connect.ZookeeperFactory;

/**
 * 类TestZNodeFactory.java的实现描述：TODO 类实现描述
 * 
 * @author zalot.zhaoh Aug 3, 2012 9:38:54 AM
 */
public class TestZNodeFactory extends TestHBase {

    @BeforeClass
    public static void init() throws Exception {
        startHBaseClusterA(0, 3);
    }

    @Test
    public void testStartFactory() throws Exception {
        String zkUrl = HBaseConfigurationUtil.getZooKeeperURL(_confA);
        ZooKeeper zk = ZookeeperFactory.createRecoverableZooKeeper(zkUrl, 1000, 10, new NothingZookeeperWatch());
        for(String child : zk.getChildren("/", false)){
            System.out.println("child = /" + child);
        }
    }
}
