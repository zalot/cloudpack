package com.alibaba.hbase.replication.domain;

import java.util.List;
import java.util.Map;

import com.alibaba.hbase.replication.domain.HLogInfo.HLogType;
import org.apache.hadoop.conf.Configurable;
import org.apache.hadoop.fs.Path;

/**
 * @author zalot.zhaoh
 *
 */
public interface HLogs extends Configurable {
	public void put(Path path);
	public void put(List<Path> paths);
	// 通过名字取得 Group
	public HLogGroup getGroup(String name);
	// 取得所有Groups
	public List<HLogGroup> getGroups();
	public void clear();
	public Map<String, Path> life();
	public Map<String, Path> old();
	public Path getPath(String name);
}
