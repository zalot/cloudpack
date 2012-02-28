package com.alibaba.hbase.replication.hlog.domain;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.regionserver.wal.HLog;

import com.alibaba.hbase.replication.hlog.HLogOperator;

/**
 * 描述一个 HLog 的基本信息
 * 
 * @author zalot.zhaoh
 *
 */
public class HLogInfo implements Comparable<HLogInfo>{

	/**
	 * 日志类型
	 * 
	 * @author zalot.zhaoh
	 * 
	 */
	public static enum HLogType {
		LIFE(1), OLD(2), END(3), UNKNOW(255);
		int typeValue;

		HLogType(int typeValue) {
			this.typeValue = typeValue;
		}

		public int getTypeValue() {
			return this.typeValue;
		}

		public static HLogType toType(int typeValue) {
			for (HLogType cp : HLogType.values()) {
				if (cp.getTypeValue() == typeValue)
					return cp;
			}
			return LIFE;
		}

		public static HLogType toType(Path path) {
			if (path != null && HLog.validateHLogFilename(path.getName())) {
				String url = path.toUri().getRawPath();
				// if(url.indexOf(HLogOperator.SYMBOL +
				// HLogOperator.HBASE_OLDLOG + HLogOperator.SYMBOL) > 0 ){
				// return HLogType.OLD;
				// }else if(url.indexOf(HLogOperator.SYMBOL +
				// HLogOperator.HBASE_LOG + HLogOperator.SYMBOL) > 0 ){
				// return HLogType.LIFE;
				// }
				if (url.indexOf(HLogOperator.HBASE_OLDLOG) > 0) {
					return HLogType.OLD;
				} else if (url.indexOf(HLogOperator.HBASE_LOG) > 0) {
					return HLogType.LIFE;
				}
			}
			return HLogType.UNKNOW;
		}
	}

	protected String name;
	protected Path path;
	protected HLogType type = HLogType.UNKNOW;
	protected long timestamp;

	public HLogInfo(String name, long timeStamp, Path path) {
		this.name = name;
		this.timestamp = timeStamp;
		this.path = path;
		this.type = HLogType.toType(path);
	}

	public String getName() {
		return name;
	}

	public Path getPath() {
		return path;
	}

	public HLogType getType() {
		return type;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPath(Path path) {
		this.path = path;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public int compareTo(HLogInfo o) {
		if(this.getTimestamp() > o.getTimestamp())
			return 1;
		if(o.getTimestamp() == this.getTimestamp())
			return 0;
		return -1;
	}
}
