package com.alibaba.hbase.replication.hlog;

import org.apache.hadoop.fs.FileSystem;

import com.alibaba.hbase.replication.hlog.domain.HLogEntry;

/**
 * 事务读取
 * 
 * @author zalot.zhaoh
 *
 */
public class TransactionHLogReader extends DefaultHLogReader{

	public TransactionHLogReader(FileSystem fs, HLogEntry info) {
	}
}
