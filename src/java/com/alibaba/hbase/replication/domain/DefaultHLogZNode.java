package com.alibaba.hbase.replication.domain;

import com.alibaba.hbase.replication.domain.HLogInfo.HLogType;
import org.apache.hadoop.fs.Path;

public class DefaultHLogZNode implements HLogZNode{
	public HLogType getType() {
		return type;
	}

	public void setType(HLogType type) {
		this.type = type;
	}

	public long getPos() {
		return pos;
	}

	public void setPos(long pos) {
		this.pos = pos;
	}

	@Override
	public String toString() {
		return "HLogZNode [type=" + type + ", pos=" + pos + ", path="
				+ path + "]";
	}

	
	protected static final String SEPARATOR = ",";
	
	private HLogType type;
	private Path path;
	public Path getPath() {
		return path;
	}

	public void setPath(Path path) {
		this.path = path;
	}


	private long pos;
	private int version;
	
	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public DefaultHLogZNode(Path path, HLogType type, long pos){
		this.path = path;
		this.type = type;
		this.pos = pos;
	}
	
	public DefaultHLogZNode(Path path, byte[] data){
		String[] sdata = new String(data).split(SEPARATOR);
		this.path = path;
		this.type = HLogType.toType(Integer.valueOf(sdata[0]));
		this.pos = Long.valueOf(sdata[1]);
	}
	
	public byte[] getData(){
		String data = type.getTypeValue() + SEPARATOR + pos;
		return data.getBytes();
	}
	
	public static HLogZNode create(Path path, HLogType type){
		return new DefaultHLogZNode(path, type, 0);
	}
}
