package org.sourceopen.analyze.hadoop.hdfs;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.junit.Test;

public class AnalyzeFileSystem {

    @Test
    public void testFileSystem() throws IOException {

        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(conf);
    }
}
