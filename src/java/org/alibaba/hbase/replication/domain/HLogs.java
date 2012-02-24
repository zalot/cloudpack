package org.alibaba.hbase.replication.domain;

import java.util.List;
import java.util.Map;

import org.alibaba.hbase.replication.domain.HLogInfo.HLogType;
import org.apache.hadoop.conf.Configurable;
import org.apache.hadoop.fs.Path;

public interface HLogs extends Configurable {
	public void put(Path path);

	// 通过名字取得 Group
	public HLogReaderGroup getGroup(String name);

	// 取得所有Groups
	public List<HLogReaderGroup> getGroups();

	public void put(Path path, HLogType type);

	public void put(List<Path> paths, HLogType type);

	public void clear();

	public void logClear();

	public void groupClear();

	public int logSize();

	public int groupSize();

	public Map<String, Path> life();

	public Map<String, Path> old();

	public Path getPath(String name);

	public boolean isOver();
}
