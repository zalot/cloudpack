package com.alibaba.hbase.replication.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import junit.framework.Assert;

import com.alibaba.hbase.replication.hlog.DefaultHLogOperatorImpl;
import com.alibaba.hbase.replication.hlog.HLogOperator.EntryInfo;
import com.alibaba.hbase.replication.hlog.HLogReader;
import com.alibaba.hbase.replication.hlog.MultHLogOperatorImpl;
import com.alibaba.hbase.replication.producer.HBaseReplicationClient;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.HTablePool;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.regionserver.wal.HLog;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 暂时未完善
 * 
 * @author zalot.zhaoh
 *
 */
public class MyReplactionTest extends MyBaseTest {
	protected static final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(50, 100, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
	protected static final int zkClusterCount = 3;
	protected static final int hbaseClusterCount = 2;
	
	protected static final String TABLEA = "testTableA";
	protected static final String TABLEB = "testTableB";
	protected static final String TABLEC = "testTableC";
	protected static final String COLA = "colA";
	protected static final String COLB = "colB";
	protected static HTablePool pool;
	protected static DefaultHLogOperatorImpl defaultRep;
	protected static MultHLogOperatorImpl muRep;
	
	public static void createTable() throws Exception{
		HBaseAdmin admin = new HBaseAdmin(test.getConfiguration());
		
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
		test.startMiniZKCluster(zkClusterCount);
		assert test.getZkCluster().getZooKeeperServerNum() == zkClusterCount;
		test.getConfiguration().setBoolean("hbase.regionserver.info.port.auto",true);
		test.getConfiguration().setLong("hbase.regionserver.hlog.blocksize", 1024);
		test.getConfiguration().setInt("hbase.regionserver.maxlogs", 2);
		hbaseCluster = test.startMiniCluster(1, hbaseClusterCount);
		assert hbaseCluster.getRegionServerThreads().size() == hbaseClusterCount;
		createTable();
		defaultRep = new DefaultHLogOperatorImpl(test.getConfiguration());
		muRep = new MultHLogOperatorImpl(test.getConfiguration());
		pool = new HTablePool(test.getConfiguration(), 20);
	}
	
	public void insertData(String table, int size) throws IOException{
		HTableInterface htable = pool.getTable(table);
		List<Put> puts = new ArrayList<Put>();
		for(int x=0; x<=size; x++){
            Put put = new Put(getRndString(TABLEA).getBytes());
            put.add(COLA.getBytes(), "name".getBytes(), getRndString(null)
                    .getBytes());
            put.add(COLA.getBytes(), "sex".getBytes(), getRndString(null)
                    .getBytes());
            put.add(COLB.getBytes(), "name".getBytes(), getRndString(null)
                    .getBytes());
            put.add(COLB.getBytes(), "sex".getBytes(), getRndString(null)
                    .getBytes());
            puts.add(put);
        }
		htable.put(puts);
		System.out.println("Table[" + table + "] PutAllCount[" + count(htable.getScanner(COLA.getBytes())) + "]");
	}
	
	public void deleteData(String table, int size) throws IOException{
		HTableInterface htable = pool.getTable(table);
		ResultScanner scan = htable.getScanner(COLA.getBytes());
		int  delCount = 0;
		Result rs;
		List<Delete> deletes = new ArrayList<Delete>();
		while((rs = scan.next()) != null && delCount <= size){
			deletes.add(new Delete(rs.getRow()));
			delCount++;
		}
		htable.delete(deletes);
		System.out.println("Table[" + table + "] DeleteCount[" + count(htable.getScanner(COLA.getBytes())) + "]");
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
	
	public void printAllHLogEntry(DefaultHLogOperatorImpl operator) throws Exception{
		int kvCount = 0;
		for(Path hlog : operator.getLifeHLogs()){
			HLogReader reader = operator.getReader(hlog);
			System.out.println("[" + reader.getPosition() + "]" + hlog );
			HLog.Entry entry = null;
			while((entry = reader.next()) != null){
				System.out.println("--------------------------------------");
				List<KeyValue> kvs = entry.getEdit().getKeyValues();
				System.out.println("[entry-key]" + entry.getKey() + " [entry-kvsize]" + kvs.size() + "");
				for(KeyValue kv : kvs){
					if(kvCount++ < 50){
						System.out.println("[ " + KeyValue.Type.codeToType(kv.getType()) +  "]" + kv);
					}
				}
				kvCount = 0;
			}
			System.out.println("[" + reader.getPosition() +"]--------------------------------------");
		}
		System.out.println("over");
	}
	
	public void testDefaultOperator() throws Exception{
//		EntryInfo info = null;
//		int entryCount = 0;
//		while((info = defaultRep.next()) != null){
//			if(HBaseReplicationClient.isCusTable(info.getEntry().getKey().getTablename())){
//				entryCount ++;
//			}
//			Assert.assertTrue(defaultRep.getMemHLogReader(info.getFileName()).getPosition() == info.getPos());
//			Assert.assertTrue(defaultRep.getHLogZNodeByFileName(info.getFileName()).getPos() != info.getPos());
//			defaultRep.commit(info);
//			Assert.assertTrue(defaultRep.getHLogZNodeByFileName(info.getFileName()).getPos() == info.getPos());
//			Assert.assertTrue(defaultRep.getMemHLogReader(info.getFileName()).getPosition() == info.getPos());
//		}
//		Assert.assertEquals(entryCount, 0); // no data
//		
//		defaultRep.sync();
//		while((info = defaultRep.next()) != null){
//			Assert.assertTrue(false); // no data
//		}
//		
//		insertData(TABLEA,20);
//		Thread.sleep(defaultRep.getSleepTime());
//		defaultRep.sync();
//		List<HLogOperator.EntryInfo> entryInfos = new ArrayList<HLogOperator.EntryInfo>();
//		
//		while((info = defaultRep.next()) != null){
//			if(HBaseReplicationClient.isCusTable(info.getEntry().getKey().getTablename())){
//				entryCount ++;
//				entryInfos.add(info);
//			}
//			System.out.println(info.getFileName());
//		}
//		Assert.assertTrue(defaultRep.getMemHLogReader(entryInfos.get(0).getFileName()).getPosition() == entryInfos.get(0).getPos());
//		Assert.assertTrue(defaultRep.getHLogZNodeByFileName(entryInfos.get(0).getFileName()).getPos() != entryInfos.get(0).getPos());
//		defaultRep.commit(entryInfos);
//		Assert.assertTrue(defaultRep.getHLogZNodeByFileName(entryInfos.get(0).getFileName()).getPos() == entryInfos.get(0).getPos());
//		
//		defaultRep.sync();
//		while((info = defaultRep.next()) != null){
//			Assert.assertTrue(false); // no data
//		}
//		
//		insertData(TABLEA,20);
//		insertData(TABLEB,20);
//		insertData(TABLEC,20);
//		Thread.sleep(defaultRep.getSleepTime());
//		defaultRep.sync();
//		entryCount = 0;
//		entryInfos.clear();
//		while((info = defaultRep.next()) != null){
//			if(HBaseReplicationClient.isCusTable(info.getEntry().getKey().getTablename())){
//				entryCount ++;
//				entryInfos.add(info);
//				Assert.assertTrue(defaultRep.getMemHLogReader(info.getFileName()).getPosition() == info.getPos());
//				Assert.assertTrue(defaultRep.getHLogZNodeByFileName(info.getFileName()).getPos() != info.getPos());
//			}
//		}
//		defaultRep.commit(entryInfos);
//		for(EntryInfo e : entryInfos){
//			Assert.assertTrue(defaultRep.getHLogZNodeByFileName(e.getFileName()).getPos() == defaultRep.getMemHLogReader(e.getFileName()).getPosition());
//		}
//		
//		insertData(TABLEA,3000);
//		Thread.sleep(defaultRep.getSleepTime());
//		defaultRep.sync();
//		entryCount = 0;
//		entryInfos.clear();
//		while((info = defaultRep.next()) != null){
//			if(HBaseReplicationClient.isCusTable(info.getEntry().getKey().getTablename())){
//				entryCount ++;
//				entryInfos.add(info);
//				Assert.assertTrue(defaultRep.getMemHLogReader(info.getFileName()).getPosition() == info.getPos());
//				Assert.assertTrue(defaultRep.getHLogZNodeByFileName(info.getFileName()).getPos() != info.getPos());
//			}
//			System.out.println(info.getFileName());
//		}
//		defaultRep.commit(entryInfos);
//		for(EntryInfo e : entryInfos){
//			Assert.assertTrue(defaultRep.getHLogZNodeByFileName(e.getFileName()).getPos() == defaultRep.getMemHLogReader(e.getFileName()).getPosition());
//		}
	}
	
	public void testMultOperator() throws Exception{
		EntryInfo ent ;
//		while((ent = muRep.next()) != null){
//			System.out.println(ent.getFileName() + "  " + ent.getPos()  + " | "  + Bytes.toString(ent.getEntry().getKey().getTablename()));
//		}
		while((ent = muRep.next()) != null){
			if(HBaseReplicationClient.isCusTable(ent.getEntry().getKey().getTablename())){
				Assert.assertFalse(true); // no data
			}
			muRep.commit(ent); // 标记读完
		}
		insertData(TABLEA, 10);
		while((ent = muRep.next()) != null){
			Assert.assertFalse(true); // no sync & no data
		}
		
		Thread.sleep(muRep.getSleepTime());
		muRep.sync();
		int cusTable = 0;
		while((ent = muRep.next()) != null){
			if(HBaseReplicationClient.isCusTable(ent.getEntry().getKey().getTablename())){
				cusTable++;
			}
			muRep.commit(ent);
		}
		if(cusTable < 0){
			Assert.assertFalse(true); // have data
		}
	}
	
	@Test
	public void testMultThread(){
		threadPool.execute(getMultReader());
	}

	public Runnable getMultReader(){
		Runnable run = new Runnable() {
			@Override
			public void run() {
				while(true){
					try {
						EntryInfo ent ;
						while((ent = muRep.next()) != null){
							if(HBaseReplicationClient.isCusTable(ent.getEntry().getKey().getTablename())){
							}
							System.out.println(Thread.currentThread().getName() + " -- " + ent.getFileName());
							muRep.commit(ent);
						}
						Thread.sleep(10000);
						muRep.sync();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		return run;
	}
	
	public Runnable getMultWriter(){
		Runnable run = new Runnable() {
			@Override
			public void run() {
				while(true){
					try {
						insertData(getRndTableName(), getRndSize());
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
		int in = rnd.nextInt(4);
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

	@AfterClass
	public static void des() throws Exception {
		test.shutdownMiniCluster();
		test.shutdownMiniZKCluster();
	}
}
