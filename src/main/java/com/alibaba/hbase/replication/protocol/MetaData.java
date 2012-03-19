package com.alibaba.hbase.replication.protocol;

/**
 * 协议元数据
 * 
 * 类MetaData.java的实现描述：TODO 类实现描述 
 * @author zalot.zhaoh Mar 19, 2012 11:51:21 AM
 */
public interface MetaData {

    public Head getHead();

    public Body getBody();

    public byte[] getBodyData();

    public void setBodyData(byte[] data);
}
