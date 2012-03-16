/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.hbase.replication.producer;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.hbase.replication.hlog.HLogEntryZookeeperPersistence;
import com.alibaba.hbase.replication.hlog.HLogService;
import com.alibaba.hbase.replication.producer.crossidc.HReplicationProducer;
import com.alibaba.hbase.replication.producer.crossidc.HReplicationRejectRecoverScanner;
import com.alibaba.hbase.replication.protocol.DefaultHDFSFileAdapter;
import com.alibaba.hbase.replication.protocol.ProtocolAdapter;
import com.alibaba.hbase.replication.server.ReplicationConf;
import com.alibaba.hbase.replication.utility.ProducerConstants;
import com.alibaba.hbase.replication.utility.ZKUtil;
import com.alibaba.hbase.replication.zookeeper.NothingZookeeperWatch;
import com.alibaba.hbase.replication.zookeeper.RecoverableZooKeeper;

/**
 * 类ReplicationSinkManger.java的实现描述：producer端的任务线程管理
 * 
 * @author dongsh 2012-3-9 下午02:04:13
 */
@Service("replicationSinkManger")
public class ReplicationSinkManger {

    @Autowired
    protected ReplicationConf conf;

    public ReplicationConf getConf() {
        return conf;
    }

    public void start() throws Exception {
        RecoverableZooKeeper zookeeper = ZKUtil.connect(conf, new NothingZookeeperWatch());

        HLogService hlogService = new HLogService(conf);
        ProtocolAdapter adapter = new DefaultHDFSFileAdapter();

        adapter.init(conf);
        HLogEntryZookeeperPersistence hLogEntryPersistence = new HLogEntryZookeeperPersistence(conf);
        hLogEntryPersistence.setZookeeper(zookeeper);

        ThreadPoolExecutor replicationPool = new ThreadPoolExecutor(
                                                                    conf.getInt(ProducerConstants.CONFKEY_REP_SINK_POOL_SIZE,
                                                                                ProducerConstants.REP_SINK_POOL_SIZE),
                                                                    conf.getInt(ProducerConstants.CONFKEY_REP_SINK_POOL_SIZE,
                                                                                ProducerConstants.REP_SINK_POOL_SIZE),
                                                                    conf.getInt(ProducerConstants.CONFKEY_THREADPOOL_KEEPALIVE_TIME,
                                                                                100),
                                                                    TimeUnit.SECONDS,
                                                                    new ArrayBlockingQueue<Runnable>(
                                                                                                     conf.getInt(ProducerConstants.CONFKEY_THREADPOOL_SIZE,
                                                                                                                 100)));
        ThreadPoolExecutor scannerPool = new ThreadPoolExecutor(
                                                                conf.getInt(ProducerConstants.CONFKEY_REP_SCANNER_POOL_SIZE,
                                                                            ProducerConstants.REP_SCANNER_POOL_SIZE),
                                                                conf.getInt(ProducerConstants.CONFKEY_REP_SCANNER_POOL_SIZE,
                                                                            ProducerConstants.REP_SCANNER_POOL_SIZE),
                                                                conf.getInt(ProducerConstants.CONFKEY_THREADPOOL_KEEPALIVE_TIME,
                                                                            100),
                                                                TimeUnit.SECONDS,
                                                                new ArrayBlockingQueue<Runnable>(
                                                                                                 conf.getInt(ProducerConstants.CONFKEY_THREADPOOL_SIZE,
                                                                                                             100)));

        ThreadPoolExecutor recoverPool = new ThreadPoolExecutor(
                                                                conf.getInt(ProducerConstants.CONFKEY_REP_REJECT_POOL_SIZE,
                                                                            ProducerConstants.REP_REJECT_POOL_SIZE),
                                                                conf.getInt(ProducerConstants.CONFKEY_REP_REJECT_POOL_SIZE,
                                                                            ProducerConstants.REP_REJECT_POOL_SIZE),
                                                                conf.getInt(ProducerConstants.CONFKEY_THREADPOOL_KEEPALIVE_TIME,
                                                                            100),
                                                                TimeUnit.SECONDS,
                                                                new ArrayBlockingQueue<Runnable>(
                                                                                                 conf.getInt(ProducerConstants.CONFKEY_THREADPOOL_SIZE,
                                                                                                             100)));

        HLogGroupZookeeperScanner scan;
        for (int i = 0; i < conf.getInt(ProducerConstants.CONFKEY_REP_SCANNER_POOL_SIZE,
                                        ProducerConstants.REP_SCANNER_POOL_SIZE); i++) {
            scan = new HLogGroupZookeeperScanner(conf);
            scan.setZooKeeper(zookeeper);
            scan.setHlogService(hlogService);
            scan.setHlogEntryPersistence(hLogEntryPersistence);
            scannerPool.execute(scan);
        }

        HReplicationRejectRecoverScanner recover;
        for (int i = 0; i < conf.getInt(ProducerConstants.CONFKEY_REP_REJECT_POOL_SIZE,
                                        ProducerConstants.REP_REJECT_POOL_SIZE); i++) {
            recover = new HReplicationRejectRecoverScanner(conf);
            recover.setZooKeeper(zookeeper);
            recover.setHlogService(hlogService);
            recover.setAdapter(adapter);
            scannerPool.execute(recover);
        }

        HReplicationProducer producer;
        for (int i = 0; i < conf.getInt(ProducerConstants.CONFKEY_REP_SINK_POOL_SIZE,
                                        ProducerConstants.REP_SINK_POOL_SIZE); i++) {
            producer = new HReplicationProducer(conf);
            producer.setAdapter(adapter);
            producer.setHlogService(hlogService);
            producer.setHlogEntryPersistence(hLogEntryPersistence);
            replicationPool.execute(producer);
        }
    }
}
