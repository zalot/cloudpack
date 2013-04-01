package org.sourceopen.analyze.hadoop.io.tfile;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.io.file.tfile.TFile;

public class AnalyzeTFile {

    public void testTFileWriter() throws IOException {
        Configuration conf = new Configuration();
        
        FileSystem lfs = FileSystem.getLocal(conf);
//        
//        Path tfile = new Path(System.getProperty("user.dir"));
//        
//        TFile.Writer tfwriter = new Writer(fsdos, minBlockSize, compressName, comparator, conf)
    }
}
