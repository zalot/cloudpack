package com.alibaba.hbase.replication.hlog;

import com.alibaba.hbase.replication.domain.HLogInfo;
import org.apache.hadoop.fs.FileSystem;

/**
 * 事务读取
 * 
 * @author zalot.zhaoh
 *
 */
public class TransactionHLogReader extends LazyOpenHLogReader{

	public TransactionHLogReader(FileSystem fs, HLogInfo info) {
		super(fs, info);
	}
}
