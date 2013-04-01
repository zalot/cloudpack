package org.sourceopen.test.hadoop.zookeeper;

import org.junit.BeforeClass;
import org.junit.Test;
import org.sourceopen.base.HBaseBase;

public class TestZNodeProxy extends HBaseBase {

    @BeforeClass
    public static void init() throws Exception {
        startHBaseClusterA(0, 3);
    }

    @Test
    public void testZNodeProxy() throws Exception {
    }
}
