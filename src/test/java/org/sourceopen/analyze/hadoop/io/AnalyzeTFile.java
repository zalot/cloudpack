package org.sourceopen.analyze.hadoop.io;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.file.tfile.TFile;
import org.apache.hadoop.io.file.tfile.TFile.Reader.Scanner;

public class AnalyzeTFile {

    public static void main(String[] args) throws IOException {
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.getLocal(conf);
        Path path = new Path(System.getProperty("user.dir") + "/target/analyze/io/tfile/tfile.tfile");
        FSDataOutputStream fsdos = fs.create(path);
        TFile.Writer writer = new TFile.Writer(fsdos, 1024, "none", null, conf);
        writer.append("test".getBytes(), "info".getBytes());
        writer.append("test1".getBytes(), "info1".getBytes());
        writer.append("test2".getBytes(), "info2".getBytes());
        writer.close();
        fsdos.flush();
        fsdos.close();
        
        
        long len = fs.getFileStatus(path).getLen();
        FSDataInputStream fsdis = fs.open(path);
        TFile.Reader reader = new TFile.Reader(fsdis, len, conf);
        Scanner scan = reader.createScanner();
        if(scan.seekTo("test1".getBytes())){
            System.out.println();
        }
        
    }
}
