/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.hbase.replication.consumer;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 类DataLoadingManager.java的实现描述：持有consumer端的HbaseClient加载数据线程池
 * 
 * @author dongsh 2012-2-28 下午04:09:01
 */
public class DataLoadingManager {

    private static final Logger LOG         = LoggerFactory.getLogger(FileChannelManager.class);
    private ThreadPoolExecutor loadingPool;
    private int                coreLoadingPoolSize = 30;
    private int                maxLoadingPoolSize  = 30;
    private int                queueSize           = 100;
    private int                keepAliveTime       = 100;
    
    public void start(){
        if (LOG.isInfoEnabled()) {
            LOG.info("DataLoadingManager start.");
        }
        loadingPool = new ThreadPoolExecutor(this.coreLoadingPoolSize, this.maxLoadingPoolSize, this.keepAliveTime,
                                             TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(this.queueSize));
        
    }
}
