package com.alibaba.hbase.replication.zookeeper;

import org.apache.hadoop.conf.Configuration;
import org.apache.zookeeper.KeeperException;

import com.alibaba.hbase.replication.domain.HLogGroupZNode;

/**
 * HLogZnode 操作
 * 
 * @author zalot.zhaoh
 *
 */
public interface HLogGroupZNodeOperator{
	public void create(HLogGroupZNode znode) throws KeeperException, InterruptedException;
	public void delete(HLogGroupZNode znode) throws KeeperException, InterruptedException;
	public HLogGroupZNode get(String name) throws KeeperException, InterruptedException;
	public boolean lockGroup(HLogGroupZNode znode) throws KeeperException, InterruptedException;
	public boolean unlockGroup(HLogGroupZNode znode) throws KeeperException, InterruptedException;
	public void init(Configuration conf)  throws KeeperException, InterruptedException;
}
