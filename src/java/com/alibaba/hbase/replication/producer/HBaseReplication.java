package com.alibaba.hbase.replication.producer;

import java.util.List;

import org.apache.hadoop.hbase.regionserver.wal.HLog.Entry;

/**
 * 复制
 * 
 * @author zalot.zhaoh
 *
 */
public interface HBaseReplication {
	public boolean put(Entry info);
	public boolean puts(List<Entry> infos);
}
