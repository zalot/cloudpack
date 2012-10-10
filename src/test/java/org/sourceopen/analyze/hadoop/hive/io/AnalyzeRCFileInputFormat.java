package org.sourceopen.analyze.hadoop.hive.io;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.ql.io.RCFile;
import org.apache.hadoop.hive.ql.io.RCFile.Writer;
import org.apache.hadoop.hive.serde2.columnar.BytesRefArrayWritable;

public class AnalyzeRCFileInputFormat {

    public static class TestWriteAble extends BytesRefArrayWritable {

        protected String v = "";

        public void setV(String v) {
            this.v = v;
        }

        @Override
        public void write(DataOutput out) throws IOException {
            out.writeInt(v.length());
            out.write(v.getBytes());
        }

        @Override
        public void readFields(DataInput in) throws IOException {
            int len = in.readInt();
            byte[] vb = new byte[len];
            in.readFully(vb);
            this.v = new String(vb);
        }

        public String getV() {
            return v;
        }
    }

    public static void main(String[] args) throws IOException {
        RCFile f = new RCFile();
        Configuration conf = new Configuration();
        conf.set(FileSystem.FS_DEFAULT_NAME_KEY, "/tmp/localdfs/");
        FileSystem fs = FileSystem.getLocal(conf);
        Writer w = new Writer(fs, conf, new Path("/test.txt"));
        // TestWriteAble wa = new TestWriteAble();
        BytesRefArrayWritable bf = new BytesRefArrayWritable();
        // wa.setV("test");
        w.append(bf);
    }
}
