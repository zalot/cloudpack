package com.alibaba.hbase.replication.plugin;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.master.LogCleanerDelegate;

/**
 * 日志清理策略
 * 
 * @author zalot.zhaoh
 *
 */
public class NothingLogCleanerDelegate implements LogCleanerDelegate{

	@Override
	public Configuration getConf() {
		return null;
	}

	@Override
	public void setConf(Configuration arg0) {
		
	}

	@Override
	public void stop(String why) {
		
	}

	@Override
	public boolean isStopped() {
		return false;
	}

	@Override
	public boolean isLogDeletable(Path filePath) {
		return false;
	}
}
