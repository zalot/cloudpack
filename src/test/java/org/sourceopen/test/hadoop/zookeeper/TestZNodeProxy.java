package org.sourceopen.test.hadoop.zookeeper;

import org.junit.BeforeClass;
import org.junit.Test;
import org.sourceopen.analyze.hadoop.hbase.TestHBase;

public class TestZNodeProxy extends TestHBase {

    @BeforeClass
    public static void init() throws Exception {
        startHBaseClusterA(0, 3);
    }

    @Test
    public void testZNodeProxy() throws Exception {
    }
}
