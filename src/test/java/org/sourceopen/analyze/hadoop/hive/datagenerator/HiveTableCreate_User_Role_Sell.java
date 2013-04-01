package org.sourceopen.analyze.hadoop.hive.datagenerator;

import java.io.IOException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.
apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.sourceopen.base.HadoopBase;
import org.sourceopen.hadoop.hdfs.io.utils.HdfsFileUtil;
import org.sourceopen.hadoop.hdfs.io.utils.HdfsFileUtil.FileGenerator;

public class HiveTableCreate_User_Role_Sell {

    public static Path  TABLE_USER_PATH = new Path("/tmp/table-user.txt");
    public static Path  TABLE_ROLE_PATH = new Path("/tmp/table-role.txt");
    public static Path  TABLE_SELL_PATH = new Path("/tmp/table-sell.txt");
    static SecureRandom rnd             = new SecureRandom();

    /**
     * <table border="1">
     * <tr>
     * <th>userid</th>
     * <th>name</th>
     * <th>sex</th>
     * <th>age</th>
     * <th>roleid</th>
     * </tr>
     * <tr>
     * <td>seq(1-20000)</td>
     * <td>rnd-string</td>
     * <td>man/women</td>
     * <td>1-99</td>
     * <td>rnd(1-100)</td>
     * </tr>
     * </table>
     * 
     * @param fs
     * @param path
     * @throws Exception
     */
    public static String createUserData(FileSystem fs, Path path) throws Exception {
        HdfsFileUtil.genHdfsFile(fs, new FileGenerator("\t") {

            @Override
            public void gen() throws IOException {
                for (int x = 1; x < 20000; x++)
                    // id , name , sex , age , ruleid
                    addFormatLine(String.valueOf(x), "name" + x, rnd.nextInt(6) > 3 ? "man" : "woman",
                                  String.valueOf(rnd.nextInt(90)), String.valueOf(1 + rnd.nextInt(49)));
            }
        }, path, true, true);
        return null;
    }

    public static String createUserData(FileSystem fs) throws Exception {
        return createUserData(fs, TABLE_USER_PATH);
    }

    /**
     * <table border="1">
     * <tr>
     * <th>roleid</th>
     * <th>rolename</th>
     * <th>rolepercent</th>
     * </tr>
     * <tr>
     * <td>seq(1-100)</td>
     * <td>rnd-string</td>
     * <td>0.1 - 0.5</td>
     * </tr>
     * </table>
     * 
     * @param fs
     * @param path
     * @throws Exception
     */
    public static String createRuleData(FileSystem fs, Path path) throws Exception {
        HdfsFileUtil.genHdfsFile(fs, new FileGenerator("\t") {

            @Override
            public void gen() throws IOException {
                for (int x = 1; x < 50; x++)
                    addFormatLine(String.valueOf(x), "rule" + x, String.valueOf((double) (10 + x) / 100));
            }
        }, path, true, true);
        return null;
    }

    public static String createRuleData(FileSystem fs) throws Exception {
        return createRuleData(fs, TABLE_ROLE_PATH);
    }

    /**
     * <table border="1">
     * <tr>
     * <th>userid</th>
     * <th>selldate</th>
     * <th>sellamount</th>
     * <th>sellmoney</th>
     * </tr>
     * <tr>
     * <td>seq(1-20000)</td>
     * <td>rnd(2010-1-1 - 2012-12-31)</td>
     * <td>rnd(1 - 20)</td>
     * <td>sellamount * 1000 + rnd.int(0-10000)</td>
     * </tr>
     * </table>
     * 
     * @param fs
     * @param path
     * @return hive load sql
     * @throws Exception
     */
    public static String createSellData(FileSystem fs, Path path) throws Exception {
        final long start = 1264994850410L; // 2010-1-1
        final int diff = 1000 * 86410;

        HdfsFileUtil.genHdfsFile(fs, new FileGenerator("\t") {

            @Override
            public void gen() throws IOException {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                int amount = 0;
                Date date = new Date(start);
                for (int x = 1; x < 20000; x++) {
                    long curTime = start;
                    for (int y = 1; y < 60; y++) {
                        // data
                        date.setTime(curTime);
                        amount = 1 + rnd.nextInt(20);
                        addFormatLine(String.valueOf(x), String.valueOf(format.format(date)), String.valueOf(amount),
                                      String.valueOf(amount * 1000 + rnd.nextInt(10000)));
                        curTime += diff;
                    }
                }
            }
        }, path, true, true);
        return null;
    }

    public static String createSellData(FileSystem fs) throws Exception {
        return createSellData(fs, TABLE_SELL_PATH);
    }

    public static String getDropDataBaseHQL() {
        return "drop database tmp cascade";
    }

    public static String getCreateDataBaseHQL() {
        return "create database if not exists tmp;";
    }

    public static String loadSellHQL() {
        return "load data inpath '/tmp/table-sell.txt' into table tmp.sell";
    }

    public static String loadRoleHQL() {
        return "load data inpath '/tmp/table-role.txt' into table tmp.role";
    }

    public static String loadUserHQL() {
        return "load data inpath '/tmp/table-user.txt' into table tmp.user";
    }

    public static String getCreateUserHQL() {
        return "create table if not exists tmp.user(userid int, sex string, name string, age int, roleid int) PARTITIONED by (dt string) row format delimited fields terminated by '\t' stored as textfile;";
    }

    public static String getCreateSellHQL() {
        return "create table if not exists tmp.sell (userid  int, selldate string, sellamount int,  sellmoney int) PARTITIONED by (dt string) row format delimited fields terminated by '\t' stored as textfile;";
    }

    public static String getCreateRoleHQL() {
        return "create table if not exists tmp.role (roleid int, rolename string, rolepecent double) PARTITIONED by (dt string) row format delimited fields terminated by '\t' stored as textfile;";
    }

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        HadoopBase.loadResourceA(conf);
        FileSystem fs = FileSystem.get(conf);
        HiveTableCreate_User_Role_Sell.createSellData(fs);
        HiveTableCreate_User_Role_Sell.createUserData(fs);
        HiveTableCreate_User_Role_Sell.createRuleData(fs);
    }
}
