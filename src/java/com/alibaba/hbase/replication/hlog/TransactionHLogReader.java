package com.alibaba.hbase.replication.hlog;

import com.alibaba.hbase.replication.domain.HLogInfo;
import org.apache.hadoop.fs.FileSystem;

public class TransactionHLogReader extends LazyOpenHLogReader{

	public TransactionHLogReader(FileSystem fs, HLogInfo info) {
		super(fs, info);
	}
}
