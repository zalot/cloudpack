package com.alibaba.hbase.replication.protocol;

public interface MetaData {
	public Head getHead();
	public Body getBody();
}
