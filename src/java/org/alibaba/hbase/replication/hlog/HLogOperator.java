package org.alibaba.hbase.replication.hlog;

import java.util.List;

import org.alibaba.hbase.replication.domain.HLogInfo.HLogType;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.regionserver.wal.HLog;
import org.apache.hadoop.hbase.regionserver.wal.HLog.Entry;

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

	public EntryInfo next();
	public boolean commit(EntryInfo entryInfo);
	public boolean commit(List<EntryInfo> entryInfos);
	public boolean sync();
}
