package com.alibaba.hbase.replication.hlog;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.zookeeper.KeeperException;

import com.alibaba.hbase.replication.domain.DefaultHLogZNode;
import com.alibaba.hbase.replication.domain.HLogInfo;
import com.alibaba.hbase.replication.domain.HLogZNode;
import com.alibaba.hbase.replication.zookeeper.HLogZNodeOperator;

/**
 * 基础的 HLogOperator & Zookeeper 合并操作
 * 
 * @author zalot.zhaoh
 *
 */
public abstract class AbstractZookeeperHLogOperator extends AbstractHLogOperator{
	HLogZNodeOperator znodeOperator;
	
	public AbstractZookeeperHLogOperator(Configuration conf)
			throws IOException, KeeperException, InterruptedException {
		super(conf);
	}
	
	public boolean sync(){
		return super.sync();
	}
	
	public HLogReader getReader(HLogInfo info){
		HLogReader reader = super.getReader(info);
		if(reader != null){
			try {
				HLogZNode znode = znodeOperator.get(info.getPath());
				if(znode != null){
					if(znode.getPos() > 0){
						reader.seek(znode.getPos());
					}
				}else{
					znode = new DefaultHLogZNode(reader.getHLogInfo().getPath(), reader.getHLogInfo().getType(), 0);
					znodeOperator.create(znode);
				}
				return reader;
			} catch (Exception e) {
				return null;
			}
		}
		return null;
	}
	
//	public boolean isLock(Path path){
//		
//	}
//	
//	public void lock(Path path){
//		
//	}
//	
//	public void unlock(Path path){
//		
//	}
}
