package com.alibaba.hbase.replication.domain;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;

import com.alibaba.hbase.replication.utility.HLogUtil;

/**
 * 默认 HLogsDomain
 * 
 * @author zalot.zhaoh
 *
 */
public class DefaultHLogs implements HLogs{
	//             name , path
	protected Map<String, HLogGroup> groups = new ConcurrentHashMap<String, HLogGroup>();
	
	protected Configuration conf = null;
	
	public Path get(String name){
		Path path = null;
		return path;
	}
	
	public void put(Path path){
		if(path == null || path.getName().length() <=0 ) return;
		HLogInfo info = HLogUtil.getHLogInfo(path);
		if(info != null){
			putGroup(info);
		}
	}
	
	protected void putGroup(HLogInfo info){
		HLogGroup group = groups.get(info.getName());
		if(group == null)
		{
			group = new HLogGroup(info.getName());
			groups.put(info.getName(), group);
		}
		group.put(info);
	}
	
	public void put(List<Path> paths){
		if(paths == null || paths.size() <= 0) return;
		for(Path path : paths){
			put(path);
		}
	}
	
	public void clear(){
	    groups.clear();
	}

	public int size() {
		return groups.size();
	}

	@Override
	public HLogGroup getGroup(String name) {
		return groups.get(name);
	}

	@Override
	public Collection<HLogGroup> getGroups() {
		return groups.values();
	}

	@Override
	public HLogInfo findByName(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HLogGroup findGroup(Path path) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HLogGroup nextGroup() {
		synchronized (groups) {
			return groups.values().iterator().next();
		}
	}
}
