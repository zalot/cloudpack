package org.alibaba.hbase.replication.hlog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.alibaba.hbase.replication.domain.HLogInfo.HLogType;
import org.alibaba.hbase.replication.domain.HLogZNode;
import org.alibaba.hbase.replication.zookeeper.HLogZNodeOperator;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.regionserver.wal.HLog;
import org.apache.zookeeper.KeeperException;

/**
 * 暂时未实现完成，单线程HLogOperator
 * 
 * @author zalot.zhaoh
 *
 */
public class DefaultHLogOperatorImpl extends AbstractHLogOperator{
	// String is FileName 
	protected Map<String,HLogReader> lifeHLogsReader = new ConcurrentHashMap<String, HLogReader>();
	protected Map<String,HLogReader> oldHLogsReader = new ConcurrentHashMap<String, HLogReader>();
	protected boolean hasSyncHDFSandZk = false;
	
	HLogZNodeOperator znodeOperator;
	public DefaultHLogOperatorImpl(Configuration conf) throws IOException, KeeperException, InterruptedException {
		super(conf, null);
	}
	
	/**
	 * 结束 EntryInfo 所在的ZNode
	 * 如果该 EntryInfo 是 OLD ，则被调用的修复
	 * 包含 Znode 和 内存
	 * 
	 * @param entryInfo
	 */
	private void tagEnd(String fileName) {
//		LOG.info("wait TagEnd [" + fileName + "]");
//		if(fileName == null) return;
//		HLogZNode znode;
//		try {
//			znode = znodeOperator.get(fileName);
//			if(znode != null){
//				znode.setType(HLogType.END);
//				updateHLogZNode(znode);
//			}
//			oldHLogsReader.remove(fileName);
//			LOG.info("TAGEND [" + fileName + "]");
//		} catch (Exception e) {
//		}
	}

	public boolean sync() {
		try{
//			if(allHlogsSync()){
//				
//			}
			return true;
		}catch(Exception e){
			// 如果同步出错，则清空内存中的同步缓存，不再同步任何数据，直到回复为止
			LOG.error(e);
			// TODO :: 需要报警
			return false;
		}
	}
	
	
	public Path getHLogZNodePath(HLogZNode znode){
		String basePath = "";
		if(znode.getType() == HLogType.OLD || znode.getType() == HLogType.END){
			basePath = HBASE_OLDLOG;
		}else if(znode.getType() == HLogType.LIFE)
			basePath = HBASE_LOG;
		else{
			return null;
		}
		return new Path(dfsRoot + SYMBOL + basePath + SYMBOL + znode.getPath().getName());
	}
	
	public Map<String, HLogReader> getMemReaderMapByType(HLogType type){
		Map<String, HLogReader> memHlogsReader = null;
		switch(type){
			case LIFE: memHlogsReader = lifeHLogsReader;break;
			case OLD: memHlogsReader = oldHLogsReader;break;
			default : memHlogsReader = oldHLogsReader; break;
		}
		return memHlogsReader;
	}
	
	public HLogReader getMemHLogReader(String fileName){
		HLogReader reader = lifeHLogsReader.get(fileName);
		if(reader != null ) return reader;
		reader = oldHLogsReader.get(fileName);
		return reader;
	}
	
	public EntryInfo next() {
		try{
			EntryInfo entryInfo = null;
			HLogReader reader;
			List<String> waitEnd = new ArrayList<String>();
			for(String fileName : oldHLogsReader.keySet()){
				LOG.debug("[Next] OldReader " + fileName);
				reader = oldHLogsReader.get(fileName);
				HLog.Entry entry = reader.next();
				// 如果是 OLD 没读到数据，则判断是否从内存中删除该Reader，且把Zoo的状态改变
				if(entry != null){
					LOG.debug("[Next][-T-] Next OldReader " + fileName);
					entryInfo = new EntryInfo(entry, HLogType.OLD, fileName, reader.getPosition());
					break;
				}else{
					LOG.debug("[Next][-F-] Next OldReader " + fileName);
					waitEnd.add(fileName);
				}
			}
			
			for(String fn : waitEnd){
				tagEnd(fn);
			}
			
			if(entryInfo == null){
				for(String fileName : lifeHLogsReader.keySet()){
					LOG.debug("[Next] Next LifeReader " + fileName);
					reader = lifeHLogsReader.get(fileName);
					HLog.Entry entry = reader.next();
					if(entry != null){
						LOG.debug("[Next][-T-] Next LifeReader " + fileName);
						entryInfo = new EntryInfo(entry, HLogType.LIFE, fileName, reader.getPosition());
						break;
					}else{
						LOG.debug("[Next][-F-] Next LifeReader " + fileName);
					}
				}
			}
			
			//如果发现没有数据可以再read则更新缓存查看是否有新的日志出现
			if(entryInfo == null)
			{
				sync();
			}
			
			return entryInfo;
		}catch(Exception e){
			return null;
		}
	}
	
	public HLogReader getReader(Path path) throws IOException{
		return getHLogReader(path);
	}
	
	protected HLogReader getHLogReader(Path path) throws IOException{
//		HLogZNode znode = getHLogZNodeByFileName(path.getName());
//		HLogReader reader = new LazyOpenHLogReader(fs, path, getHLogType(path.getName()), conf);
//		if(znode != null && znode.getPos() > 0){
//			reader.seek(znode.getPos());
//		}
		return null;
	}

	public HLogReader getReader() throws Exception {
		return null;
	}

	public boolean commit(EntryInfo entryInfo) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean commit(List<EntryInfo> entryInfos) {
		// TODO Auto-generated method stub
		return false;
	}

}