package org.alibaba.hbase.replication.hlog;

import org.alibaba.hbase.replication.domain.HLogInfo;
import org.apache.hadoop.fs.FileSystem;

public class TransactionHLogReader extends LazyOpenHLogReader{

	public TransactionHLogReader(FileSystem fs, HLogInfo info) {
		super(fs, info);
	}
}
