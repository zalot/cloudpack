package org.sourceopen.base;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.HRegionInfo;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.HTablePool;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.regionserver.HRegionServer;
import org.apache.hadoop.hbase.util.JVMClusterUtil;
import org.apache.hadoop.hbase.util.JVMClusterUtil.RegionServerThread;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MiniMRCluster;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;
import org.junit.AfterClass;
import org.sourceopen.hadoop.hbase.utils.HRepConfigUtil;
import org.sourceopen.hadoop.zookeeper.connect.AdvZooKeeper;
import org.sourceopen.hadoop.zookeeper.connect.NothingZookeeperWatch;
import org.sourceopen.hadoop.zookeeper.connect.ZookeeperFactory;

public class HBaseBase extends HadoopBase {

    @AfterClass
    public static void close() throws Exception {
        if (_startA) {
            _utilA.shutdownMiniCluster();
            _utilA.toString();
            _utilA.shutdownMiniZKCluster();
        }
        if (_startB) {
            _utilB.shutdownMiniCluster();
            _utilB.shutdownMiniZKCluster();
        }
    }

    private static void initHBaseClusterA() {
        _confA = HBaseConfiguration.create();
        _confA.setBoolean("hbase.regionserver.info.port.auto", true);
        _confA.setLong("hbase.regionserver.hlog.blocksize", 1024 * 1);
        _confA.setInt("hbase.regionserver.maxlogs", 2);
        _confA.set(HConstants.ZOOKEEPER_ZNODE_PARENT, "/1");
        _confA.setBoolean("dfs.support.append", true);
        _confA.setInt("hbase.master.info.port", 60010);
    }

    public static void startHBaseClusterA(int hbaseNum, int mapr, int zkNum) throws Exception {
        if (!_startA) {
            initHBaseClusterA();
            _utilA = new HBaseTestingUtility(_confA);
            _utilA.startMiniZKCluster(zkNum);
            // 内置了 System.out.println(System.getProperty("hadoop.log.dir"));
            if (hbaseNum > 0) _utilA.startMiniCluster(1, hbaseNum);
            _poolA = new HTablePool(_confA, 20);
            _startA = true;
            if (mapr > 0) {
                _mrA = startMiniMapRServer(mapr, _confA);
            }
            if (hbaseNum > 0) printHadoopInfo("ClusterA", _confA, _utilA, _mrA);
            FileOutputStream fout = new FileOutputStream(new File(_confAPath));
            _confA.writeXml(fout);
            fout.close();
        }
    }

    public static void startHBaseClusterA(int hbaseNum, int zkNum) throws Exception {
        startHBaseClusterA(hbaseNum, 0, zkNum);
    }

    private static void initClusterB() {
        _confB = HBaseConfiguration.create();
        _confB.set(HConstants.ZOOKEEPER_ZNODE_PARENT, "/2");
        _confB.setBoolean("dfs.support.append", true);
        _confB.setBoolean("hbase.regionserver.info.port.auto", true);
        _confB.setLong("hbase.regionserver.hlog.blocksize", 1024);
        _confB.setInt("hbase.master.info.port", 60011);
    }

    public static void startHBaseClusterB(int hbaseNum, int zkNum) throws Exception {
        startHBaseClusterB(hbaseNum, 0, zkNum);
    }

    public static void startHBaseClusterB(int hbaseNum, int mapr, int zkNum) throws Exception {
        if (!_startB) {
            initClusterB();
            _utilB = new HBaseTestingUtility(_confB);
            _utilB.startMiniZKCluster(zkNum);
            _utilB.startMiniMapReduceCluster(3);
            if (hbaseNum > 0) _utilB.startMiniCluster(1, hbaseNum);
            _poolB = new HTablePool(_confB, 20);
            if (mapr > 0) {
                _mrB = startMiniMapRServer(mapr, _confB);
            }
            FileOutputStream fout = new FileOutputStream(new File(_confBPath));
            _confB.writeXml(fout);
            fout.close();
            printHadoopInfo("ClusterB", _confB, _utilB, _mrB);
        }
    }

    public static String getRndStringInArray(String[] strArray) {
        return strArray[rnd.nextInt(strArray.length)];
    }

    public static AdvZooKeeper getAdvZooKeeperByConfig(Configuration conf) throws Exception {
        String url = HRepConfigUtil.getZKStringV1(conf);
        AdvZooKeeper zk = ZookeeperFactory.createRecoverableZooKeeper(url, 200, new NothingZookeeperWatch(), 10, 1000);
        Assert.assertEquals(zk.getChildren("/", false).size(), 1);
        zk.create("/test1", null, Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        Assert.assertEquals(zk.getChildren("/", false).size(), 2);
        Stat stat = zk.exists("/test1", false);
        if (stat != null) zk.delete("/test1", stat.getVersion());
        Assert.assertEquals(zk.getChildren("/", false).size(), 1);
        return zk;
    }

    protected static MiniMRCluster startMiniMapRServer(int servers, Configuration conf) throws IOException {
        conf.set("mapred.output.dir", conf.get("hadoop.tmp.dir"));
        MiniMRCluster mrCluster = new MiniMRCluster(servers, FileSystem.get(conf).getUri().toString(), 1);
        JobConf jobConf = mrCluster.createJobConf();
        conf.set("mapred.job.tracker", jobConf.get("mapred.job.tracker"));
        conf.set("mapreduce.framework.name", "yarn");
        conf.set("mapred.job.tracker.http.address", jobConf.get("mapred.job.tracker.http.address"));
        return mrCluster;
    }

    protected static void printClusterAInfo() {
        printInfo("ClusterA", _confA, _utilA, _mrA);
    }

    protected static void printClusterBInfo() {
        printInfo("ClusterB", _confB, _utilB, _mrB);
    }

    public static void printInfo(String base, Configuration conf, HBaseTestingUtility util, MiniMRCluster mrc) {
        System.out.println("-HBASE-");
        String hbaseHttp = "http://localhost:" + conf.get("hbase.master.info.port");
        System.out.println(base + "-HBase HttpServer: " + hbaseHttp);
    }

    public static void serverWait(long waitTime) throws InterruptedException {
        synchronized (Thread.currentThread()) {
            if (waitTime <= 0) Thread.currentThread().wait();
            else Thread.currentThread().wait(waitTime);
        }
    }

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
