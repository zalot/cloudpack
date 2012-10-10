package org.sourceopen.test.hbase.replication;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sourceopen.analyze.hadoop.TestBase1;
import org.sourceopen.hadoop.hbase.replication.core.hlog.domain.HLogPersistence;
import org.sourceopen.hadoop.hbase.replication.producer.ZkHLogPersistence;
import org.sourceopen.hadoop.hbase.utils.HRepConfigUtil;
import org.sourceopen.hadoop.zookeeper.connect.AdvZooKeeper;
import org.sourceopen.hadoop.zookeeper.connect.ZookeeperFactory;

/**
 * 类TestMZookeeper.java的实现描述：TODO 类实现描述
 * 
 * @author zalot.zhaoh Apr 1, 2012 11:37:47 AM
 */
public class TestMZookeeper extends TestBase1 {

    protected static final int     tableSize = 10;
    protected static Configuration CONF      = HBaseConfiguration.create();

    @BeforeClass
    public static void init() throws Exception {
        startHBaseClusterA(-1, 3);
        HRepConfigUtil.setProducerConfig(_utilA.getConfiguration(), _utilB.getConfiguration(), CONF);
    }

    @Test
    public void testMZkHLogP() throws Exception {
        final int count = 3;
        final AdvZooKeeper[] zks = new AdvZooKeeper[count];
        final HLogPersistence[] ps = new HLogPersistence[count];
        final Random rnd = new Random();
        final ThreadPoolExecutor pool = new ThreadPoolExecutor(count, count, 100, TimeUnit.SECONDS,
                                                               new ArrayBlockingQueue<Runnable>(count));

        for (int x = 0; x < count; x++) {
            zks[x] = ZookeeperFactory.createRecoverableZooKeeper(HRepConfigUtil.getZKStringV1(_confA), 100,
                                                                 null, 5, 1000);
            ps[x] = new ZkHLogPersistence(CONF, zks[x]);
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

}
