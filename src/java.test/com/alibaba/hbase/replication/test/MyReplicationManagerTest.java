package com.alibaba.hbase.replication.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import junit.framework.Assert;

import com.alibaba.hbase.replication.hlog.HLogOperator.EntryInfo;
import com.alibaba.hbase.replication.producer.HBaseReplicationListener;
import com.alibaba.hbase.replication.producer.HBaseReplicationManager;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.HTablePool;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.regionserver.wal.HLog.Entry;
import org.apache.zookeeper.KeeperException;
import org.junit.BeforeClass;
import org.junit.Test;

public class MyReplicationManagerTest extends MyBaseTest{
	protected static final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(50, 100, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
	protected static final int zkClusterCount = 2;
	protected static final int hbaseClusterCount = 2;
	
	protected static HTablePool pool1;
	protected static HTablePool pool2;
	
	protected static HBaseTestingUtility util1;
	protected static HBaseTestingUtility util2;
	
	protected static Configuration conf1;
	protected static Configuration conf2;
	
	protected static final String TABLEA = "testTableA";
	protected static final String TABLEB = "testTableB";
	protected static final String TABLEC = "testTableC";
	protected static final String COLA = "colA";
	protected static final String COLB = "colB";
	protected static HBaseReplicationManager rep1to2;
	protected static HBaseReplicationManager rep2to1;
	
	public static void createTable(Configuration conf) throws Exception{
		HBaseAdmin admin = new HBaseAdmin(conf);
		HTableDescriptor htable = new HTableDescriptor(TABLEA);
		HColumnDescriptor hcolumnA = new HColumnDescriptor(COLA);
		HColumnDescriptor hcolumnB = new HColumnDescriptor(COLB);
		htable.addFamily(hcolumnA);
		htable.addFamily(hcolumnB);
		admin.createTable(htable);

		htable = new HTableDescriptor(TABLEB);
		htable.addFamily(hcolumnA);
		htable.addFamily(hcolumnB);
		admin.createTable(htable);
		
		htable = new HTableDescriptor(TABLEC);
		htable.addFamily(hcolumnA);
		htable.addFamily(hcolumnB);
		admin.createTable(htable);
		admin.close();
	}
	
	@BeforeClass
	public static void init() throws Exception {
		conf1 = HBaseConfiguration.create();
		conf1.setBoolean("hbase.regionserver.info.port.auto",true);
		conf1.setLong("hbase.regionserver.hlog.blocksize", 1024);
		conf1.setInt("hbase.regionserver.maxlogs", 2);
		conf1.set(HConstants.ZOOKEEPER_ZNODE_PARENT, "/1");
		conf1.setBoolean("dfs.support.append", true);
		conf1.setInt("hbase.master.info.port", 60010);
		
		util1 = new HBaseTestingUtility(conf1);
		util1.startMiniZKCluster(zkClusterCount);
		util1.startMiniCluster(1, hbaseClusterCount);
		createTable(conf1);
		pool1 = new HTablePool(conf1, 10);
		
		conf2 = HBaseConfiguration.create(conf1);
	    conf2.set(HConstants.ZOOKEEPER_ZNODE_PARENT, "/2");
	    conf2.setBoolean("dfs.support.append", true);
	    conf2.setBoolean("hbase.regionserver.info.port.auto",true);
	    conf2.setLong("hbase.regionserver.hlog.blocksize", 1024);
	    conf2.setInt("hbase.master.info.port", 60011);
	    
	    util2 = new HBaseTestingUtility(conf2);
		util2.startMiniZKCluster(zkClusterCount);
		util2.startMiniCluster(1, hbaseClusterCount);
		pool2 = new HTablePool(conf2, 10);
		
		Configuration conf1to2 = HBaseConfiguration.create(conf2);
		conf1to2.set("hbase.rootdir", conf1.get("hbase.rootdir")); // 读取 1
		conf1to2.set("fs.default.name", conf1.get("fs.default.name")); // 读取 1
		conf1to2.set("hbase.zookeeper.quorum", conf2.get("hbase.zookeeper.quorum"));
		conf1to2.set("zookeeper.znode.parent", conf2.get("zookeeper.znode.parent"));
		conf1to2.set("hbase.master.port", conf2.get("hbase.master.port"));
		rep1to2 = new HBaseReplicationManager(conf1to2);
		rep1to2.start();
		rep1to2.setListener(new HBaseReplicationListener() {
			@Override
			public void preCommit(EntryInfo info) {
				Entry entry = info.getEntry();
				System.out.println("1to2 " + entry.getKey().getClusterId());
			}
		});
		
		Configuration conf2to1 = HBaseConfiguration.create(conf1);
		conf2to1.set("hbase.rootdir", conf2.get("hbase.rootdir")); // 读取 2
		conf2to1.set("fs.default.name", conf2.get("fs.default.name")); // 读取 2
		conf2to1.set("hbase.zookeeper.quorum", conf1.get("hbase.zookeeper.quorum"));
		conf2to1.set("zookeeper.znode.parent", conf1.get("zookeeper.znode.parent"));
		conf2to1.set("hbase.master.port", conf1.get("hbase.master.port"));
		rep2to1 = new HBaseReplicationManager(conf2to1);
		rep2to1.start();
		rep2to1.setListener(new HBaseReplicationListener() {
			@Override
			public void preCommit(EntryInfo info) {
				Entry entry = info.getEntry();
				System.out.println("2to1 " + entry.getKey().getClusterId());
			}
		});
	}
	
	
	public void insertData(HTablePool pool, String table, String family, String qualifier, int size) throws IOException{
		HTableInterface htable = pool.getTable(table);
		List<Put> puts = new ArrayList<Put>();
		for(int x=0; x<size; x++){
            Put put = new Put(getRndString(table).getBytes());
            put.add(family.getBytes(), qualifier.getBytes(), getRndString(null).getBytes());
            puts.add(put);
        }
		htable.put(puts);
		System.out.println("Table[" + table + "] PutAllCount[" + count(htable.getScanner(COLA.getBytes())) + "]");
	}
	
	public int count(ResultScanner scan){
		try{
			int count = 0;
			Result rs;
			while((rs = scan.next()) != null){
				count ++;
			}
			return count;
		}catch(Exception e){
			return 0;
		}
	}
	
	public Runnable getMultWriter(){
		Runnable run = new Runnable() {
			@Override
			public void run() {
				while(true){
					try {
						insertData(pool1, getRndTableName(), COLA, "name", getRndSize());
						Thread.sleep(10000);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
		};
		return run;
	}
	
	public String getRndTableName(){
		int in = rnd.nextInt(3);
		if(in == 1){
			return TABLEA;
		}else if(in == 2){
			return TABLEB;
		}else{
			return TABLEC;
		}
	}
	
	public int getRndSize(){
		return rnd.nextInt(200);
	}
	
	@Test
	public void testTableReplication() throws IOException{
		HBaseAdmin admin = new HBaseAdmin(conf1);
		HColumnDescriptor hcolumn = new HColumnDescriptor("colC");
		admin.disableTable(TABLEA);
		admin.addColumn(TABLEA, hcolumn);
		admin.enableTable(TABLEA);
		System.out.println(pool2.getTable(TABLEA));
	}
	
	@Test
	public void testDataReplication() throws IOException, KeeperException, InterruptedException{
		long waitTime = 20000;
		int ct = 0;
		String tableName = getRndTableName();
		int size = getRndSize();
		// ===================================================================
		// 1to2 Put Replication Test
		String rowA = "testA";
		Put put = new Put(rowA.getBytes());
		Get get = new Get(rowA.getBytes());
		put.add(COLA.getBytes(), "name".getBytes(), "zalot".getBytes());
		pool1.getTable(tableName).put(put);
		Result rs = pool1.getTable(tableName).get(get);
		Assert.assertTrue(!rs.isEmpty());
		Thread.sleep(waitTime);
		get = new Get(rowA.getBytes());
		rs = pool2.getTable(tableName).get(get);
		Assert.assertTrue(!rs.isEmpty());
		Assert.assertTrue(rs.containsColumn(COLA.getBytes(), "name".getBytes()));
		System.out.println("[1]==================================================");
		// ===================================================================
		// 1to2 Delete Replication Test
		Delete delete = new Delete(rowA.getBytes());
		pool1.getTable(tableName).delete(delete);
		rs = pool1.getTable(tableName).get(get);
		Assert.assertTrue(rs.isEmpty());
		Thread.sleep(waitTime);
		get = new Get(rowA.getBytes());
		rs = pool2.getTable(tableName).get(get);
		Assert.assertTrue(rs.isEmpty());
		System.out.println("[2]==================================================");
		// ===================================================================
		// 
		insertData(pool1, tableName, COLA, "name", size);
		insertData(pool2, tableName, COLA, "name", size);
		insertData(pool1, tableName, COLA, "name", size);
		Thread.sleep(waitTime);
		int count = count(pool1.getTable(tableName).getScanner(COLA.getBytes()));
		Assert.assertEquals(size * 3, count);
		count = count(pool2.getTable(tableName).getScanner(COLA.getBytes()));
		Assert.assertEquals(size * 3, count);
		System.out.println("[3]==================================================");
		// ===================================================================
		String rowB = "testB";
		get = new Get(rowB.getBytes());
		put = new Put(rowB.getBytes());
		put.add(COLA.getBytes(), "name".getBytes(), "zalot".getBytes());
		pool1.getTable(tableName).put(put);
		
		put = new Put(rowB.getBytes());
		put.add(COLA.getBytes(), "sex".getBytes(), "zalot".getBytes());
		pool2.getTable(tableName).put(put);
		rs = pool2.getTable(tableName).get(get);
		Assert.assertTrue(rs.containsColumn(COLA.getBytes(), "sex".getBytes()));
		Thread.sleep(waitTime);
		rs = pool2.getTable(tableName).get(get);
		Assert.assertTrue(rs.containsColumn(COLA.getBytes(), "name".getBytes()));
		Assert.assertTrue(rs.containsColumn(COLA.getBytes(), "sex".getBytes()));
		System.out.println("[4]==================================================");
		// ===================================================================
		// 1to2 and 2to1
		String rowC = "testC";
		get = new Get(rowC.getBytes());
		put = new Put(rowC.getBytes());
		put.add(COLA.getBytes(), "name".getBytes(), "zalot".getBytes());
		pool1.getTable(tableName).put(put);
		rs = pool1.getTable(tableName).get(get);
		Assert.assertTrue(!rs.isEmpty());
		Thread.sleep(waitTime);
		rs = pool2.getTable(tableName).get(get);
		Assert.assertTrue(!rs.isEmpty());
		pool2.getTable(tableName).delete(new Delete(rowC.getBytes()));
		Thread.sleep(waitTime);
		rs = pool1.getTable(tableName).get(get);
		Assert.assertTrue(rs.isEmpty());
		System.out.println("[5]==================================================");
	}
}
