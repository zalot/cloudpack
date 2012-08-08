package org.sourceopen.analyze.hadoop.hbase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HRegionInfo;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.HTablePool;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.regionserver.HRegionServer;
import org.apache.hadoop.hbase.util.JVMClusterUtil;
import org.apache.hadoop.hbase.util.JVMClusterUtil.RegionServerThread;
import org.sourceopen.analyze.hadoop.TestBase;

public class TestHBase extends TestBase {

    public static void createDefTable(Configuration conf) throws IOException {
        createTable(conf, new String[] { "testA", "testB" }, new String[] { "colA", "colB" });
    }

    public static void createTable(Configuration conf, String[] tables, String[] familys) throws IOException {
        HBaseAdmin admin = new HBaseAdmin(conf);
        for (String tableInfo : tables) {
            HTableDescriptor htableDes = new HTableDescriptor(tableInfo);
            for (String family : familys) {
                htableDes.addFamily(new HColumnDescriptor(family));
            }
            admin.createTable(htableDes);
        }
    }

    public static void insertRndData(HTablePool pool, String[] tables, String[] familys, String qualifier, int size)
                                                                                                                    throws IOException {
        for (String table : tables) {
            for (String family : familys)
                insertRndData(pool, table, family, qualifier, size);
        }
    }

    public static void insertRndData(HTablePool pool, String table, String family, String qualifier, int size)
                                                                                                              throws IOException {
        HTableInterface htable = pool.getTable(table);
        List<Put> puts = new ArrayList<Put>();
        for (int x = 0; x < size; x++) {
            Put put = new Put(getRndString(table).getBytes());
            put.add(family.getBytes(), qualifier == null ? getRndString("rnd-").getBytes() : qualifier.getBytes(),
                    getRndString(null).getBytes());
            puts.add(put);
        }
        htable.put(puts);
        System.out.println("insert Data " + size);
    }

    protected void printHRegionServer(HBaseTestingUtility util) throws IOException {
        for (JVMClusterUtil.RegionServerThread hregionThread : util.getHBaseCluster().getLiveRegionServerThreads()) {
            long count = 0;
            HRegionServer regionServer = hregionThread.getRegionServer();
            System.out.println("RegionServer -- " + regionServer.getRpcServer().getListenerAddress());
            for (HRegionInfo region : regionServer.getOnlineRegions()) {
                System.out.println("    || region - " + region);
            }
        }
    }

    protected RegionServerThread getHRegionByTable(HBaseTestingUtility util, String table) throws IOException {
        for (JVMClusterUtil.RegionServerThread hregionThread : util.getHBaseCluster().getLiveRegionServerThreads()) {
            HRegionServer regionServer = hregionThread.getRegionServer();
            for (HRegionInfo region : regionServer.getOnlineRegions()) {
                if (region.getTableNameAsString().equals(table)) {
                    return hregionThread;
                }
            }
        }
        return null;
    }
}
