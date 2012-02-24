package com.alibaba.hbase.replication.hlog;

import org.apache.hadoop.hbase.regionserver.wal.HLog.Entry;

import com.alibaba.hbase.replication.domain.HLogInfo;

public interface ReplicationCallBack {
	public boolean next(HLogInfo info, Entry entryInfo);
}
