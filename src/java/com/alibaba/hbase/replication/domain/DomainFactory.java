package com.alibaba.hbase.replication.domain;

import org.apache.hadoop.fs.Path;

import com.alibaba.hbase.replication.domain.HLogInfo.HLogType;

public class DomainFactory {
	public static HLogZNode createHLogZNode(Path path, byte[] data, int version){
		DefaultHLogZNode znode = new DefaultHLogZNode(path, data);
		znode.setVersion(version);
		return znode;
	}
	
	public static HLogZNode createHLogZNode(Path path, HLogType type, long pos, int version){
		DefaultHLogZNode znode = new DefaultHLogZNode(path, type, pos);
		znode.setVersion(version);
		return znode;
	}
}
