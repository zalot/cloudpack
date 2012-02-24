package org.alibaba.hbase.replication.test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.alibaba.hbase.replication.zookeeper.ReplicationZookeeperWatch;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.MiniHBaseCluster;
import org.apache.hadoop.hbase.zookeeper.RecoverableZooKeeper;
import org.apache.hadoop.hbase.zookeeper.ZKUtil;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.zookeeper.KeeperException;

public class MyBaseTest {
	protected final static HBaseTestingUtility test = new HBaseTestingUtility();
	
	private final static String ROOT_PATH = "/tmp/tmphbaserootdir/";
	protected static MiniDFSCluster dfsCluster;
	protected static MiniHBaseCluster hbaseCluster;
	protected static RecoverableZooKeeper zoo;
	protected static Random rnd = new Random();
	
	public static RecoverableZooKeeper getZoo() throws Exception{
		if(test.getZkCluster().getZooKeeperServerNum() <=0 ) test.startMiniZKCluster(3);
		if(zoo == null)
		{
			zoo = ZKUtil.connect(test.getConfiguration() ,new ReplicationZookeeperWatch());
			return zoo;
		}
		return zoo;
	}
		
	public static String getRndString(String base) {
		String uuid = UUID.randomUUID().toString().substring(0, 10);
		return base == null || base.length() <= 0 ? uuid : base + uuid;
	}

	protected void printZk(RecoverableZooKeeper zoo, String path)
			throws KeeperException, InterruptedException {
		System.out.println("[ZK] " + path + " [DATA] "  + Arrays.toString(zoo.getData(path, false, null)));
		List<String> childs = zoo.getChildren(path, false);
		if (childs != null) {
			for (String cd : childs)
				printZk(zoo, path + "/" + cd);
		}
	}

	protected void printDfs(FileSystem fs, Path path) {
		try {
			System.out.println((fs.isFile(path) ? "[F]" : "[D]") + path);
			if (!fs.isFile(path)) {
				for (FileStatus fst : fs.listStatus(path)) {
					printDfs(fs, fst.getPath());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void printHBaseZK() throws Exception{
		printZk(getZoo(), test.getConfiguration().get("zookeeper.znode.parent"));
	}
	
	public void printHBaseDFS(String path) throws IOException{
		printDfs(test.getDFSCluster().getFileSystem(), new Path(test.getConfiguration().get("hbase.rootdir") + (path == null ? "" : "/" + path)));
	}
}
