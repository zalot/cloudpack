package com.alibaba.hbase.replication.hlog.domain;

import java.util.Collection;
import java.util.List;

import org.apache.hadoop.fs.Path;

/**
 * HLog Java 数据结构对象
 * 1. 存储的 HLog 的 Path
 * 2. 根据存储的 Path 并进行分组
 * 3. 分组内容排序
 * 
 * @author zalot.zhaoh
 * 
 */
public interface HLogs{
	public void put(Path path);
	public void put(List<Path> paths);
	// 通过名字取得 Group
	public HLogGroup getGroup(String name);
	// 取得所有Groups
	public Collection<HLogGroup> getGroups();
	public void clear();
	public int size();
	public HLogInfo findByName(String name);
	public HLogGroup findGroup(Path path);
	public HLogGroup nextGroup();
}
