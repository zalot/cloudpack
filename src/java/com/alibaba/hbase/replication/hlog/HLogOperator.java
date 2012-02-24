package com.alibaba.hbase.replication.hlog;

import java.util.List;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.regionserver.wal.HLog;
import org.apache.hadoop.hbase.regionserver.wal.HLog.Entry;

import com.alibaba.hbase.replication.domain.HLogInfo;
import com.alibaba.hbase.replication.domain.HLogInfo.HLogType;

/**
 * 日志操作接口
 * 
 * @author zalot.zhaoh
 *
 */
public interface HLogOperator {
	public static final String HBASE_LOG = ".logs";
	public static final String HBASE_OLDLOG = ".oldlogs";
	public static final String SYMBOL = "/";
	
	public static class EntryInfo {
		@Override
		public String toString() {
			return "EntryInfo [entry=" + entry + ", type=" + type + ", pos="
					+ pos + ", fileName=" + fileName + "]";
		}

		HLog.Entry entry = null;
		HLogType type;
		long pos;
		String fileName;

		public String getFileName() {
			return fileName;
		}

		public HLog.Entry getEntry() {
			return entry;
		}

		public HLogType getType() {
			return type;
		}

		public long getPos() {
			return pos;
		}

		public EntryInfo(Entry entry, HLogType type, String fileName, long pos) {
			this.entry = entry;
			this.type = type;
			this.pos = pos;
			this.fileName = fileName;
		}

		public EntryInfo(Entry entry, Path path, long pos) {
		}
	}

	public Entry next();
	public HLogReader getReader(HLogInfo info);
	public boolean commit();
	public boolean sync();
}
