package org.sourceopen.base;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.cli.CliDriver;
import org.apache.hadoop.hive.cli.CliSessionState;
import org.apache.hadoop.hive.cli.OptionsProcessor;
import org.apache.hadoop.hive.common.io.CachingPrintStream;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.HiveMetaStore;
import org.apache.hadoop.hive.ql.exec.Utilities;
import org.apache.hadoop.hive.ql.session.SessionState;
import org.apache.hadoop.hive.shims.ShimLoader;
import org.apache.thrift.transport.TTransportException;
import org.junit.AfterClass;
import org.sourceopen.hadoop.hive.ql.http.QueryPlanHelper;

public class HiveBase extends HBaseBase {

    protected final static Map<String, HiveConf>     hiveConfs = new HashMap<String, HiveConf>();
    protected final static Map<String, SessionState> hiveStats = new HashMap<String, SessionState>();
    protected final static Map<String, CliDriver>    drivers   = new HashMap<String, CliDriver>();
    protected final static OptionsProcessor          oproc     = new OptionsProcessor();
    protected static boolean                         runLocal  = false;

    public static void selectHiveKey(String key) {
        if (curKey == null) curKey = key;
        if (hiveConfs.get(key) != null && !curKey.equals(key)) {
            oldKey = curKey;
            curKey = key;
        }
        if (oldKey == null) oldKey = key;
    }

    @AfterClass
    public static void closeHive() throws Exception {
        for (SessionState s : hiveStats.values()) {
            if (s instanceof CliSessionState) ((CliSessionState) s).close();
        }
        closeHadoop();
    }

    public static enum HIVE_METADB {
        MYSQL("com.mysql.jdbc.Driver"), ORACLE(""), DERBY("org.apache.derby.jdbc.EmbeddedDriver");

        protected String ds;

        HIVE_METADB(String driverClass){
            this.ds = driverClass;
        }

        public String toString() {
            return ds;
        }
    }

    public static void startHive(String key, String mdbString, HIVE_METADB hive, String mdbUser, String mdbPwd,
                                 Configuration conf) throws Exception {
        final HiveConf hiveConf = new HiveConf(SessionState.class);
        if (conf != null) {
            hiveConf.set("fs.default.name", conf.get("fs.default.name"));
            hiveConf.set("mapred.job.tracker", conf.get("mapred.job.tracker"));
        }
        hiveConf.set("io.compression.codecs",
                     "org.apache.hadoop.io.compress.DefaultCodec,org.apache.hadoop.io.compress.GzipCodec,org.apache.hadoop.io.compress.BZip2Codec");

        hiveConf.set("javax.jdo.option.ConnectionURL", mdbString);
        hiveConf.set("javax.jdo.option.ConnectionDriverName", hive.toString());
        hiveConf.set("javax.jdo.option.ConnectionUserName", mdbUser);
        hiveConf.set("javax.jdo.option.ConnectionPassword", mdbPwd);
        initHive(key, hiveConf);
        selectHiveKey(key);
    }

    private static int initHive(String key, HiveConf hiveConf) throws Exception {
        String[] args = new String[] {};
        if (!oproc.process_stage1(args)) {
            return 1;
        }
        CliSessionState sessionState = new CliSessionState(hiveConf);
        sessionState.in = System.in;
        try {
            sessionState.out = new PrintStream(System.out, true, "UTF-8");
            sessionState.info = new PrintStream(System.err, true, "UTF-8");
            sessionState.err = new CachingPrintStream(System.err, true, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return 3;
        }

        if (!oproc.process_stage2(sessionState)) {
            return 2;
        }

        // set all properties specified via command line
        HiveConf conf = sessionState.getConf();
        for (Map.Entry<Object, Object> item : sessionState.cmdProperties.entrySet()) {
            conf.set((String) item.getKey(), (String) item.getValue());
            sessionState.getOverriddenConfigurations().put((String) item.getKey(), (String) item.getValue());
        }

        SessionState.start(sessionState);
        if (sessionState.getHost() != null) {
            sessionState.connect();
        }

        // CLI remote mode is a thin client: only load auxJars in local mode
        if (!sessionState.isRemoteMode() && !ShimLoader.getHadoopShims().usesJobShell()) {
            // hadoop-20 and above - we need to augment classpath using hiveconf
            // components
            // see also: code in ExecDriver.java
            ClassLoader loader = conf.getClassLoader();
            String auxJars = HiveConf.getVar(conf, HiveConf.ConfVars.HIVEAUXJARS);
            if (StringUtils.isNotBlank(auxJars)) {
                loader = Utilities.addToClassPath(loader, StringUtils.split(auxJars, ","));
            }
            conf.setClassLoader(loader);
            Thread.currentThread().setContextClassLoader(loader);
        }

        CliDriver cli = new CliDriver();
        cli.setHiveVariables(oproc.getHiveVariables());

        // Execute -i init files (always in silent mode)
        cli.processInitFiles(sessionState);

        drivers.put(key, cli);
        System.out.println("hive stat->" + sessionState.getConf().get("mapred.job.tracker"));
        hiveStats.put(key, sessionState);
        hiveConfs.put(key, hiveConf);
        if (sessionState.execString != null) {
            return cli.processLine(sessionState.execString);
        }

        try {
            if (sessionState.fileName != null) {
                return cli.processFile(sessionState.fileName);
            }
        } catch (FileNotFoundException e) {
            System.err.println("Could not open input file for reading. (" + e.getMessage() + ")");
            return 3;
        }

        return 1;
    }

    public static void printHiveKeys() {
        for (String key : hiveConfs.keySet())
            System.out.println(key + "=" + hiveConfs.get(key).get("mapred.job.tracker"));
    }

    protected static String oldKey = null;
    protected static String curKey = null;

    public static int run(String sql, String key) throws Exception {
        return r(sql, key, false);
    }

    public static int runLocal(String sql, String key) throws Exception {
        return r(sql, key, true);
    }

    public static int runLocal(String sql) throws Exception {
        return runLocal(sql, getCurKey());
    }

    public static int run(String sql) throws Exception {
        return run(sql, getCurKey());
    }

    private static int r(String sql, String key, boolean islocal) throws TTransportException, IOException {
        if (hiveStats.get(key) != null) {
            setHiveLocalModel(hiveStats.get(key).getConf(), islocal);
            SessionState.start(hiveStats.get(key));
            drivers.get(key).processInitFiles((CliSessionState) hiveStats.get(key));
            return drivers.get(key).processLine(sql, false);
        } else {
            System.out.println("ERROR - illegal key - " + key);
            printHiveKeys();
        }
        return -1;
    }

    public static void set(String k, String v) {
        hiveConfs.get(getCurKey()).set(k, v);
    }

    public static class MetaStoreRunnable extends Thread {

        int              port;
        private HiveConf conf;

        public MetaStoreRunnable(int port, HiveConf hiveConf){
            this.port = port;
            this.conf = hiveConf;
        }

        @Override
        public void run() {
            try {
                this.conf.set("hive.metastore.server.min.threads", "30");
                this.conf.set("hive.metastore.server.max.threads", "100");

                HiveMetaStore.startMetaStore(this.port, ShimLoader.getHadoopThriftAuthBridge(), this.conf);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

    };

    static Thread meta = null;

    public static void startMetaStoreService(int port, String key) throws Throwable {
        if (hiveConfs.get(key) != null) {
            meta = new MetaStoreRunnable(port, hiveConfs.get(key));
            meta.setDaemon(true);
            meta.start();
        }
    }

    public static void hiveCmd() throws Exception {
        BufferedReader read = new BufferedReader(new InputStreamReader(System.in));
        String cmd = "";
        while (true) {
            System.out.println("hive[-key=" + curKey + ",-local" + (runLocal ? "[true]" : "") + ",-remote"
                               + (runLocal ? "" : "[true]") + ",printkey,quit] >");
            String tm = read.readLine();
            if (tm.trim().length() < 1) continue;
            if (tm.indexOf("-key=") >= 0 && tm.length() > 5) {
                selectHiveKey(tm.substring(5, tm.length()));
                System.out.println("select key " + curKey);
                continue;
            }
            if ("-local".equals(tm)) {
                runLocal = true;
                continue;
            }
            if ("-remote".equals(tm)) {
                runLocal = false;
                continue;
            }
            if ("printkey".equals(tm)) {
                printHiveKeys();
                continue;
            }
            if ("quit".equals(tm)) {
                break;
            }
            if (tm.charAt(tm.length() - 1) != ';') {
                cmd += tm;
                continue;
            } else {
                cmd += tm.substring(0, tm.length() - 1);
            }

            if (curKey == null) {
                System.out.println("not init hivekey , pls invoke startHive(xxx)/startMemoryHive() method .... or invoke selectHiveKey() ...");
                continue;
            }
            QueryPlanHelper.cleanHook(HiveBase.getCurConf());
            if (runLocal) {
                runLocal(cmd);
                cmd = "";
            } else {
                run(cmd);
                cmd = "";
            }
        }
    }

    public static void startMemoryHive(String key, Configuration conf) throws Exception {
        startHive(key, "jdbc:derby:memory:testDB;create=true", HIVE_METADB.DERBY, "APP", "mine", conf);
    }

    public static String getCurKey() {
        return curKey;
    }

    public static void setHiveLocalModel(HiveConf conf, boolean islocal) {
        conf.setBoolVar(HiveConf.ConfVars.LOCALMODEAUTO, islocal);
    }

    public static Configuration getCurConf() {
        return hiveConfs.get(getCurKey());
    }

    public static void printHiveInfo(String base) {
    }
}
