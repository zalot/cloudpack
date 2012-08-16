package org.sourceopen.analyze.hadoop;

import java.io.IOException;
import java.util.Random;
import java.util.UUID;

import junit.framework.Assert;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.client.HTablePool;
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

/**
 * 集群基础测试类<BR>
 * 类BaseReplicationTest.java的实现描述：TODO 类实现描述
 * 
 * @author zalot.zhaoh Mar 5, 2012 6:19:57 PM
 */
public class TestBase {

    protected static Random              rnd     = new Random();
    protected static HBaseTestingUtility _utilA;
    protected static Configuration       _confA;
    protected static HTablePool          _poolA;
    protected static MiniMRCluster       _mrA    = null;
    private static boolean               _startA = false;

    protected static HBaseTestingUtility _utilB;
    protected static Configuration       _confB;
    protected static HTablePool          _poolB;
    protected static MiniMRCluster       _mrB    = null;
    private static boolean               _startB = false;

    @AfterClass
    public static void closed() throws Exception {
        if (_startA) {
            _utilA.shutdownMiniCluster();
            _utilA.shutdownMiniZKCluster();
        }
        if (_startB) {
            _utilB.shutdownMiniCluster();
            _utilB.shutdownMiniZKCluster();
        }
    }

    private static void initClusterA() {
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
            initClusterA();
            _utilA = new HBaseTestingUtility(_confA);
            _utilA.startMiniZKCluster(zkNum);
            if (hbaseNum > 0) _utilA.startMiniCluster(1, hbaseNum);
            _poolA = new HTablePool(_confA, 20);
            _startA = true;
            if (mapr > 0) {
                _mrA = startMiniMapRServer(mapr, _confA);
            }
            printInfo("ClusterA", _confA);
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
            if (hbaseNum > 0) _utilB.startMiniCluster(1, hbaseNum);
            _poolB = new HTablePool(_confB, 20);
            if (mapr > 0) {
                _mrB = startMiniMapRServer(mapr, _confB);
            }
            printInfo("ClusterB", _confA);
        }
    }

    public static String getRndString(String base) {
        return base + UUID.randomUUID().toString().substring(0, 10);
    }

    public static String getRndStringInArray(String[] strArray) {
        return strArray[rnd.nextInt(strArray.length)];
    }

    public static void printDFS(FileSystem fs, Path path) throws IOException {
        if (fs.isFile(path)) {
            System.out.println(path.toString() + " - len = " + fs.getFileStatus(path).getLen());
        } else {
            for (FileStatus fss : fs.listStatus(path)) {
                printDFS(fs, fss.getPath());
            }
        }
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

    private static void printInfo(String base, Configuration conf) {
        String nnHttp = "http://" + conf.get("dfs.http.address");
        String snHttp = "http://" + conf.get("dfs.secondary.http.address");
        String taskHttp = "http://" + conf.get("mapred.job.tracker.http.address");
        System.out.println(base + "-NameNode : " + nnHttp);
        System.out.println(base + "-SecondaryNameNode : " + snHttp);
        System.out.println(base + "-JobTracker : " + taskHttp);
    }
}
