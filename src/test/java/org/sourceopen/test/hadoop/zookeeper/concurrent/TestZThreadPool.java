package org.sourceopen.test.hadoop.zookeeper.concurrent;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.junit.BeforeClass;
import org.junit.Test;
import org.sourceopen.analyze.hadoop.TestBase;
import org.sourceopen.hadoop.zookeeper.concurrent.ZThreadPool;
import org.sourceopen.hadoop.zookeeper.connect.AdvZooKeeper;
import org.sourceopen.hadoop.zookeeper.core.ZNode;
import org.sourceopen.hadoop.zookeeper.core.ZNodeFactory;

public class TestZThreadPool extends TestBase {

    @BeforeClass
    public static void init() throws Exception {
        startHBaseClusterA(0, 3);
    }

    @Test
    public void testDaemonThread() throws Exception {
        AdvZooKeeper zk = getAdvZooKeeperByConfig(_confA);
        ZNode root = ZNodeFactory.createZNode(zk, "17102@centos6.zalot");
        String[] gs = new String[] { "a", "b", "c" };
        for (String g : gs) {
            ZNode gn = root.addChild(g);
            for (int x = 0; x < 10; x++) {
                gn.addChild(g + x);
            }
        }

        ZThreadPool pool = new ZThreadPool(root, zk, 2, 4, 2, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(100));
    }
}
