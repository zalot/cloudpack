package com.alibaba.hbase.replication.producer;

import java.util.List;

import com.alibaba.hbase.replication.hlog.HLogOperator.EntryInfo;

public interface HBaseReplication {
	public boolean put(EntryInfo info);
	public boolean puts(List<EntryInfo> infos);
}
