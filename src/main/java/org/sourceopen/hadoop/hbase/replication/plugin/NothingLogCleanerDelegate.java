package org.sourceopen.hadoop.hbase.replication.plugin;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.master.LogCleanerDelegate;

/**
 * 日志清理策略<BR>
 * 主要是 HBase 的 plugin <BR>
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
