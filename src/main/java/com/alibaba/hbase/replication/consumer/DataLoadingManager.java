/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.hbase.replication.consumer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.HTablePool;
import org.apache.hadoop.hbase.client.Put;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.hbase.replication.protocol.Body.Edit;
import com.alibaba.hbase.replication.server.ReplicationConf;
import com.alibaba.hbase.replication.utility.ConsumerConstants;

/**
 * 类DataLoadingManager.java的实现描述：持有consumer端的HbaseClient加载数据线程池
 * 
 * @author dongsh 2012-2-28 下午04:09:01
 */
@Service("dataLoadingManager")
public class DataLoadingManager {

    private static final Logger LOG = LoggerFactory.getLogger(DataLoadingManager.class);
    protected ReplicationConf   conf;

    public ReplicationConf getConf() {
        return conf;
    }

    public void setConf(ReplicationConf conf) {
        this.conf = conf;
    }

    protected HTablePool pool;
    protected int        batchSize;

    public void init() {
        if (LOG.isInfoEnabled()) {
            LOG.info("DataLoadingManager start.");
        }
        batchSize = conf.getInt(ConsumerConstants.CONFKEY_REP_DATA_LAODING_BATCH_SIZE, 1000);
        pool = new HTablePool(conf, conf.getInt(ConsumerConstants.CONFKEY_REP_DATA_LAODING_POOL_SIZE, 30));
    }

    /**
     * 线程安全的批量数据加载(针对DML数据Mutation，DDL不作批量)
     * 
     * @param tableName
     * @param editList
     */
    public void batchLoad(String tableName, List<Edit> editList) {
        if (StringUtils.isBlank(tableName) || CollectionUtils.isEmpty(editList)) {
            if (LOG.isWarnEnabled()) {
                LOG.warn("param error: tableName: " + tableName);
            }
            return;
        }
        List<Put> puts = new ArrayList<Put>();
        List<Delete> deletes = new ArrayList<Delete>();
        for (Edit e : editList) {
            switch (e.getType()) {
                case Put:
                    Put p = new Put(e.getRowKey(), e.getTimeStamp());
                    p.setClusterId(ConsumerConstants.SLAVE_CLUSTER_ID);
                    p.add(e.getFamily(), e.getQualifier(), e.getValue());
                    puts.add(p);
                    break;
                case Delete:
                    Delete d = new Delete(e.getRowKey());
                    d.deleteColumn(e.getFamily(), e.getQualifier(), e.getTimeStamp());
                    d.setClusterId(ConsumerConstants.SLAVE_CLUSTER_ID);
                    deletes.add(d);
                    break;
                case DeleteColumn:
                    Delete dc = new Delete(e.getRowKey());
                    dc.deleteColumns(e.getFamily(), e.getQualifier(), e.getTimeStamp());
                    dc.setClusterId(ConsumerConstants.SLAVE_CLUSTER_ID);
                    deletes.add(dc);
                    break;
                case DeleteFamily:
                    Delete df = new Delete(e.getRowKey());
                    df.deleteFamily(e.getFamily(), e.getTimeStamp());
                    df.setClusterId(ConsumerConstants.SLAVE_CLUSTER_ID);
                    deletes.add(df);
                    break;
                default:
                    if (LOG.isWarnEnabled()) {
                        LOG.warn("edit type error: tableName: " + tableName + " edit: " + e);
                    }
                    break;
            }
        }
        HTableInterface table = pool.getTable(tableName);
        for (int i = 0; i < puts.size(); i += batchSize) {
            try {
                table.put(puts.subList(i, i + batchSize < puts.size() ? i + batchSize : puts.size()));
            } catch (IOException e1) {
                if (LOG.isErrorEnabled()) {
                    LOG.error("puts error,tableName: " + tableName);
                }
                // FIXME 异常数据是否需要保存起来？
            }
        }
        for (int i = 0; i < deletes.size(); i += batchSize) {
            try {
                table.delete(deletes.subList(i, i + batchSize < deletes.size() ? i + batchSize : deletes.size()));
            } catch (IOException e1) {
                if (LOG.isErrorEnabled()) {
                    LOG.error("deletes error,tableName: " + tableName);
                }
                // FIXME 异常数据是否需要保存起来？
            }
        }
        try {
            // pool.put方法已经被deprecated掉了
            table.close();
        } catch (IOException e1) {
            if (LOG.isErrorEnabled()) {
                LOG.error("pool puts table error,tableName: " + tableName);
            }
        }
    }
}
