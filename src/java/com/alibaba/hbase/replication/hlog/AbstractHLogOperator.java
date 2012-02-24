package com.alibaba.hbase.replication.hlog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.regionserver.wal.HLog;
import org.apache.zookeeper.KeeperException;

import com.alibaba.hbase.replication.domain.DefaultHLogs;
import com.alibaba.hbase.replication.domain.HLogInfo;
import com.alibaba.hbase.replication.domain.HLogs;
import com.alibaba.hbase.replication.utility.HLogUtil;

/**
 * 日志操作
 * 
 * @author zalot.zhaoh
 * 
 */
public abstract class AbstractHLogOperator implements HLogOperator{
	
	protected static final Log LOG = LogFactory.getLog(AbstractHLogOperator.class);
	protected long lastSyncTime = 0;
	protected long minSyncTime = 10000;
	
	public static List<Path> getHLogsByHDFS(FileSystem fs, Path path)
			throws IOException {
		List<Path> hlogs = new ArrayList<Path>();
		if (!fs.exists(path))
			return hlogs;
		if (fs.isFile(path)) {
			if (HLog.validateHLogFilename(path.getName()))
				hlogs.add(path);
		} else {
			for (FileStatus fst : fs.listStatus(path)) {
				List<Path> childHlogs = getHLogsByHDFS(fs, fst.getPath());
				for (Path cdPath : childHlogs)
					hlogs.add(cdPath);
			}
		}
		return hlogs;
	}
	
	protected String dfsRoot;
	protected Configuration conf;
	protected FileSystem fs;
	protected HLogs _hogs = new DefaultHLogs();
	
	public AbstractHLogOperator(Configuration conf) throws IOException, KeeperException, InterruptedException {
		this(conf, null);
	}

	public AbstractHLogOperator(Configuration conf, FileSystem fs) throws IOException, KeeperException, InterruptedException {
		this.dfsRoot = conf.get("hbase.rootdir");
		if(fs == null)
			this.fs = FileSystem.get(conf);
		else
			this.fs = fs;
		this.conf = conf;
	}

	
	public List<Path> getLifeHLogs() {
		try {
			return getHLogsByHDFS(fs, new Path(dfsRoot + SYMBOL + HBASE_LOG));
		} catch (IOException e) {
			LOG.error(e);
		}
		return new ArrayList<Path>();
	}
	
	public List<Path> getOldHLogs() {
		try {
			return getHLogsByHDFS(fs, new Path(dfsRoot + SYMBOL + HBASE_OLDLOG));
		} catch (IOException e) {
			LOG.error(e);
		}
		return new ArrayList<Path>();
	}
	
	public boolean sync() {
		synchronized (_hogs) {
			try{
				if(lastSyncTime + minSyncTime < System.currentTimeMillis()){
					LOG.debug("[SYNC][Start]----------------------------------------------");
					// 取得当前HDFS下所有的HLog，并清理映射表
					List<Path> lifeHLogsPaths = getLifeHLogs();
					// TODO 优化 5
					// 无需要读到所有 OLD 的 日志
					// 可以根据 清空来少读
					List<Path> oldHLogsPaths = getOldHLogs();
					//数据放入内存
					_hogs.clear();
					_hogs.put(lifeHLogsPaths);
					// TODO 优化 5
					// 无需要读到所有 OLD 的 日志
					// 可以根据 清空来少读
					_hogs.put(oldHLogsPaths);
					lastSyncTime = System.currentTimeMillis();
					LOG.debug("[SYNC][End][canRead"+ _hogs.size() +"]----------------------------------------------");
					return true;
				}else{
					LOG.debug("[SYNC] wait " + minSyncTime + " millisecond");
					return false;
				}
				
			}catch(Exception e){
				// 如果同步出错，则清空内存中的同步缓存，不再同步任何数据，直到回复为止
				_hogs.clear();
				LOG.error(e);
				// TODO :: 需要报警
				return false;
			}
		}
	}
	
	public long getSleepTime(){
		return minSyncTime;
	}
	
//	@Override
//	public boolean commit(List<EntryInfo> entryInfos) {
//		List<String> tmpFiles = new ArrayList<String>();
//		for(EntryInfo info : entryInfos){
//			if(!tmpFiles.contains(info.getFileName())){
//				tmpFiles.add(info.getFileName());
//			}
//		}
//		HLogReader reader;
//		HLogZNode znode;
//		HLogType type = HLogType.LIFE;
//		try{
//			for(String fileName : tmpFiles){
//				reader = canReadHLogReaders.get(fileName);
//				if(reader != null){
//					znode = getHLogZNodeByFileName(fileName);
//					if(znode != null){
//						znode.setPos(reader.getPosition());
//						updateHLogZNode(znode);
//					}else{
//						znode = new HLogZNode(fileName,type, reader.getPosition());
//						createHLogZNode(znode);
//					}
//				}else{
//					throw new Exception("commit error , file no found [" + fileName + "]");
//				}
//			}
//			return true;
//		}catch(Exception e){
//			return false;
//		}
//	}
//	
//	public HLogReader getReader(Path path) throws IOException{
//		HLogZNode znode = getHLogZNodeByFileName(path.getName());
//		HLogType type = getHLogType(path.getName());
//		if(type == HLogType.UNKNOW)
//			return null;
//		
//		if(znode == null){
//			try {
//				znode = HLogZNode.create(path.getName(), type);
//				createHLogZNode(znode);
//			} catch (Exception e) {
//				LOG.error(e);
//				return null;
//			}
//		}
//
//		// 如果已经读完
//		if(type == HLogType.OLD && znode.getType() == HLogType.END){
//			LOG.info("[HLogReader.getReader][TYPE.END]" + Thread.currentThread().getName() + "  [FILE]" + path.getName());
//			return null;
//		}
//		
//		//修复错误数据
//		if(type == HLogType.LIFE && znode.getType() == HLogType.END){
//			znode.setType(type);
//			try{
//				updateHLogZNode(znode);
//			}catch(Exception e){
//				LOG.error("miss Zookeeper !");
//			}
//		}else if(type == HLogType.OLD && znode.getType() == HLogType.LIFE){
//			znode.setType(HLogType.OLD);
//			try{
//				updateHLogZNode(znode);
//			}catch(Exception e){
//				LOG.error("miss Zookeeper !");
//			}
//		}
//		
//		HLogReader reader = new LazyOpenHLogReader(fs, path, type, conf);
//		if(znode != null && znode.getPos() > 0){
//			reader.seek(znode.getPos());
//		}
//
//		LOG.info("[HLogReader.getReader][ZNode.TYPE."+ znode.getType() +"][HLog.TYPE." + type + "]" + reader);
//		return reader;
//	}
	
	@Override
	public HLogReader getReader(HLogInfo info) {
		if(info != null)
			return new LazyOpenHLogReader(fs, info);
		return null;
	}
}
