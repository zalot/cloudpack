package com.alibaba.hbase.replication.zookeeper;

import com.alibaba.hbase.replication.domain.HLogZNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.zookeeper.RecoverableZooKeeper;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;

/**
 * 默认 HLogZNode 操作类
 * 
 * @author zalot.zhaoh
 *
 */
public class DefaultHLogZNodeOperator implements HLogZNodeOperator {
	protected static final Log LOG = LogFactory.getLog(DefaultHLogZNodeOperator.class);
	public static String REP_ZOO_BASE = "/alirep";
	protected String zkReplRoot;
	protected String zkRoot;
	protected RecoverableZooKeeper zoo;
	
	public DefaultHLogZNodeOperator(Configuration conf) throws KeeperException, InterruptedException{
		REP_ZOO_BASE = conf.get("com.alibaba.hbase.replication.zkparent", "/alirep");
		init(conf);
	}
	
	public void create(HLogZNode znode) throws KeeperException, InterruptedException{
//		zoo.create(getZkPathByFileName(znode.getPath().getName()), znode.getData(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		LOG.info("[ZNode Create] " + znode);
	}

	public void update(HLogZNode znode) throws InterruptedException, KeeperException{
//		zoo.delete(getZkPathByFileName(znode.getPath().getName()), znode.getVersion());
		LOG.info("[ZNode Delete] " + znode);
	}
	
	public void delete(HLogZNode znode) throws KeeperException, InterruptedException{
//		zoo.setData(getZkPathByFileName(znode.getPath().getName()), znode.getData(), znode.getVersion());
		LOG.info("[ZNode Update] " + znode);
	}

	@Override
	public HLogZNode get(Path path) throws KeeperException,
			InterruptedException {
		try{
//			String pathStr = getZkPathByFileName(path.getName());
//			Stat stat = zoo.exists(path, false);
//			if(stat != null){
//				HLogZNode znode = new HLogZNode(fileName, zoo.getData(path, false, null));
//				znode.setVersion(stat.getVersion());
//				return znode;
//			}
		}
		catch(Exception e){
			//TODO : No found HLogZNode by File Name
			LOG.error(e.getMessage());
		}
		return null;
	}


	@Override
	public void init(Configuration conf) throws KeeperException, InterruptedException {
		zkRoot = conf.get("zookeeper.znode.parent");
		zkReplRoot = zkRoot + "/" + REP_ZOO_BASE;
		Stat statZkRoot = zoo.exists(zkRoot, false);
		if(statZkRoot == null){
			zoo.create(zkRoot, null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		}
		Stat statZkRepl = zoo.exists(zkReplRoot, false);
		if(statZkRepl == null)
			zoo.create(zkReplRoot, null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
	}

	@Override
	public void setData(byte[] data) throws KeeperException,
			InterruptedException {
		zoo.setData(zkReplRoot, data, 0);
	}

	@Override
	public byte[] getData() throws KeeperException, InterruptedException {
		Stat stat = zoo.exists(zkReplRoot, false);
		if(stat != null){
			return zoo.getData(zkReplRoot, false, stat);
		}
		return null;
	}
}
