package org.sourceopen.analyze.zookeeper;

import java.io.IOException;
import java.util.Arrays;

import junit.framework.Assert;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.sourceopen.TestBase;
import org.junit.BeforeClass;
import org.junit.Test;

import com.alibaba.hbase.test.alireplication.util.TestConfigurationUtil;

/**
 * 类TestMZookeeper.java的实现描述：TODO 类实现描述
 * 
 * @author zalot.zhaoh Apr 1, 2012 11:37:47 AM
 */
public class AnalyzeZookeeperWatch extends TestBase {

    @BeforeClass
    public static void init() throws Exception {
        initClusterA();
        initClusterB();
        // start zookeeper only
        startHBaseClusterA(-1, 3);
    }

    @Test
    public void testMZkWatcher() throws IOException, KeeperException, InterruptedException {
        String zkStr = TestConfigurationUtil.getZkString(_util1.getConfiguration());
        class CheckBoolean {

            public boolean[] checks;

            public CheckBoolean(int size){
                checks = new boolean[size];
                Arrays.fill(checks, false);
            }

            public boolean check() {
                boolean ckOk = true;
                for (boolean booOk : checks) {
                    ckOk = booOk && ckOk;
                    if (ckOk == false) return false;
                }
                return true;
            }
        }

        final CheckBoolean ck = new CheckBoolean(3);
        ZooKeeper zk = new ZooKeeper(zkStr, 2000, new Watcher() {

            @Override
            public void process(WatchedEvent arg0) {
                ck.checks[0] = true;
            }
        });

        String basePath = "/idx";
        Stat stat = zk.exists(basePath, true);
        if (stat == null) {
            zk.create(basePath, null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }

        ZooKeeper zk2 = new ZooKeeper(zkStr, 2000, new Watcher() {

            @Override
            public void process(WatchedEvent arg0) {
                ck.checks[1] = true;
            }
        });

        zk2.getChildren(basePath, true);

        ZooKeeper zk3 = new ZooKeeper(zkStr, 2000, new Watcher() {

            @Override
            public void process(WatchedEvent arg0) {
                ck.checks[2] = true;
            }
        });

        zk3.getChildren(basePath, true);
        zk.create("/idx/1", null, Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        Assert.assertTrue(ck.check());
    }
}
