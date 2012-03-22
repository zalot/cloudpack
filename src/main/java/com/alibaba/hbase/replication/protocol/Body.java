package com.alibaba.hbase.replication.protocol;

import org.apache.hadoop.hbase.KeyValue;

/**
 * 基础协议体 <BR>
 * 
 * 类Body.java的实现描述：TODO 类实现描述 
 * @author zalot.zhaoh Mar 22, 2012 3:11:22 PM
 */
public abstract class Body {

    public abstract void setBodyData(byte[] data) throws Exception;

    public abstract byte[] getBodyData() throws Exception;

    public abstract void putKeyValue(String tableName, KeyValue kv);

}
