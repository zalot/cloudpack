package com.alibaba.hbase.replication.producer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.alibaba.hbase.replication.hlog.HLogOperator;
import com.alibaba.hbase.replication.hlog.MultHLogOperatorImpl;
import com.alibaba.hbase.replication.hlog.HLogOperator.EntryInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.zookeeper.KeeperException;

/**
 * 同步管理
 * @author zalot.zhao
 *
 */
public class HBaseReplicationManager {
	protected static final Log LOG = LogFactory.getLog(HBaseReplicationManager.class);
	HBaseReplication replication;
	HLogOperator hlogOperator;
	Configuration conf;
	HBaseReplicationListener listener;
	public void setListener(HBaseReplicationListener listener){
		this.listener = listener;
	}
	int threadType = 1;
	int threadSize = 5;
	boolean isStop = false;
	protected ThreadPoolExecutor threadPool;
	public HBaseReplicationManager(Configuration conf) throws IOException, KeeperException, InterruptedException{
		replication = new HBaseReplicationClient(conf);
		hlogOperator = new MultHLogOperatorImpl(conf);
		this.conf = conf;
		init();
	}
	
	public void init(){
		threadSize = conf.getInt("alibaba.replication.thread.size", 5);
		threadType = conf.getInt("alibaba.replication.thread.type", 1);
		threadPool  = new ThreadPoolExecutor(5, 20, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
	}
	
	public void start(){
		for(int x=0; x<threadSize; x++){
			threadPool.execute(getThreadByType());
		}
	}
	
	public boolean stop(){
		this.isStop = true;
		return threadPool.isShutdown();
	}
	
	private Runnable getThreadByType() {
		if(threadType == 1){
			return getThread1();
		}else{
			return getThread2();
		}
	}

	protected Runnable getThread1(){
		Runnable run = new Runnable() {
			@Override
			public void run() {
				EntryInfo entryInfo = null;
				while(!isStop){
					try {
						entryInfo = hlogOperator.next();
						if(entryInfo != null)
						{
							if(replication.put(entryInfo)){
								if(listener != null)
									listener.preCommit(entryInfo);
								hlogOperator.commit(entryInfo);
							}
						}else{
							Thread.sleep(10000);
							if(!isStop)
								hlogOperator.sync();
							else
								return;
						}
					} catch (Exception e) {
						LOG.error(e);
					}
				}
			}
		};
		return run;
	}
	
	protected Runnable getThread2(){
		Runnable run = new Runnable() {
			@Override
			public void run() {
				List<EntryInfo> entryInfos = null;
				EntryInfo entryInfo = null;
				while(!isStop){
					try {
						entryInfos = new ArrayList<EntryInfo>();
						while((entryInfo = hlogOperator.next()) != null){
							entryInfos.add(entryInfo);
						}
						if(replication.puts(entryInfos)){
							hlogOperator.commit(entryInfo);
							LOG.info("[ReplicationManager][Commits]" + entryInfos);
						}
					} catch (Exception e) {
						LOG.error(e);
					}
				}
			}
		};
		return run;
	}
	
	public static void main(String[] args) throws IOException, KeeperException, InterruptedException{
		Configuration conf = HBaseConfiguration.create();
		HBaseReplicationManager manager = new HBaseReplicationManager(conf);
		manager.start();
	}
}
