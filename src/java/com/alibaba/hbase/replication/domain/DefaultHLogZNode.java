package com.alibaba.hbase.replication.domain;

import com.alibaba.hbase.replication.domain.HLogInfo.HLogType;
import org.apache.hadoop.fs.Path;

public class DefaultHLogZNode implements HLogZNode {
	protected static final String SEPARATOR = ",";

	public static HLogZNode create(Path path, HLogType type) {
		return new DefaultHLogZNode(path, type, 0);
	}

	private Path path;

	private long pos;

	private HLogType type;

	private int version;

	public DefaultHLogZNode(Path path, byte[] data) {
		String[] sdata = new String(data).split(SEPARATOR);
		this.path = path;
		this.type = HLogType.toType(Integer.valueOf(sdata[0]));
		this.pos = Long.valueOf(sdata[1]);
	}

	public DefaultHLogZNode(Path path, HLogType type, long pos) {
		this.path = path;
		this.type = type;
		this.pos = pos;
	}

	public byte[] getData() {
		String data = type.getTypeValue() + SEPARATOR + pos;
		return data.getBytes();
	}

	public Path getPath() {
		return path;
	}

	public long getPos() {
		return pos;
	}

	public HLogType getType() {
		return type;
	}

	public int getVersion() {
		return version;
	}

	public void setPath(Path path) {
		this.path = path;
	}

	public void setPos(long pos) {
		this.pos = pos;
	}

	public void setType(HLogType type) {
		this.type = type;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	@Override
	public String toString() {
		return "HLogZNode [type=" + type + ", pos=" + pos + ", path=" + path
				+ "]";
	}
}
