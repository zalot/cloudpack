package org.sourceopen.analyze.hadoop.hbase;

import java.io.IOException;

import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HRegionInfo;
import org.apache.hadoop.hbase.regionserver.HRegionServer;
import org.apache.hadoop.hbase.util.JVMClusterUtil;
import org.apache.hadoop.hbase.util.JVMClusterUtil.RegionServerThread;
import org.sourceopen.analyze.hadoop.TestBase;

public class TestHBase extends TestBase {

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
