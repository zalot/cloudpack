package org.sourceopen.analyze.hadoop.io;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.SequenceFile.CompressionType;
import org.apache.hadoop.io.Text;
import org.sourceopen.base.HadoopBase;

public class AnalyzeSequenceFile extends HadoopBase {

    public static void main(String[] args) throws IOException {
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.getLocal(conf);
        Path path = new Path(System.getProperty("user.dir") + "/target/analyze/io/sequencefile/record.seq");
        SequenceFile.Writer writer = SequenceFile.createWriter(fs, conf, path, LongWritable.class, Text.class,
                                                               CompressionType.BLOCK);
        writer.append(new LongWritable(100), new Text("zhaoheng"));
        writer.append(new LongWritable(200), new Text("zhaoheng1"));
        writer.append(new LongWritable(300), new Text("zhaoheng2"));
        writer.close();

        SequenceFile.Reader reader = new SequenceFile.Reader(fs, path, conf);
        LongWritable key = new LongWritable();
        Text val = new Text();
        while (reader.next(key, val)) {
            System.out.println(key + "=" + val);
        }
    }
}
