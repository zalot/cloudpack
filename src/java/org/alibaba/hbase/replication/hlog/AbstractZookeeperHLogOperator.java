package org.alibaba.hbase.replication.hlog;

import java.io.IOException;

import org.alibaba.hbase.replication.domain.HLogZNode;
import org.alibaba.hbase.replication.zookeeper.HLogZNodeOperator;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.zookeeper.KeeperException;

public abstract class AbstractZookeeperHLogOperator extends AbstractHLogOperator{
	HLogZNodeOperator znodeOperator;
	
	public AbstractZookeeperHLogOperator(Configuration conf)
			throws IOException, KeeperException, InterruptedException {
		super(conf);
	}
	
	public boolean sync(){
		if(super.sync()){
			for(Path path : _hogs.life().values()){
				try {
					HLogZNode znode  = znodeOperator.get(path);
					
				} catch (Exception e){
				}
			}
		}
		return false;
	}
}
