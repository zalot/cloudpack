package org.sourceopen.analyze.hadoop.hcatalog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URL;
import java.util.HashSet;
import java.util.Properties;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.pig.ExecType;
import org.apache.pig.backend.hadoop.datastorage.ConfigurationUtil;
import org.apache.pig.impl.PigContext;
import org.apache.pig.impl.util.ObjectSerializer;
import org.apache.pig.impl.util.PropertiesUtil;
import org.apache.pig.impl.util.UDFContext;
import org.junit.Test;
import org.sourceopen.analyze.hadoop.hive.datagenerator.HiveTableCreate_User_Role_Sell;
import org.sourceopen.base.HiveBase;

public class TestHCatalogServer extends HiveBase {

    public static String        mdbString          = "jdbc:mysql://10.20.153.121:3306/hive?useUnicode=true&amp;characterEncoding=utf-8";

    protected static boolean    initPig            = false;
    protected static PigContext pigContext;
    public static int           METASERVICE_PORT   = 50123;
    public static String        METASERVICE_OPS    = "hive.metastore.uris";
    public static String        METASERVICE_THRIFT = "thrift://localhost:" + METASERVICE_PORT;

    public static void initPig() throws IOException {
        Properties properties = new Properties();
        PropertiesUtil.loadDefaultProperties(properties);
        properties.putAll(ConfigurationUtil.toProperties(new Configuration(false)));
        pigContext = new PigContext(ExecType.MAPREDUCE, properties);
        pigContext.getProperties().setProperty("pig.logfile",
                                               System.getProperty("user.dir") + "/build/piglog/" + rnd.nextInt()
                                                       + ".log");
        pigContext.getProperties().setProperty(METASERVICE_OPS, METASERVICE_THRIFT);
        HashSet<String> optimizerRules = new HashSet<String>();
        if (!Boolean.valueOf(properties.getProperty("pig.exec.filterLogicExpressionSimplifier", "false"))) {
            optimizerRules.add("FilterLogicExpressionSimplifier");
        }

        if (optimizerRules.size() > 0) {
            pigContext.getProperties().setProperty("pig.optimizer.rules", ObjectSerializer.serialize(optimizerRules));
        }
        PigContext.setClassLoader(pigContext.createCl(null));
        UDFContext.getUDFContext().setClientSystemProps(properties);
        pigContext.getProperties().setProperty("pig.cmd.args", "");
    }

    public static class BreadT implements Runnable {

        BufferedReader input;
        boolean        iserror = false;

        public BreadT(BufferedReader input, boolean ie){
            this.input = input;
            this.iserror = ie;
        }

        @Override
        public void run() {
            String line = null;
            try {
                while ((line = input.readLine()) != null) {
                    System.out.println((iserror ? "--" : "") + line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static void runPigCmd() throws IOException {
        String classPath = System.getProperty("java.class.path");
        String[] jars = classPath.split(":");
        URL[] urls = new URL[jars.length];
        int x = 0;
        String tclassPath = classPath.replace("antlr-3.0.1.jar", "").replace("antlr-runtime-3.0.1.jar", "");
        for (String jar : jars) {
            String url = "";
            if (jar.indexOf("antlr-3.0.1") > 0) continue;
            if (jar.indexOf(".jar") > 0) {
                url = "file://" + jar;
            } else {
                url = "file://" + jar + "/";
            }
            System.out.println(url);
            tclassPath += jar + ":";
            urls[x++] = new URL(url);
        }
        String[] args = new String[] { "java", "-classpath", "-D" + METASERVICE_OPS, METASERVICE_THRIFT,
                tclassPath.substring(0, tclassPath.length() - 1), "org.apache.pig.Main" };
        Process child = Runtime.getRuntime().exec(args);

        BufferedReader input = new BufferedReader(new InputStreamReader(child.getInputStream()));
        BufferedReader error = new BufferedReader(new InputStreamReader(child.getErrorStream()));
        try {
            Thread outT = new Thread(new BreadT(input, false));
            outT.setDaemon(true);
            outT.start();
            Thread outE = new Thread(new BreadT(error, true));
            outE.setDaemon(true);
            outE.start();

            PrintStream print = new PrintStream(child.getOutputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                System.out.println("grunt>");
                String tm = reader.readLine();
                if ("exit".equals(tm)) {
                    input.close();
                    error.close();
                    break;
                }
                print.println(tm);
                print.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void startHCatalogServer() throws Throwable {
        Configuration h0conf = new Configuration();
        h0conf.set("mapred.job.tracker", "h0:54311");
        h0conf.set("fs.default.name", "hdfs://h0:9100");
        startHive("h0", mdbString, HIVE_METADB.MYSQL, "root", "123456", h0conf);

        startHadoopClusterA(2, 2);
        startHive("local", mdbString, HIVE_METADB.MYSQL, "root", "123456", _confA);
        curKey = "local";
        oldKey = "local";
        startMetaStoreService(METASERVICE_PORT, "local");

        BufferedReader read = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            System.out.print("[pig,hive,exit,tmpcreate,printa,classpath] >");
            String tm = read.readLine().trim();
            if ("pig".equals(tm)) {
                runPigCmd();
            }
            if ("hive".equals(tm)) {
                hiveCmd();
            }
            if ("tmpcreate".equals(tm)) {
                HiveConf hcon = hiveConfs.get(curKey);
                FileSystem fs = FileSystem.get(hcon);
                HiveTableCreate_User_Role_Sell.createSellData(fs);
                HiveTableCreate_User_Role_Sell.createUserData(fs);
                HiveTableCreate_User_Role_Sell.createRuleData(fs);

                run(HiveTableCreate_User_Role_Sell.getDropDataBaseHQL(), curKey);
                run(HiveTableCreate_User_Role_Sell.getCreateDataBaseHQL(), curKey);

                run(HiveTableCreate_User_Role_Sell.getCreateRoleHQL(), curKey);
                run(HiveTableCreate_User_Role_Sell.getCreateSellHQL(), curKey);
                run(HiveTableCreate_User_Role_Sell.getCreateUserHQL(), curKey);

                run(HiveTableCreate_User_Role_Sell.loadRoleHQL(), curKey);
                run(HiveTableCreate_User_Role_Sell.loadUserHQL(), curKey);
                run(HiveTableCreate_User_Role_Sell.loadSellHQL(), curKey);
            }
            if ("classpath".equals(tm)) {
                System.out.println(System.getProperty("java.class.path"));
            }
            if ("printa".equals(tm)) {
                printHadoopClusterAInfo();
            }
            if ("exit".equals(tm)) {
                return;
            }
        }
    }
}
