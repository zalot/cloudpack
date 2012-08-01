package org.sourceopen.analyze.hbase.hregionserver;

import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;

import org.apache.hadoop.hbase.util.JVMClusterUtil;
import org.apache.hadoop.hbase.util.JVMClusterUtil.RegionServerThread;
import org.sourceopen.TestHBase;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * HRegionServer High Availability Analyze <BR>
 * 1. HRegionServer Shutdown Analyze <BR>
 * 
 * @author zalot.zhaoh Mar 5, 2012 6:18:04 PM
 */
public class AnalyzeHRegionServerHighAvailability extends TestHBase {

    private static String[] Tables  = new String[] { "tableA", "tableB", "tableC", "tableD", "tableE", "tableF" };
    private static String[] Familys = new String[] { "colA", "colB" };

    @BeforeClass
    public static void init() throws Exception {
        initClusterA();
        startHBaseClusterA(3, 3);
        createTable(_confA, Tables, Familys);
        insertRndData(_poolA, Tables, Familys, null, 1000);
    }

    class HRegionThread {

        public JVMClusterUtil.RegionServerThread thread;
        public long                              hlogSizeCount;

        public HRegionThread(RegionServerThread thread, long hlogSizeCount){
            super();
            this.thread = thread;
            this.hlogSizeCount = hlogSizeCount;
        }
    }

    @Test
    public void testHregion() throws IOException, InterruptedException {
        printHRegionServer(_util1);
        RegionServerThread td = getHRegionByTable(_util1, Tables[0]);
        System.out.println(td.getRegionServer().getServerName().getServerName() + " stop!");
        try {
            td.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {

                @Override
                public void uncaughtException(Thread t, Throwable e) {
                    System.out.println(t.getName());
                }
            });
        } catch (Throwable ex) {
            System.out.print(ex.getMessage());
            System.out.println("Shutdown HRegionServer");
        } finally {
            Thread.sleep(5000);
        }

        td = getHRegionByTable(_util1, Tables[0]);
        System.out.println(td.getRegionServer().getServerName().getServerName());
        printHRegionServer(_util1);
        // hbaseCluster.getMaster(0).getAssignmentManager().assignMeta();
    }
}
