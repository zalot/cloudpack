package org.sourceopen.hadoop.hdfs.io.utils;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;

public class SequenceFileWriter {

    public static void main(String[] args) throws IOException {
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(conf);
        SequenceFile.Writer sfw = new SequenceFile.Writer(fs, conf, new Path("/tmp/test.txt"), Text.class, Text.class);
        Text A = new Text();
        Text B = new Text();
        for (int x = 0; x < 1000; x++) {
            A.set("a" + x);
            B.set("b" + x);
            sfw.append(A, B);
        }
        org.apache.hadoop.io.IOUtils.closeStream(sfw);
    }
}
