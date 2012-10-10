package org.sourceopen.analyze.hadoop.hive;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hive.cli.CliDriver;
import org.apache.hadoop.hive.cli.CliSessionState;
import org.apache.hadoop.hive.cli.OptionsProcessor;
import org.apache.hadoop.hive.common.io.CachingPrintStream;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.ql.exec.Utilities;
import org.apache.hadoop.hive.ql.session.SessionState;
import org.apache.hadoop.hive.shims.ShimLoader;
import org.junit.AfterClass;
import org.sourceopen.analyze.hadoop.TestBase1;

public class TestHive extends TestBase1 {

    protected static HiveConf         hiveConf     = null;
    protected static CliSessionState  sessionState = null;
    protected static OptionsProcessor oproc        = new OptionsProcessor();
    protected static boolean          initHive     = false;
    protected static CliDriver        cli          = null;

    public static enum HIVE {
        MYSQL("com.mysql.jdbc.Driver"), ORACLE("");

        protected String ds;

        HIVE(String driverClass){
            this.ds = driverClass;
        }

        public String toString() {
            return ds;
        }
    }

    @AfterClass
    public static void closeHive() throws Exception {
        if (sessionState != null) {
            sessionState.close();
        }
        closed();
    }

    public static void start(String mdbString, HIVE hive, String mdbUser, String mdbPwd) throws Exception {
        startHBaseClusterA(3, 3, 3);
        hiveConf = new HiveConf(SessionState.class);
        hiveConf.set("fs.default.name", _confA.get("fs.default.name"));
        hiveConf.set("mapred.job.tracker", _confA.get("mapred.job.tracker"));
        hiveConf.set("io.compression.codecs",
                     "org.apache.hadoop.io.compress.DefaultCodec,org.apache.hadoop.io.compress.GzipCodec,org.apache.hadoop.io.compress.BZip2Codec");

        hiveConf.set("javax.jdo.option.ConnectionURL", mdbString);
        // javax.jdo.option.ConnectionDriverName
        hiveConf.set("javax.jdo.option.ConnectionDriverName", hive.toString());
        hiveConf.set("javax.jdo.option.ConnectionUserName", mdbUser);
        hiveConf.set("javax.jdo.option.ConnectionPassword", mdbPwd);
        initHive();
    }

    private static int initHive() throws Exception {
        String[] args = new String[] {};
        if (!oproc.process_stage1(args)) {
            return 1;
        }
        sessionState = new CliSessionState(hiveConf);
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

        cli = new CliDriver();
        cli.setHiveVariables(oproc.getHiveVariables());

        // Execute -i init files (always in silent mode)
        cli.processInitFiles(sessionState);

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
        initHive = true;
        return 1;
    }

    public static int run(String sql) throws Exception {
        if (!initHive) {
            initHive();
        }
        sessionState.getConf().set("is.local", "false");
        return cli.processLine(sql, false);
    }

    public static int runLocal(String sql) throws Exception {
        if (!initHive) {
            initHive();
        }
        sessionState.getConf().set("is.local", "true");
        return cli.processLine(sql, false);
    }
}
