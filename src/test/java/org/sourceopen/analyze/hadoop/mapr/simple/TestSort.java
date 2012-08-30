package org.sourceopen.analyze.hadoop.mapr.simple;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.util.ToolRunner;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sourceopen.analyze.hadoop.hbase.TestHBase;
import org.sourceopen.hadoop.hdfs.io.utils.HdfsFileUtil;
import org.sourceopen.hadoop.hdfs.io.utils.HdfsFileUtil.FileGenerator;

public class TestSort extends TestHBase {

    @BeforeClass
    public static void init() throws Exception {
        startHBaseClusterA(3, 3, 3);
    }

    @Test
    public void testA() throws Exception {
        String host = _utilA.getConfiguration().get("fs.default.name");
        String indir = host + "/in";
        String outdir = host + "/out";
        String inf = indir + "/test1.txt";
        HdfsFileUtil.genHdfsFile(new FileGenerator(1024, ',') {

            @Override
            public void gen() {
                for (int x = 0; x < 100; x++) {
                    addFormat(rnd.nextInt(2000), rnd.nextInt(2000), rnd.nextInt(2000));
                    endLine();
                }
            }
        }, inf);

        long start = System.currentTimeMillis();
        SimpleDateFormat format = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        String date = format.format(new Date(System.currentTimeMillis()));
        // SimpleSortAndGroupExample.conf = _utilA.getConfiguration();
        String[] conf = new String[] { indir, outdir + "/" + date + "/" };
        long end = System.currentTimeMillis();
        System.out.println(ToolRunner.run(new SimpleSortAndGroupExample(), conf));
        FileStatus[] fss = _utilA.getDFSCluster().getFileSystem().listStatus(new Path(outdir + "/" + date + "/"));
        for (FileStatus fs : fss) {
            if (!fs.isDir()) {
                FSDataInputStream input = null;
                try {
                    input = _utilA.getDFSCluster().getFileSystem().open(fs.getPath());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                } catch (Exception e) {

                } finally {
                    if (input != null) {
                        input.close();
                    }
                    input = null;
                }
            }
        }
    }
}
