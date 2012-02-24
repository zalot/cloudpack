package com.alibaba.hbase.replication.domain;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;

import com.alibaba.hbase.replication.domain.HLogInfo.HLogType;
import com.alibaba.hbase.replication.utility.HLogUtil;

/**
 * 默认 HLogsDomain
 * 
 * @author zalot.zhaoh
 *
 */
public class DefaultHLogs implements HLogs{
	//             name , path
	protected Map<String, Path> lifeHLogs = new ConcurrentHashMap<String, Path>();
	protected Map<String, Path>	oldHLogs = new ConcurrentHashMap<String, Path>();
	protected Map<String, HLogGroup> groups = new ConcurrentHashMap<String, HLogGroup>();
	
	protected Configuration conf = null;
	
	public Map<String, Path> get(HLogType type){
		if(type == null) return Collections.emptyMap();
		if(HLogType.LIFE == type){
			return lifeHLogs;
		}else if(HLogType.OLD == type){
			return oldHLogs;
		}
		return Collections.emptyMap();
	}
	
	public Path get(String name){
		Path path = null;
		path = lifeHLogs.get(name);
		if(path != null) return path;
		path = oldHLogs.get(name);
		return path;
	}
	
	public void put(Path path){
		if(path == null || path.getName().length() <=0 ) return;
		HLogInfo info = HLogUtil.getHLogInfo(path);
		if(info != null){
			if(HLogType.LIFE == info.getType())
				lifeHLogs.put(path.getName(), path);
			else if(HLogType.OLD == info.getType())
				oldHLogs.put(path.getName(), path);
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
	}
	
	public void put(List<Path> paths){
		if(paths == null || paths.size() <= 0) return;
		for(Path path : paths){
			put(path);
		}
	}
	
	public void clear(){
		lifeHLogs.clear();
		oldHLogs.clear();
	}

	public int size() {
		return lifeHLogs.size() + oldHLogs.size();
	}

	public Map<String, Path> life(){
		return lifeHLogs;
	}
	
	public Map<String, Path> old(){
		return lifeHLogs;
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
	public void setConf(Configuration conf) {
		this.conf = conf;
	}

	@Override
	public Configuration getConf() {
		return this.conf;
	}
}
