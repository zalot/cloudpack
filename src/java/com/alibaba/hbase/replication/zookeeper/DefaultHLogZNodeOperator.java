package com.alibaba.hbase.replication.zookeeper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.zookeeper.RecoverableZooKeeper;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;

import com.alibaba.hbase.replication.domain.DomainFactory;
import com.alibaba.hbase.replication.domain.HLogZNode;

/**
 * 默认 HLogZNode 操作类
 * 
 * @author zalot.zhaoh
 * 
 */
public class DefaultHLogZNodeOperator implements HLogZNodeOperator {
	protected static final Log LOG = LogFactory
			.getLog(DefaultHLogZNodeOperator.class);
	public static String REP_ZOO_BASE = "/alirep";
	protected String zkReplRoot;
	protected String zkRoot;
	protected RecoverableZooKeeper zoo;

	public DefaultHLogZNodeOperator(Configuration conf) throws KeeperException,
			InterruptedException {
		REP_ZOO_BASE = conf.get("com.alibaba.hbase.replication.zkparent",
				"/alirep");
		init(conf);
	}

	public void create(HLogZNode znode) throws KeeperException,
			InterruptedException {
		String path = path(znode.getPath());
		zoo.create(path, znode.getData(), Ids.OPEN_ACL_UNSAFE,
				CreateMode.PERSISTENT);
		LOG.info("[ZNode Create] " + znode);
	}

	public void update(HLogZNode znode) throws InterruptedException,
			KeeperException {
		zoo.delete(path(znode.getPath()), znode.getVersion());
		LOG.info("[ZNode Delete] " + znode);
	}

	public void delete(HLogZNode znode) throws KeeperException,
			InterruptedException {
		String path = path(znode.getPath());
		zoo.sync(path, null, null);
		zoo.setData(path, znode.getData(), znode.getVersion());
		LOG.info("[ZNode Update] " + znode);
	}

	@Override
	public HLogZNode get(Path path) throws KeeperException,
			InterruptedException {
		String strPath = path(path);
		Stat stat = zoo.exists(strPath, false);
		if (stat != null) {
			byte[] data = zoo.getData(strPath, false, stat);
			HLogZNode znode = DomainFactory.createHLogZNode(path, data,
					stat.getVersion());
			return znode;
		}
		return null;
	}

	protected String path(Path path) {
		return zkReplRoot + "/" + path.getName();
	}

	@Override
	public void init(Configuration conf) throws KeeperException,
			InterruptedException {
		zkRoot = conf.get("zookeeper.znode.parent");
		zkReplRoot = zkRoot + "/" + REP_ZOO_BASE;
		Stat statZkRoot = zoo.exists(zkRoot, false);
		if (statZkRoot == null) {
			zoo.create(zkRoot, null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		}
		Stat statZkRepl = zoo.exists(zkReplRoot, false);
		if (statZkRepl == null)
			zoo.create(zkReplRoot, null, Ids.OPEN_ACL_UNSAFE,
					CreateMode.PERSISTENT);
	}

	@Override
	public void setData(byte[] data) throws KeeperException,
			InterruptedException {
		zoo.setData(zkReplRoot, data, 0);
	}

	@Override
	public byte[] getData() throws KeeperException, InterruptedException {
		Stat stat = zoo.exists(zkReplRoot, false);
		if (stat != null) {
			return zoo.getData(zkReplRoot, false, stat);
		}
		return null;
	}
}
