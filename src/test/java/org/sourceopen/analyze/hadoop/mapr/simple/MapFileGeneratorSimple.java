package org.sourceopen.analyze.hadoop.mapr.simple;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.Iterator;
import java.util.Random;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.sourceopen.base.HadoopBase;
import org.sourceopen.hadoop.hdfs.io.utils.HdfsFileUtil;
import org.sourceopen.hadoop.hdfs.io.utils.HdfsFileUtil.FileGenerator;

public class MapFileGeneratorSimple extends Configured implements Tool {

    final static String nnhost = "hdfs://h0:9100/";
    final static String jthost = "h0:54311";

    String              out;

    public MapFileGeneratorSimple(String out){
        this.out = out;
    }

    static class FileGMapper implements Mapper<LongWritable, Text, WritableComparable<?>, Text> {

        protected static final Text t = new Text();

        @Override
        public void configure(JobConf job) {

        }

        @Override
        public void close() throws IOException {

        }

        @Override
        public void map(LongWritable key, Text value, OutputCollector<WritableComparable<?>, Text> output,
                        Reporter reporter) throws IOException {
            // t.set(key);
            output.collect(key, value);
        }

    }

    static class FileGReducer implements Reducer<LongWritable, Text, WritableComparable<?>, Text> {

        protected static final Text t = new Text();

        @Override
        public void configure(JobConf job) {

        }

        @Override
        public void close() throws IOException {

        }

        @Override
        public void reduce(LongWritable key, Iterator<Text> values,
                           OutputCollector<WritableComparable<?>, Text> output, Reporter reporter) throws IOException {
            // TODO Auto-generated method stub
            output.collect(key, null);
            // System.out.println(key);
        }

    }

    public JobConf setup() throws Exception {
        Path inputPath = new Path("/tmp", "inputyunti");
        Path outputPath = new Path(out);
        Configuration conf = new Configuration();
        HadoopBase.loadResourceA(conf);
        FileSystem fs = FileSystem.get(conf);
        JobConf job = new JobConf(MapFileGeneratorSimple.class);
//        job.set("fs.default.name", nnhost);
//        job.set("mapred.job.tracker", jthost);
        HadoopBase.loadResourceA(job);
        System.out.println(job.get("fs.default.name"));
        job.set("io.compression.codecs",
                "org.apache.hadoop.io.compress.DefaultCodec,org.apache.hadoop.io.compress.GzipCodec,org.apache.hadoop.io.compress.BZip2Codec");

        job.setMapperClass(FileGMapper.class);
        job.setReducerClass(FileGReducer.class);
        FileInputFormat.setInputPaths(job, inputPath);
        FileOutputFormat.setOutputPath(job, outputPath);
        DistributedCache.addLocalFiles(conf,
                                       MapFileGeneratorSimple.class.getResource("MapFileGeneratorSimple.class").toString());
        DistributedCache.addArchiveToClassPath(new Path("/tmp/MapFileGeneratorSimple.class"), conf, fs);
        // DistributedCache.addCacheFile(new
        // URI(MapFileGeneratorSimple.class.getResource("MapFileGeneratorSimple.class").toString() + "#MapF"), job);
        // DistributedCache.addArchiveToClassPath(archive, conf)

        // DistributedCache.
         job.setNumReduceTasks(2);
        job.setNumMapTasks(3);
        // job.setInt("mapred.task.timeout", 0);
        return job;
    }

    @Override
    public int run(String[] args) throws Exception {
        JobClient.runJob(setup()).waitForCompletion();
        return 0;
    }

    final static Random rnd = new Random();

    public static void main(String[] args) throws Exception {
        Path inputPath = new Path("/tmp", "inputyunti");
        Configuration conf = new Configuration();
        conf.addResource(new Path(HadoopBase._confAPath));
        System.out.println(conf.get("fs.default.name"));
        FileSystem fs = FileSystem.get(conf);
        fs.delete(new Path("/tmp/group"), true);
        fs.delete(new Path("/work/workspace/sources/cloudpack/target/test-data/21648d87-f707-4c5a-b1f9-75f7b68d1230/hadoop-tmp-dir/mapred/staging/root/.staging"), true);
        fs.copyFromLocalFile(new Path(MapFileGeneratorSimple.class.getResource("MapFileGeneratorSimple$FileGMapper.class").toString()), new Path("/tmp/MapFileGeneratorSimple.class"));
        //
         FSDataOutputStream outS = fs.create(inputPath);
         BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outS));
        
         for (int x = 0; x < 40000; x++) {
         writer.write(rnd.nextLong() + "\r\n");
         }
         writer.close();
//         HdfsFileUtil.genHdfsFile(new FileGenerator(40960) {
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

        // fs.delete(new Path("/home/zhaoheng/hadoop/tmp/mapred/staging"), true);
        for (String out : outs)
            ToolRunner.run(new MapFileGeneratorSimple("/tmp" + out), args);
    }
}
