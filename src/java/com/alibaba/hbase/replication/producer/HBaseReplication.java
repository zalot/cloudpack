package com.alibaba.hbase.replication.producer;

import java.util.List;

import com.alibaba.hbase.replication.hlog.HLogOperator.EntryInfo;

/**
 * 复制
 * 
 * @author zalot.zhaoh
 *
 */
public interface HBaseReplication {
	public boolean put(EntryInfo info);
	public boolean puts(List<EntryInfo> infos);
}
