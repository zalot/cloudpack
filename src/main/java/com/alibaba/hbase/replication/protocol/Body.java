package com.alibaba.hbase.replication.protocol;

import org.apache.hadoop.hbase.KeyValue;

public abstract class Body {

    public abstract void setBodyData(byte[] data) throws Exception;

    public abstract byte[] getBodyData() throws Exception;

    public abstract void putKeyValue(String tableName, KeyValue kv);

}
