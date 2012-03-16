package com.alibaba.hbase.replication.protocol;

public interface MetaData {
	public Head getHead();
	public Body getBody();
	public byte[] getBodyData();
	public void setBodyData(byte[] data);
}
