package org.sourceopen.analyze.hadoop.hive.mapr.simple;

import org.junit.BeforeClass;
import org.junit.Test;
import org.sourceopen.base.HiveBase;

/**
 * 类AnalyzeSQL2MapR.java的实现描述：TODO 类实现描述
 * 
 * @author zalot.zhaoh Sep 5, 2012 2:03:01 PM
 */
public class AnalyzeSQL2MapR extends HiveBase {

    protected static String key = "h0";

    @BeforeClass
    public static void init() throws Exception {
        startHadoopClusterA(3, 3);
        startHive(key, "jdbc:mysql://10.20.153.121:3306/hive?useUnicode=true&amp;characterEncoding=utf-8", HIVE_METADB.MYSQL,
              "root", "123456", _confA);
    }

    @Test
    public void testSqlLocal1() throws Exception {
        runLocal("use zhaoheng; select * from test2 join test1 on (test1.name=test2.name ) where test1.name=1;", key);
    }

    @Test
    public void testSqlLocal2() throws Exception {
        runLocal("use zhaoheng; select * from (select * from test2 where name=1) t join test1 on (t.name=test1.name) where test1.name=1;",
                 key);
    }

    @Test
    public void testSqlOnline() throws Exception {
        run("use zhaoheng; select * from test2 join test1 on (test1.name=test2.name ) where test1.name=99;", key);
    }
}
