package org.sourceopen.test.hbase.replication;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.junit.BeforeClass;
import org.junit.Test;
import org.sourceopen.analyze.hadoop.TestBase;
import org.sourceopen.hadoop.hbase.replication.hlog.HLogEntryPoolPersistence;
import org.sourceopen.hadoop.hbase.replication.hlog.HLogEntryPoolZookeeperPersistence;
import org.sourceopen.hadoop.hbase.replication.producer.ReplicationSinkManger;
import org.sourceopen.hadoop.hbase.replication.server.ReplicationConf;
import org.sourceopen.hadoop.zookeeper.connect.NothingZookeeperWatch;
import org.sourceopen.hadoop.zookeeper.connect.RecoverableZooKeeper;
import org.sourceopen.hadoop.zookeeper.connect.ZookeeperFactory;

import com.alibaba.hbase.test.alireplication.util.TestConfigurationUtil;

/**
 * 类TestMZookeeper.java的实现描述：TODO 类实现描述
 * 
 * @author zalot.zhaoh Apr 1, 2012 11:37:47 AM
 */
public class TestMZookeeper extends TestBase {

    protected static final int             tableSize    = 10;
    protected static final ReplicationConf confProducer = new ReplicationConf();

    @BeforeClass
    public static void init() throws Exception {
        initClusterA();
        initClusterB();
        TestConfigurationUtil.setProducer(_utilA.getConfiguration(), _utilB.getConfiguration(), confProducer);
    }

    @Test
    public void testMZkHLogP() throws Exception {
        final int count = 0;

        final RecoverableZooKeeper[] zks = new RecoverableZooKeeper[count];
        final HLogEntryPoolPersistence[] ps = new HLogEntryPoolPersistence[count];
        final Random rnd = new Random();
        final ThreadPoolExecutor pool = new ThreadPoolExecutor(count, count, 100, TimeUnit.SECONDS,
                                                               new ArrayBlockingQueue<Runnable>(count));

        for (int x = 0; x < count; x++) {
            zks[x] = ZookeeperFactory.createRecoverableZooKeeper(url, timeout, rt, watcher).connect(confProducer,
                                                                                                    new NothingZookeeperWatch());
            ps[x] = new HLogEntryPoolZookeeperPersistence(confProducer, zks[x]);
            // pool.execute(new Runnable() {
            // @Override
            // public void run() {
            // while(true){
            // int curRnd = rnd.nextInt(count);
            // }
            // }
            // });
            ps[x] = null;
        }
        int c = rnd.nextInt(count);
    }

    // @Test
    public void test() throws Exception {
        ReplicationConf confProducer = new ReplicationConf();
        TestConfigurationUtil.setProducer(_utilA.getConfiguration(), _utilB.getConfiguration(), confProducer);

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
