package com.alibaba.hbase.replication.producer;

import com.alibaba.hbase.replication.hlog.HLogOperator.EntryInfo;

public interface HBaseReplicationListener {
	public void preCommit(EntryInfo info);
}
