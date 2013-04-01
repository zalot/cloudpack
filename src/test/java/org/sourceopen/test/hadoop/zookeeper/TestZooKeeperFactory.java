package org.sourceopen.test.hadoop.zookeeper;

import org.junit.BeforeClass;
import org.junit.Test;
import org.sourceopen.base.HBaseBase;

public class TestZooKeeperFactory extends HBaseBase {

    @BeforeClass
    public static void init() throws Exception {
        startHBaseClusterA(-1, 3);
    }

    @Test
    public void testCreateRecoverableZooKeeper() throws Exception {
        getAdvZooKeeperByConfig(_confA);
    }
}
