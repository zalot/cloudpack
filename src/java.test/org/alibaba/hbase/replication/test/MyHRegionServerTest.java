package org.alibaba.hbase.replication.test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.HRegionInfo;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.LocalHBaseCluster;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.regionserver.HRegionServer;
import org.apache.hadoop.hbase.util.JVMClusterUtil;
import org.apache.hadoop.hbase.util.JVMClusterUtil.RegionServerThread;
import org.junit.BeforeClass;
import org.junit.Test;

public class MyHRegionServerTest {
	private final static HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();
	private final static String ROOT_PATH = "/tmp/tmphbaserootdir/";
	private static LocalHBaseCluster hbaseCluster;
	private static Configuration conf = null;
	private static String TABLE_STR = "testTable";
	private static String[] TABLE_STRS = new String[]{
		TABLE_STR + "A",
		TABLE_STR + "B",
		TABLE_STR + "C",
		TABLE_STR + "D"
	};
	
	private static byte[] COL = "column".getBytes();

	public static String getRndString(String base) {
		String uuid = UUID.randomUUID().toString().substring(0, 10);
		return base == null || base.length() <= 0 ? uuid : base + uuid;
	}

	public static Configuration getConf() throws Exception {
		if (conf != null)
			return conf;
		if (TEST_UTIL.getZkCluster() == null)
			TEST_UTIL.startMiniZKCluster();
		Configuration conf = TEST_UTIL.getConfiguration();
		conf.set(HConstants.MASTER_PORT, "0");
		String path = "file:///" + ROOT_PATH + getRndString(null) + "/";
		conf.set(HConstants.HBASE_DIR, path);
		conf.setBoolean("hbase.regionserver.info.port.auto", true);
		MyHRegionServerTest.conf = conf;
		System.out.println("HMaster Dir[" + path + "]");
		return conf;
	}

	@BeforeClass
	public static void init() throws Exception {
		hbaseCluster = new LocalHBaseCluster(getConf(), 0, 0);
		hbaseCluster.addMaster(getConf(), 0);
		hbaseCluster.addRegionServer();
		hbaseCluster.addRegionServer();
		hbaseCluster.addRegionServer();
		hbaseCluster.addRegionServer();
		hbaseCluster.addRegionServer();
		hbaseCluster.startup();
	}

	@Test
	public void testCreateTable() throws Exception {
		HBaseAdmin admin = new HBaseAdmin(getConf());
		for(String tableStr : TABLE_STRS){
			HTableDescriptor htable = new HTableDescriptor(tableStr);
			HColumnDescriptor hcolumn = new HColumnDescriptor(COL);
			htable.addFamily(hcolumn);
			admin.createTable(htable);
		}
		admin.close();
	}

	@Test
	public void testPut() throws Exception {
		for(String tableStr : TABLE_STRS){
			HTable table = new HTable(getConf(), tableStr);
			List<Put> puts = new ArrayList<Put>();
			for(int x=0; x<10000; x++){
				Put put = new Put(getRndString(TABLE_STR).getBytes());
				put.add(COL, "name".getBytes(), getRndString(null)
						.getBytes());
				puts.add(put);
			}
			table.put(puts);
			table.close();
		}
	}
	class Cpx{
		public JVMClusterUtil.RegionServerThread thread;
		public long hlogSizeCount;
		public Cpx(RegionServerThread thread, long hlogSizeCount) {
			super();
			this.thread = thread;
			this.hlogSizeCount = hlogSizeCount;
		}
	}
	private Cpx printJvmInfoAndGetHRegionByTable(String table) throws IOException{
		JVMClusterUtil.RegionServerThread tableRegionThread = null;
		long allCount = 0;
		String oldHlogPath = null;
		for (JVMClusterUtil.RegionServerThread hregionServer : hbaseCluster
				.getLiveRegionServers()) {
			long count = 0;
			HRegionServer regionServer = hregionServer.getRegionServer();
			System.out.println("RegionServer -- " + regionServer.getRpcServer().getListenerAddress());
			for(HRegionInfo region : regionServer.getOnlineRegions()){
				System.out.println("    || region - " + region);
				if(region.getTableNameAsString().equals(table)){
					tableRegionThread = hregionServer;
				}
			}
			System.out.println("    ||----------------------------------------------------------------");
//			File file = new File(regionServer.getHlog().getDir1().toUri().getRawPath());
//			if(oldHlogPath == null)
//				oldHlogPath = regionServer.getHlog().getOldDir().toUri().getRawPath();
//			String[] files = null;
//			long currentSize = 0;
//			if(file.isDirectory()){
//				files = file.list();
//				File fz = null;
//				for(String filePath : files){
//					fz = new File(file, filePath);
//					currentSize+=fz.length();
//				}
//			}
//			System.out.println("    || region hlog [" + regionServer.getRpcServer().getListenerAddress() + "]" + file + " files is [" + files.length + "] count = [" + currentSize + "]");
//			System.out.println("    ||----------------------------------------------------------------");
//			allCount+=currentSize;
		}
		File file = new File(oldHlogPath);
		String[] files = null;
		long oldCount = 0;
		System.out.println("    ||----------------------------------------------------------------");
		if(file.isDirectory()){
			files = file.list();
			File fz = null;
			for(String filePath : files){
				fz = new File(file, filePath);
				System.out.println("    ||" + fz.getPath());
				oldCount+=fz.length();
			}
		}
		System.out.println("hlogSize = " + allCount);
		System.out.println("oldSize = " + oldCount);
		System.out.println("    ||----------------------------------------------------------------");
		return new Cpx(tableRegionThread,allCount);
	}
	
	@Test
	public void testHregion() throws IOException, InterruptedException{
		Cpx cpx = printJvmInfoAndGetHRegionByTable(TABLE_STRS[0]);
		try
		{
			System.out.println("Hlog size is " + cpx.hlogSizeCount);
			System.out.println(cpx.thread.getRegionServer().getServerName().getServerName() + " stop!"); 
			cpx.thread.stop();
		}catch(RuntimeException ex){
		}finally{
			Thread.sleep(5000);
		}
		
		cpx = printJvmInfoAndGetHRegionByTable(TABLE_STRS[0]);
		System.out.println("Hlog size is " + cpx.hlogSizeCount);
		
//		hbaseCluster.getMaster(0).getAssignmentManager().assignMeta();
	}
	
	
	@Test
	public void testPutAgain() throws Exception {
		for(String tableStr : TABLE_STRS){
			HTable table = new HTable(getConf(), tableStr);
			List<Put> puts = new ArrayList<Put>();
			for(int x=0; x<100; x++){
				Put put = new Put(getRndString(TABLE_STR).getBytes());
				put.add(COL, "name".getBytes(), getRndString(null)
						.getBytes());
				puts.add(put);
			}
			table.put(puts);
			table.close();
		}
	}
}
