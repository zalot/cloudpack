package org.alibaba.hbase.replication.producer;

import org.alibaba.hbase.replication.hlog.HLogOperator.EntryInfo;

public interface HBaseReplicationListener {
	public void preCommit(EntryInfo info);
}
