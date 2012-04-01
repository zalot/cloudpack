package com.alibaba.hbase.replication.test.zookeeper;

import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import junit.framework.Assert;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.junit.BeforeClass;
import org.junit.Test;

import com.alibaba.hbase.replication.hlog.HLogEntryPoolPersistence;
import com.alibaba.hbase.replication.hlog.HLogEntryPoolZookeeperPersistence;
import com.alibaba.hbase.replication.producer.ReplicationSinkManger;
import com.alibaba.hbase.replication.server.ReplicationConf;
import com.alibaba.hbase.replication.test.util.TestConfigurationUtil;
import com.alibaba.hbase.replication.utility.ZKUtil;
import com.alibaba.hbase.replication.zookeeper.NothingZookeeperWatch;
import com.alibaba.hbase.replication.zookeeper.RecoverableZooKeeper;

/**
 * 类TestMZookeeper.java的实现描述：TODO 类实现描述 
 * @author zalot.zhaoh Apr 1, 2012 11:37:47 AM
 */
public class TestMZookeeper extends TestBaseZookeeper {

    protected final int tableSize = 10;

    @BeforeClass
    public static void init() throws Exception {
        init1();
        init2();
        TestConfigurationUtil.setProducer(util1.getConfiguration(), util2.getConfiguration(), confProducer);
    }

    @Test
    public void testMZkWatcher() throws IOException, KeeperException, InterruptedException {
        String zkStr = TestConfigurationUtil.getZkString(util1.getConfiguration());
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

    @Test
    public void testMZkHLogP() throws Exception {
        final int count = 0;

        final RecoverableZooKeeper[] zks = new RecoverableZooKeeper[count];
        final HLogEntryPoolPersistence[] ps = new HLogEntryPoolPersistence[count];
        final Random rnd = new Random();
        final ThreadPoolExecutor pool = new ThreadPoolExecutor(count, count, 100, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(count));
        
        for (int x = 0; x < count; x++) {
            zks[x] = ZKUtil.connect(confProducer, new NothingZookeeperWatch());
            ps[x] = new HLogEntryPoolZookeeperPersistence(confProducer, zks[x]);
//            pool.execute(new Runnable() {
//                @Override
//                public void run() {
//                    while(true){
//                        int curRnd = rnd.nextInt(count);
//                    }
//                }
//            });
        }
        int c = rnd.nextInt(count);
        
    }

    // @Test
    public void test() throws Exception {
        ReplicationConf confProducer = new ReplicationConf();
        TestConfigurationUtil.setProducer(util1.getConfiguration(), util2.getConfiguration(), confProducer);

        for (int x = 0; x < 3; x++) {
            ReplicationSinkManger manager = new ReplicationSinkManger();
            manager.setRefConf(confProducer);
            manager.start();
        }
        Thread.sleep(1200000);
    }

    public void rndWrite() {
        // createTable(conf);
    }
}
