package com.alibaba.hbase.replication.hlog;

public interface HLogOperatorTransaction {
	public boolean process(ReplicationCallBack call);
	public boolean process(ReplicationCallBack call, int count);
}
