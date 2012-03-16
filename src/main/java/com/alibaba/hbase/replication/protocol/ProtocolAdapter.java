package com.alibaba.hbase.replication.protocol;

import java.util.List;

import org.apache.hadoop.conf.Configuration;

/**
 * 协议适配器 类ProtocolAdapter.java的实现描述：TODO 类实现描述
 * 
 * @author zalot.zhaoh Feb 28, 2012 2:26:38 PM
 */
public interface ProtocolAdapter {

    public void write(MetaData data) throws Exception;

    public MetaData read(Head head) throws Exception;
    
    public List<Head> listHead() throws Exception;

    public void recover(MetaData data) throws Exception;

    public void init(Configuration conf);

    public List<Head> listRejectHead();
}
