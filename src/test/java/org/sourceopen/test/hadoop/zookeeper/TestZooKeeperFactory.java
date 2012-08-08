package org.sourceopen.test.hadoop.zookeeper;

import org.junit.BeforeClass;
import org.junit.Test;
import org.sourceopen.analyze.hadoop.hbase.TestHBase;

public class TestZooKeeperFactory extends TestHBase {

    @BeforeClass
    public static void init() throws Exception {
        startHBaseClusterA(-1, 3);
    }

    @Test
    public void testCreateRecoverableZooKeeper() throws Exception {
        getAdvZooKeeperByConfig(_confA);
    }
}
