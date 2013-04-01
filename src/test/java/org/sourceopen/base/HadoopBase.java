package org.sourceopen.base;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Random;
import java.util.UUID;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.client.HTablePool;
import org.apache.hadoop.hdfs.server.datanode.DataNode;
import org.apache.hadoop.http.HttpServer;
import org.apache.hadoop.mapred.MiniMRCluster;
import org.apache.hadoop.net.NetUtils;
import org.junit.AfterClass;

/**
 * 集群基础测试类<BR>
 * 类BaseReplicationTest.java的实现描述：TODO 类实现描述
 * 
 * @author zalot.zhaoh Mar 5, 2012 6:19:57 PM
 */
public class HadoopBase {

    protected static Random              rnd        = new Random();
    protected static HBaseTestingUtility _utilA;
    protected static Configuration       _confA;
    public static final String           _confAPath = "/tmp/hadoop-site-clusta.xml";

    public static void loadResourceA(Configuration conf) {
        conf.addResource(new Path(_confAPath));
    }

    protected static HTablePool          _poolA;
    protected static MiniMRCluster       _mrA       = null;
    protected static boolean             _startA    = false;

    protected static HBaseTestingUtility _utilB;
    protected static Configuration       _confB;
    public static final String           _confBPath = "/tmp/hadoop-site-clustb.xml";
    protected static HTablePool          _poolB;
    protected static MiniMRCluster       _mrB       = null;
    protected static boolean             _startB    = false;

    @AfterClass
    public static void closeHadoop() throws Exception {
        if (_startA) {
            _utilA.shutdownMiniCluster();
            _utilA.shutdownMiniZKCluster();
        }
        if (_startB) {
            _utilB.shutdownMiniCluster();
            _utilB.shutdownMiniZKCluster();
        }
    }

    public static void startHadoopClusterA(int dn, int tn) throws Exception {
        if (!_startA) {
            _utilA = new HBaseTestingUtility(_confA);
            // 内置了 System.out.println(System.getProperty("hadoop.log.dir"));
            _utilA.startMiniDFSCluster(dn > 0 ? dn : 1);
            _startA = true;
            if (tn > 0) {
                _utilA.startMiniMapReduceCluster(tn);
            }
            printHadoopInfo("ClusterA", _confA, _utilA, _mrA);
            FileOutputStream fout = new FileOutputStream(new File(_confAPath));
            _confA.writeXml(fout);
            fout.close();
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

    protected static void printHadoopClusterAInfo() {
        printHadoopInfo("ClusterA", _confA, _utilA, _mrA);
    }

    protected static void printHadoopClusterBInfo() {
        printHadoopInfo("ClusterB", _confB, _utilB, _mrB);
    }

    public static void printHadoopInfo(String base, Configuration conf, HBaseTestingUtility util, MiniMRCluster mrc) {
        String nnHttp = "http://" + conf.get("dfs.http.address");
        String snHttp = "http://" + conf.get("dfs.secondary.http.address");
        String taskHttp = "http://" + conf.get("mapred.job.tracker.http.address");
        System.out.println("-------------------------------------------------------------------");
        System.out.println("-HADOOP-");
        System.out.println(base + "-NameNode HttpServer: " + nnHttp);
        System.out.println(base + "-SecondaryNameNode HttpServer: " + snHttp);
        System.out.println(base + "-JobTracker HttpServer: " + taskHttp);
        System.out.println(base + "-JobTracker RPC: " + conf.get("mapred.job.tracker"));
        System.out.println(base + "-NN RPC" + util.getDFSCluster().getNameNode().getNameNodeAddress());
        for (DataNode dn : util.getDFSCluster().getDataNodes()) {
            System.out.println(base + "-DN RPC : " + dn.getSelfAddr());
        }
        System.out.println("-------------------------------------------------------------------");
    }

    public static HttpServer startHTTPServer(String name, String host, int port, Configuration conf) throws IOException {
        String infoAddr = host + ":" + port;
        InetSocketAddress infoSocAddr = NetUtils.createSocketAddr(infoAddr);
        String infoHost = infoSocAddr.getHostName();
        int infoPort = infoSocAddr.getPort();
        return new HttpServer(name, infoHost, infoPort, infoPort == 0, conf == null ? new Configuration() : conf);
    }
}
