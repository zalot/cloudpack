package org.sourceopen.analyze.hadoop.hbase.client;

import java.io.IOException;

import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sourceopen.base.HBaseBase;

public class AnalyzeClient extends HBaseBase {

    private static String[] Tables  = new String[] { "tableA", "tableB", "tableC", "tableD", "tableE", "tableF" };
    private static String[] Familys = new String[] { "colA", "colB" };

    @BeforeClass
    public static void init() throws Exception {
        startHBaseClusterA(3, 3);
    }

    @Test
    public void testClient() throws IOException {
        HBaseAdmin admin = new HBaseAdmin(_confA);
        HTableDescriptor htable = new HTableDescriptor("tableA".getBytes());
        HColumnDescriptor col = new HColumnDescriptor("colA".getBytes());
        htable.addFamily(col);
        admin.createTable(htable);
        
        Put put = new Put("rowKey".getBytes());
        put.add("colA".getBytes(), "name".getBytes(), "zhaoheng".getBytes());
        _poolA.getTable("tableA").put(put);

        Get get = new Get("rowKey".getBytes());
        Result rs = _poolA.getTable("tableA").get(get);
        System.out.println(rs);
    }
}
