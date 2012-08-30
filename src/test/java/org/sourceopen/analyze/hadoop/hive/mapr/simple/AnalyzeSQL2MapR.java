package org.sourceopen.analyze.hadoop.hive.mapr.simple;

import org.junit.BeforeClass;
import org.junit.Test;
import org.sourceopen.analyze.hadoop.hive.TestHive;

public class AnalyzeSQL2MapR extends TestHive {

    @BeforeClass
    public static void init() throws Exception {
        start("jdbc:mysql://10.20.153.121:3306/hive?useUnicode=true&amp;characterEncoding=utf-8", HIVE.MYSQL, "root",
              "123456");
    }

    @Test
    public void testSql() throws Exception {
        runLocal("use zhaoheng; select * from test2 join test1 on (test1.name=test2.name ) where test1.name=99;");
    }
}
