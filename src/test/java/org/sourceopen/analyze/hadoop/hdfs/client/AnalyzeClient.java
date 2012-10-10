package org.sourceopen.analyze.hadoop.hdfs.client;

import java.io.IOException;

import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sourceopen.analyze.hadoop.TestBase1;

public class AnalyzeClient extends TestBase1 {

    @BeforeClass
    public static void init() throws Exception {
        startHBaseClusterA(3, 3, 3);
    }

    @Test
    public void testCreate() throws IOException {
        FileSystem fs = FileSystem.get(_confA);
        FSDataOutputStream fsout = fs.create(new Path("/test.txt"));
        fsout.write("AA".getBytes());
        fsout.write("BB".getBytes());
        fsout.write("CC".getBytes());
        fsout.close();
        System.out.println("----over----");
    }

    @Test
    public void testAppend() throws IOException {
        FileSystem fs = FileSystem.get(_confA);
        FSDataOutputStream fsout = fs.append(new Path("/test.txt"));
        fsout.write("DD".getBytes());
        fsout.write("EE".getBytes());
        fsout.close();
        System.out.println("----over----");
    }
}
