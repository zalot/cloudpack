package com.alibaba.hbase.replication.hlog;

import java.io.IOException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.regionserver.wal.HLog.Entry;
import org.apache.zookeeper.KeeperException;

import com.alibaba.hbase.replication.domain.HLogInfo;

/**
 * 多线程的 HLogReader
 * 
 * @author zalot.zhaoh
 *
 */
public class MultThreadHLogOperatorImpl extends AbstractHLogOperator implements HLogOperatorTransaction{
	protected static final Log LOG = LogFactory.getLog(MultThreadHLogOperatorImpl.class);
	protected ThreadLocal<HLogInfo> currentHLog = new ThreadLocal<HLogInfo>();
	protected ThreadLocal<HLogReader> currentReader = new ThreadLocal<HLogReader>();
	
	boolean hasSync = false;

	public MultThreadHLogOperatorImpl(Configuration conf) throws IOException,
			KeeperException, InterruptedException {
		super(conf, null);
		sync();
	}

	public static class NoFoundEntryInfoException extends Exception{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1895927503779821652L;
		public NoFoundEntryInfoException(){}
	}
	
	public HLogReader getReader() throws Exception {
//		if (!hasSync) {
//			sync();
//		}
//		
//		synchronized (canReadGroup) {
//			if (currentReader.get() == null) {
//				for (String fileName : canReadHLogReaders.keySet()) {
//					HLogReader reader = canReadHLogReaders.get(fileName);
//					if (!reader.hasOpened()) {
//						reader.open();
//						currentReader.set(reader);
//						break;
//					}else{
//						continue;
//					}
//				}
//			}
//			if(currentReader.get() == null){
//				throw new NoFoundEntryInfoException();
//			}
//		}
//		_hogs.ge
		
		return currentReader.get();
	}

	@Override
	public Entry next() {
//		HLogReader reader = null;
//		while(true){
//			try {
//				reader  = getReader();
//				if (reader != null) {
//					Entry entry = reader.next();
//					if (entry != null) {
//						return new EntryInfo(entry, reader.getType(), reader.getPath().getName(),
//								reader.getPosition());
//					}else{
//						currentReader.set(null);
//						reader.close();
//						if(reader.getType() == HLogType.OLD){
//							HLogZNode znode = getHLogZNodeByFileName(reader.getPath().getName());
//							if(znode != null){
//								LOG.debug("[HLogOperator][TYPE.END]" + Thread.currentThread().getName() + " + [FILE]" + reader.getPath().getName());
//								znode.setType(HLogType.END);
//								updateHLogZNode(znode);
//							}
//						}
//					}
//				}
//			}catch(NoFoundEntryInfoException nofound){
//				return null;
//			} catch (Exception e) {
//				LOG.error("next Error " + e.getMessage());
//				if(reader != null){
//					try {
//						reader.close();
//					} catch (IOException e1) {
//					}
//				}
//				currentReader.set(null);
//			}
//		}
		return null;
	}

	@Override
	public boolean sync() {
//		if(allHlogsSync()){
//			synchronized (canReadGroup) {
//				for(HLogReaderGroup hlogGroup : canReadGroup.values()){
//					if(!hlogGroup.isOver()){
//						return false;
//					}
//				}
//				canReadGroup.clear();
//				hasSync = true;
//				return true;
//			}
//		}
		return false;
	}
	

	@Override
	public boolean commit() {
//		List<String> tmpFiles = new ArrayList<String>();
//		for(EntryInfo info : entryInfos){
//			if(!tmpFiles.contains(info.getFileName())){
//				tmpFiles.add(info.getFileName());
//			}
//		}
//		HLogReader reader;
//		HLogZNode znode;
//		HLogType type = HLogType.LIFE;
		try{
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
			return true;
		}catch(Exception e){
			return false;
		}
	}
	
//	@Override
//	public boolean commit(Entry entry) {
//		try{
//			if(entryInfo == null || entryInfo.getEntry() == null || entryInfo.getPos() <=0 ) 
//				return false;
//			HLogZNode znode = getHLogZNodeByFileName(entryInfo.getFileName());
//			if(znode != null){
//				znode.setPos(entryInfo.getPos());
//				znode.setType(entryInfo.getType());
//				updateHLogZNode(znode);
//				return true;
//			}else{
//				LOG.error("entryinfo[" + entryInfo + "] no found in zookeeper !");
//			}
//		}catch(Exception e){
//			// TODO :: commit ERROR
//		}
//		return false;
//	}

	public boolean process(ReplicationCallBack call) {
		Entry entry = next();
		if(call.next(currentHLog.get(), entry)){
			return commit();
		}
		return false;
	}

	@Override
	public boolean process(ReplicationCallBack call, int count) {
		// TODO Auto-generated method stub
		return false;
	}
}
