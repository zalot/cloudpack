package com.alibaba.hbase.replication.hlog;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.regionserver.wal.HLog.Entry;
import org.apache.zookeeper.KeeperException;


/**
 * 暂时未实现完成，单线程HLogOperator
 * 
 * @author zalot.zhaoh
 */
public class DefaultHLogOperatorImpl extends ZookeeperHLogOperator {

    public DefaultHLogOperatorImpl(Configuration conf) throws IOException, KeeperException, InterruptedException{
        super(conf);
    }

    @Override
    public Entry next() {
        return null;
    }

    @Override
    public boolean commit() {
        return false;
    }

    @Override
    public void start() {
        
    }

}
