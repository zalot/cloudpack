package org.sourceopen.analyze.hadoop.hive.datagenerator;

import java.io.IOException;
import java.security.SecureRandom;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.sourceopen.analyze.CommonUtils;
import org.sourceopen.hadoop.hdfs.io.utils.HdfsFileUtil;
import org.sourceopen.hadoop.hdfs.io.utils.HdfsFileUtil.FileGenerator;

public class HiveTableCreate_Key_Value {

    static SecureRandom rnd = new SecureRandom();

    public static String createKeyValueTableData(FileSystem fs, Path path) throws Exception {
        HdfsFileUtil.genHdfsFile(fs, new FileGenerator("\t") {

            @Override
            public void gen() throws IOException {
                for (int x = 1; x < 1000; x++)
                    // seq_id , key , value
                    for (int y = 1; y < 10; y++)
                        addFormatLine(String.valueOf(x), "key" + y, String.valueOf(rnd.nextInt(90)));
            }
        }, path, true, true);
        return null;
    }

    public static void main(String[] args) throws Exception {
        FileSystem fs = FileSystem.get(CommonUtils.getH0ClusterConfiguration());
        createKeyValueTableData(fs, new Path("/tmp/tmpKeyValue.txt"));
    }
}
