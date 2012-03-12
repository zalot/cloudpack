/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.hbase.replication.producer;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.zookeeper.KeeperException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.hbase.replication.protocol.FileAdapter;
import com.alibaba.hbase.replication.server.ReplicationConf;
import com.alibaba.hbase.replication.utility.ProducerConstants;

/**
 * 类ReplicationSinkManger.java的实现描述：producer端的任务线程管理
 * 
 * @author dongsh 2012-3-9 下午02:04:13
 */
@Service("replicationSinkManger")
public class ReplicationSinkManger {

    @Autowired
    protected ReplicationConf conf;

    @Autowired
    protected FileAdapter     fileAdapter;

    public void start() throws KeeperException, InterruptedException, IOException {
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

        for (int i = 0; i < conf.getInt(ProducerConstants.CONFKEY_REP_SCANNER_POOL_SIZE,
                                        ProducerConstants.REP_SCANNER_POOL_SIZE); i++) {
            scannerPool.execute(new HLogGroupZookeeperScanner(conf));
        }
        for (int i = 0; i < conf.getInt(ProducerConstants.CONFKEY_REP_SINK_POOL_SIZE,
                                        ProducerConstants.REP_SINK_POOL_SIZE); i++) {
            replicationPool.execute(new CrossIDCHBaseReplicationSink(conf, fileAdapter));
        }
    }
}
