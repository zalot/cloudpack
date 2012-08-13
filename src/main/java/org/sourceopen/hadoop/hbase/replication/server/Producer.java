/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package org.sourceopen.hadoop.hbase.replication.server;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sourceopen.hadoop.hbase.replication.core.HBaseService;
import org.sourceopen.hadoop.hbase.replication.core.hlog.domain.HLogPersistence;
import org.sourceopen.hadoop.hbase.replication.producer.HLogScanner;
import org.sourceopen.hadoop.hbase.replication.producer.ZkHLogPersistence;
import org.sourceopen.hadoop.hbase.replication.protocol.ProtocolAdapter;
import org.sourceopen.hadoop.hbase.replication.utility.ProducerConstants;
import org.sourceopen.hadoop.hbase.utils.HRepConfigUtil;
import org.sourceopen.hadoop.zookeeper.connect.AdvZooKeeper;
import org.sourceopen.hadoop.zookeeper.connect.ZookeeperFactory;
import org.sourceopen.hadoop.zookeeper.core.ZNode;
import org.sourceopen.hadoop.zookeeper.core.ZNodeFactory;

/**
 * 类Producer.java的实现描述：Producer的main线程
 */
public class Producer {

    private static final Logger LOG     = LoggerFactory.getLogger(Producer.class);
    private static boolean      running = true;

    public static void main(String args[]) {
        try {
            // 钩子
            Runtime.getRuntime().addShutdownHook(new Thread() {

                @Override
                public void run() {
                    try {
                        Producer.stop();
                        LOG.info("Producer server stopped");
                        System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())
                                           + " Producer server stoped");
                    } catch (Throwable t) {
                        LOG.error("Fail to stop Producer server: ", t);
                    }
                    synchronized (Producer.class) {
                        running = false;
                        Producer.class.notify();
                    }

                }
            });
            // 启动Server
            Configuration conf = HBaseConfiguration.create();
            conf.addResource(ProducerConstants.COMMON_CONFIG_FILE);
            conf.addResource(ProducerConstants.PRODUCER_CONFIG_FILE);
            Producer.start(conf);
            LOG.info("Producer server started");
            System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())
                               + " Producer server started");

        } catch (Throwable t) {
            LOG.error("Fail to start producer server: ", t);
            System.exit(-1);
        }
        synchronized (Producer.class) {
            while (running) {
                try {
                    Producer.class.wait();
                } catch (Throwable t) {
                    LOG.error("Producer server got runtime errors: ", t);
                }
            }
        }
    }

    protected static void stop() {
        // TODO Auto-generated method stub

    }

    protected static ThreadPoolExecutor replicationPool;
    protected static ThreadPoolExecutor recoverPool;
    protected static ThreadPoolExecutor scannerPool;
    protected static ZNode              root;

    public static void start(Configuration conf) throws Exception {
        AdvZooKeeper zookeeper = ZookeeperFactory.createRecoverableZooKeeper(HRepConfigUtil.getZkStringV2(conf), 500,
                                                                             null, 10, 1000);

        HBaseService hbService = new HBaseService(conf);
        ProtocolAdapter adapter = ProtocolAdapter.getAdapter(conf);
        HLogPersistence hp = new ZkHLogPersistence(conf, zookeeper);

        root = ZNodeFactory.createZNode(zookeeper,
                                        conf.get(ProducerConstants.CONFKEY_ROOT_ZOO, ProducerConstants.ROOT_ZOO), true);

        startHLogScanner(conf, hbService, hp);
        startRejectScanner(conf, hbService, adapter);
        startReplicationTransfer(conf, hbService, adapter, hp);
        // replicationPool = new ThreadPoolExecutor(
        // conf.getInt(ProducerConstants.CONFKEY_REP_SINK_POOL_SIZE,
        // ProducerConstants.REP_SINK_POOL_SIZE),
        // conf.getInt(ProducerConstants.CONFKEY_REP_SINK_POOL_SIZE,
        // ProducerConstants.REP_SINK_POOL_SIZE),
        // conf.getInt(ProducerConstants.CONFKEY_THREADPOOL_KEEPALIVE_TIME, 100),
        // TimeUnit.SECONDS,
        // new ArrayBlockingQueue<Runnable>(
        // conf.getInt(ProducerConstants.CONFKEY_THREADPOOL_SIZE,
        // 100)));

        // recoverPool = new ThreadPoolExecutor(
        // conf.getInt(ProducerConstants.CONFKEY_REP_REJECT_POOL_SIZE,
        // ProducerConstants.REP_REJECT_POOL_SIZE),
        // conf.getInt(ProducerConstants.CONFKEY_REP_REJECT_POOL_SIZE,
        // ProducerConstants.REP_REJECT_POOL_SIZE),
        // conf.getInt(ProducerConstants.CONFKEY_THREADPOOL_KEEPALIVE_TIME, 100),
        // TimeUnit.SECONDS,
        // new ArrayBlockingQueue<Runnable>(
        // conf.getInt(ProducerConstants.CONFKEY_THREADPOOL_SIZE,
        // 100)));

        // HReplicationRejectRecoverScanner recover;
        // for (int i = 0; i < conf.getInt(ProducerConstants.CONFKEY_REP_REJECT_POOL_SIZE,
        // ProducerConstants.REP_REJECT_POOL_SIZE); i++) {
        // recover = new HReplicationRejectRecoverScanner(conf);
        // recover.setZooKeeper(zookeeper);
        // recover.setHlogService(hlogService);
        // recover.setAdapter(adapter);
        // recoverPool.execute(recover);
        // }

        // ThreadPoolExecutor crushPool = new ThreadPoolExecutor(
        // conf.getInt(ProducerConstants.CONFKEY_REP_REJECT_POOL_SIZE,
        // ProducerConstants.REP_REJECT_POOL_SIZE),
        // conf.getInt(ProducerConstants.CONFKEY_REP_REJECT_POOL_SIZE,
        // ProducerConstants.REP_REJECT_POOL_SIZE),
        // conf.getInt(ProducerConstants.CONFKEY_THREADPOOL_KEEPALIVE_TIME,
        // 100),
        // TimeUnit.SECONDS,
        // new ArrayBlockingQueue<Runnable>(
        // conf.getInt(ProducerConstants.CONFKEY_THREADPOOL_SIZE,
        // 100)));
        // HReplicationCrushScanner crush;
        // for (int i = 0; i < conf.getInt(ProducerConstants.CONFKEY_REP_REJECT_POOL_SIZE,
        // ProducerConstants.REP_REJECT_POOL_SIZE); i++) {
        // crush = new HReplicationCrushScanner(conf);
        // crush.setZooKeeper(zookeeper);
        // crush.setAdapter(adapter);
        // crushPool.execute(crush);
        // }

        // HReplicationProducer producer;
        // for (int i = 0; i < conf.getInt(ProducerConstants.CONFKEY_REP_SINK_POOL_SIZE,
        // ProducerConstants.REP_SINK_POOL_SIZE); i++) {
        // producer = new HReplicationProducer(conf);
        // producer.setAdapter(adapter);
        // producer.setHlogService(hlogService);
        // producer.setHlogEntryPersistence(hLogEntryPersistence);
        // replicationPool.execute(producer);
        // }
    }

    private static void startRejectScanner(Configuration conf, HBaseService hbService, ProtocolAdapter adapter) {
        // TODO Auto-generated method stub

    }

    private static void startReplicationTransfer(Configuration conf, HBaseService hbService, ProtocolAdapter adapter,
                                                 HLogPersistence hp) {
        // TODO Auto-generated method stub

    }

    private static void startHLogScanner(Configuration conf, HBaseService hbService, HLogPersistence hp) {
        scannerPool = new ThreadPoolExecutor(
                                             conf.getInt(ProducerConstants.CONFKEY_REP_SCANNER_POOL_SIZE,
                                                         ProducerConstants.REP_SCANNER_POOL_SIZE),
                                             conf.getInt(ProducerConstants.CONFKEY_REP_SCANNER_POOL_SIZE,
                                                         ProducerConstants.REP_SCANNER_POOL_SIZE),
                                             conf.getInt(ProducerConstants.CONFKEY_THREADPOOL_KEEPALIVE_TIME, 100),
                                             TimeUnit.SECONDS,
                                             new ArrayBlockingQueue<Runnable>(
                                                                              conf.getInt(ProducerConstants.CONFKEY_THREADPOOL_SIZE,
                                                                                          100)));
        
        long lockTime  = ProducerConstants.CONFKEY_ZOO_SCAN_LOCK_FLUSHSLEEPTIME,
                                       ProducerConstants.ZOO_SCAN_LOCK_FLUSHSLEEPTIME));
        lock.setTryLockTime(conf.getLong(ProducerConstants.CONFKEY_ZOO_SCAN_LOCK_RETRYTIME,
                                         ProducerConstants.ZOO_SCAN_LOCK_RETRYTIME));
        HLogScanner.newInstance(zk, root, hlog, hb, tryLockTime, onceSleepTime)
        for (int i = 0; i < conf.getInt(ProducerConstants.CONFKEY_REP_SCANNER_POOL_SIZE,
                                        ProducerConstants.REP_SCANNER_POOL_SIZE); i++) {
            scan = new HLogScanner(conf);
            scan.setZooKeeper(zookeeper);
            scan.setHlogService(hlogService);
            scan.setHLogPersistence(hLogEntryPersistence);
            scannerPool.execute(scan);
        }
    }
}
