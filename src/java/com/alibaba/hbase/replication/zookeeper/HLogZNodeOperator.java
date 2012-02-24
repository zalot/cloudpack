package com.alibaba.hbase.replication.zookeeper;

import com.alibaba.hbase.replication.domain.HLogZNode;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.zookeeper.KeeperException;

public interface HLogZNodeOperator {
	public void create(HLogZNode znode) throws KeeperException, InterruptedException;
	public void update(HLogZNode znode) throws KeeperException, InterruptedException;
	public void delete(HLogZNode znode) throws KeeperException, InterruptedException;
	public HLogZNode get(Path path) throws KeeperException, InterruptedException;
	public void setData(byte[] data)  throws KeeperException, InterruptedException;
	public byte[] getData()  throws KeeperException, InterruptedException;
	public void init(Configuration conf)  throws KeeperException, InterruptedException;
}
