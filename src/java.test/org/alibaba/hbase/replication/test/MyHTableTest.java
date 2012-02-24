
package org.alibaba.hbase.replication.test;

import java.io.IOException;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.HTablePool;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class MyHTableTest {
	private final static HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();
	private final byte[] TABLE = Bytes.toBytes("zhaohengtest");
	private final byte[] COL = Bytes.toBytes("col");

	@BeforeClass
	public static void init() throws Exception {
		TEST_UTIL.shutdownMiniCluster();
		TEST_UTIL.startMiniCluster();
	}

	@Test
	public void testCreateTable() throws IOException {
		Configuration conf = TEST_UTIL.getConfiguration();
		HBaseAdmin admin = new HBaseAdmin(conf);
		HTableDescriptor tableDes = new HTableDescriptor(TABLE);
		HColumnDescriptor columnDes = new HColumnDescriptor(COL);
		tableDes.addFamily(columnDes);
		admin.createTable(tableDes);
		assert admin.getTableDescriptor(TABLE) != null;
	}
	
	@Test
	public void testPut() throws IOException{
		HTablePool pool = new HTablePool(TEST_UTIL.getConfiguration(), 1000);
		HTable table = (HTable) pool.getTable(TABLE);
		Put put = new Put(Bytes.toBytes("zhaoheng"));
		put.add(COL, Bytes.toBytes("name") , Bytes.toBytes("zhaoheng"));
		table.put(put);
		
		Get get = new Get("zhaoheng".getBytes());
		Result res = table.get(get);
		List<KeyValue> kys = res.getColumn(COL, "name".getBytes());
		
		System.out.println(kys.get(0).getTimestamp());
		
		put = new Put(Bytes.toBytes("zhaoheng"));
		put.add(COL, Bytes.toBytes("name") , Bytes.toBytes("zhaoheng1"));
		table.put(put);
		
		res = table.get(get);
		kys = res.getColumn(COL, "name".getBytes());
	}
	
	public void testDelte(){
		
	}
	
	public void testScan(){
		
	}

	public void testDeleteTable() throws IOException {
		Configuration conf = TEST_UTIL.getConfiguration();
		HBaseAdmin admin = new HBaseAdmin(conf);

		admin.disableTable(TABLE);
		admin.deleteTable(TABLE);

		assert admin.getTableDescriptor(TABLE) == null;
	}
	
	@AfterClass
	public static void des() throws Exception{
		TEST_UTIL.shutdownMiniCluster();
	}
	
}
