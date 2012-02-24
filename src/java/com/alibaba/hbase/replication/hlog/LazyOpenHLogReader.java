package com.alibaba.hbase.replication.hlog;

import java.io.IOException;

import com.alibaba.hbase.replication.domain.HLogInfo;
import com.alibaba.hbase.replication.domain.HLogInfo.HLogType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.hbase.regionserver.wal.HLog;
import org.apache.hadoop.hbase.regionserver.wal.HLog.Entry;

/**
 * 延迟Open HLogReader
 * @author zalot.zhaoh
 *
 */
public class LazyOpenHLogReader implements HLogReader{
	protected static final Log LOG = LogFactory.getLog(LazyOpenHLogReader.class);

	private Configuration conf;
	private HLogInfo info;
	private FileSystem fs;
	private HLog.Reader reader;
	private long seek;
	private boolean hasOpened = false;
	private boolean isOpen = false;
	private HLogType type = HLogType.UNKNOW;
	
	public LazyOpenHLogReader(final FileSystem fs,
			HLogInfo info){
		this.fs = fs;
		this.info = info;
	}
	
	@Override
	public void close() throws IOException {
		if( reader != null && isOpen)
			reader.close();
		reader = null;
		isOpen = false;
		LOG.debug("[HLogReader][Close][TYPE." + info.getType() +  "]" + Thread.currentThread().getName() + " [File] " + info.getPath().getName());
	}
	@Override
	public int compareTo(HLogReader o) {
		if(o == null || o.getHLogInfo().getPath() == null || o.getHLogInfo().getPath().getName() == null)
			return -1;
		if(HLog.validateHLogFilename(o.getHLogInfo().getPath().getName())){
			
		}
		return 0;
	}
	
	public long getPosition() throws IOException{
		lazyOpen();
		return reader.getPosition();
	}
	
	@Override
	public HLogInfo getHLogInfo() {
		return info;
	}

	@Override
	public boolean hasOpened() {
		return hasOpened;
	}

	public boolean isOpen(){
		return isOpen;
	}

	private void lazyOpen() {
		try{
			if(reader == null && !hasOpened)
			{
				LOG.debug("[HLogReader][Open][TYPE." + info.getType() +  "]" + Thread.currentThread().getName() + " [File] " + info.getPath().getName());
				reader = HLog.getReader(fs, info.getPath() , conf);
				if(reader != null){
					isOpen = true;
					if(seek > 0){
						reader.seek(seek);
					}
				}
			}
		}catch(Exception e){
			isOpen = false;
		}finally{
			hasOpened = true;
		}
	}

	public Entry next() throws IOException{
		lazyOpen();
		if(isOpen){
			return reader.next();
		}
		return null;
	}

	@Override
	public Entry next(Entry reuse) throws IOException{
		lazyOpen();
		if(isOpen)
			return reader.next(reuse);
		return null;
	}

//	@Override
//	public boolean hasMoreNext() {
//		lazyOpen();
//		try{
//			if(isOpen()){
//				long pos = reader.getPosition();
//				Entry entry = reader.next();
//				if(entry != null){
//					if(reader.getPosition() != pos)
//						reader.seek(pos);
//					return true;
//				}else{
//					// 如果该文件已经没有内容则关闭
//					close();
//				}
//			}
//		}catch(Exception e){
//			try {
//				close();
//			} catch (IOException e1) {
//			}
//		}
//		return false;
//	}

	@Override
	public void open() throws IOException {
		lazyOpen();
	}

	public void seek(long pos) throws IOException{
		if(!isOpen){
			seek = pos;
		}
	}

	@Override
	public String toString() {
		return "LazyOpenHLogReader [reader=" + reader + ", path=" + info.getPath() + ", type=" + type + ", seek=" + seek
				+ ", isOpen=" + isOpen + ", hasOpened="
				+ hasOpened + "]";
	}
}
