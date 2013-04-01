package org.sourceopen.analyze.hadoop.mapr.simple;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.util.ToolRunner;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sourceopen.base.HBaseBase;
import org.sourceopen.hadoop.hdfs.io.utils.HdfsFileUtil;
import org.sourceopen.hadoop.hdfs.io.utils.HdfsFileUtil.FileGenerator;

public class TestSimple extends HBaseBase {

    @BeforeClass
    public static void init() throws Exception {
        startHBaseClusterA(3, 3, 3);
    }

    /**
     * @throws Exception
     */
    /**
     * @throws Exception
     */
    @Test
    public void testA() throws Exception {
        String host = _utilA.getConfiguration().get("fs.default.name");
        String indir = host + "/in";
        String outdir = host + "/out";
        String inf = indir + "/test1.txt";
        String inf2 = indir + "/test2.txt";
        FileGenerator rndFileG = new FileGenerator(",") {

            @Override
            public void gen() throws IOException {
                for (int x = 0; x < 100; x++) {
                    addFormatLine(String.valueOf(rnd.nextInt(2000)), String.valueOf(rnd.nextInt(2000)),
                                  String.valueOf(rnd.nextInt(2000)));
                }
            }
        };
//        HdfsFileUtil.genHdfsFile(rndFileG, inf);
//        HdfsFileUtil.genHdfsFile(rndFileG, inf2);

        long start = System.currentTimeMillis();
        SimpleDateFormat format = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        String date = format.format(new Date(System.currentTimeMillis()));

        /*
         * use LocalJobSubmit
         */
        boolean useLocalJob = false;
        if (!useLocalJob) SimpleSortAndGroupExample.conf = _utilA.getConfiguration();

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

    @Test
    public void testB() {
        // FileSystem fs = FileSystem.get(_confA);
        //
        // FSDataOutputStream outS = fs.create(inputPath);
        // BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outS));
        //
        // for (int x = 0; x < 40000; x++) {
        // writer.write(rnd.nextLong() + "\r\n");
        // }
        // writer.close();
        // HdfsFileUtil.genHdfsFile(new FileGenerator(40960) {
        //
        // @Override
        // public void gen() {
        // for (int x = 0; x < 5000000; x++)
        // addline(rnd.nextLong() + "");
        // }
        //
        // }, host + "/" + inputPath.toString());

        String[] outs = new String[] {
                "/group/dts/hive/hs_s_vs_ad_guiding_log/pt=20121120000000/pagetype=jsclick/urltype=ipv/usertype=mid/",
                "/group/dts/hive/hs_s_vs_ad_guiding_log/pt=20121120000000/pagetype=jsclick/urltype=ipv/usertype=uid/",
                "/group/dts/hive/hs_s_vs_ad_guiding_log/pt=20121120000000/pagetype=jsclick/urltype=other/usertype=mid/",
                "/group/dts/hive/hs_s_vs_ad_guiding_log/pt=20121120000000/pagetype=jsclick/urltype=other/usertype=uid/",
                "/group/dts/hive/hs_s_vs_ad_guiding_log/pt=20121120000000/pagetype=normal/urltype=other/usertype=uid/",
                "/group/dts/hive/hs_s_vs_ad_guiding_log/pt=20121120000000/pagetype=normal/urltype=search/usertype=mid/" };

        // fs.delete(new Path("/tmp/group"), true);
        // for (String out : outs)
        // ToolRunner.run(new MapFileGeneratorSimple("/tmp" + out), args);
    }
}
