package org.alibaba.hbase.replication.domain;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.alibaba.hbase.replication.domain.HLogInfo.HLogType;
import org.alibaba.hbase.replication.utility.HLogUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;

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
	protected Map<String, HLogReaderGroup> groups = new ConcurrentHashMap<String, HLogReaderGroup>();
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
		HLogType type = HLogType.toType(path);
		if(HLogType.LIFE == type){
			lifeHLogs.put(path.getName(), path);
			putGroup(path, type);
		}else if(HLogType.OLD == type){
			oldHLogs.put(path.getName(), path);
			putGroup(path, type);
		}
	}
	
	public void putGroup(Path path , HLogType type){
		HLogInfo info = HLogUtil.getHLogInfo(path);
		HLogReaderGroup group = groups.get(info.getName());
		if(group == null)
		{
			group = new HLogReaderGroup(info.getName());
			groups.put(info.getName(), group);
		}
	}
	
	public void put(Path path, HLogType type){
		if(HLogType.LIFE == type){
			lifeHLogs.put(path.getName(), path);
			putGroup(path, type);
		}else if(HLogType.OLD == type){
			oldHLogs.put(path.getName(), path);
			putGroup(path, type);
		}
	}
	
	public void put(List<Path> paths, HLogType type){
		if(type == null || paths == null || paths.size() <= 0) return;
		if(HLogType.LIFE == type){
			for(Path path : paths){
				lifeHLogs.put(path.getName(), path);
				putGroup(path, type);
			}
		}else if(HLogType.OLD == type){
			for(Path path : paths){
				oldHLogs.put(path.getName(), path);
				putGroup(path, type);
			}
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
	public HLogReaderGroup getGroup(String name) {
		return null;
	}

	@Override
	public List<HLogReaderGroup> getGroups() {
		return null;
	}

	@Override
	public void logClear() {
		lifeHLogs.clear();
		oldHLogs.clear();
	}

	@Override
	public void groupClear() {
		
	}

	@Override
	public int logSize() {
		return 0;
	}

	@Override
	public int groupSize() {
		return 0;
	}

	@Override
	public Path getPath(String name) {
		return null;
	}

	@Override
	public boolean isOver() {
		return false;
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
