package org.sourceopen.analyze.hadoop.mapr.simple;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;

/**
 * @author zalot
 */
public class SimpleSortAndGroupExample extends Configured implements Tool {

    public static Configuration conf;

    public static class SimplePair implements WritableComparable<SimplePair> {

        private long long1;
        private long long2;
        private long long3;

        public long getLong1() {
            return long1;
        }

        public long getLong2() {
            return long2;
        }

        public long getLong3() {
            return long3;
        }

        public void readFields(DataInput input) throws IOException {
            // 目的是为了直观
            long inputFirstLong = input.readLong();
            long inputSecondLong = input.readLong();
            long inputThirdLong = input.readLong();
            long1 = inputFirstLong;
            long2 = inputSecondLong;
            long3 = inputThirdLong;
        }

        public void write(DataOutput out) throws IOException {
            // 目的是为了直观
            long outputFirstLong = long1;
            long outputSecondLong = long2;
            long outputThirdLong = long3;
            out.writeLong(outputFirstLong);
            out.writeLong(outputSecondLong);
            out.writeLong(outputThirdLong);
        }

        // 默认排序规则
        // 如果不指定 job.setSorting
        public int compareTo(SimplePair o) {
            if (long1 != o.long1) {
                return long1 < o.long1 ? -1 : 1;
            } else if (long2 != o.long2) {
                return long2 < o.long2 ? -1 : 1;
            } else if (long3 != o.long3) {
                return long3 < o.long3 ? -1 : 1;
            } else return 0;
        }

        public void set(long long1, long long2, long long3) {
            this.long1 = long1;
            this.long2 = long2;
            this.long3 = long3;
        }
    }

    public static class SimpleMap extends Mapper<LongWritable, Text, SimplePair, SimplePair> {

        private final SimplePair param = new SimplePair();

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String line = value.toString();
            StringTokenizer tokenizer = new StringTokenizer(line, ",");
            if (tokenizer.hasMoreTokens()) {
                long long1 = Long.parseLong(tokenizer.nextToken());
                long long2 = Long.parseLong(tokenizer.nextToken());
                long long3 = Long.parseLong(tokenizer.nextToken());
                param.set(long1, long2, long3);
                context.write(param, param);
            }
        }

    }

    public static class SimpleReduce extends Reducer<SimplePair, SimplePair, Text, LongWritable> {

        private final Text keyParam = new Text();
        private final Text line     = new Text("-----------------");

        public void reduce(SimplePair key, Iterable<SimplePair> values, Context context) throws IOException,
                                                                                        InterruptedException {
            context.write(line, null);
            for (SimplePair val : values) {
                keyParam.set(val.long1 + "," + val.long2 + "," + val.long3);
                context.write(keyParam, null);
                System.out.println("------------");
            }
        }
    }

    /**
     * 分区函数类。根据first确定Partition。
     */
    public static class SimplePartitioner extends Partitioner<SimplePair, SimplePair> {

        @Override
        public int getPartition(SimplePair key, SimplePair value, int numPartitions) {
            int pat = (int) Math.abs(key.long1) % numPartitions;
            return pat;
        }
    }

    public static class SimpleGrouping extends WritableComparator {

        protected SimpleGrouping(){
            super(SimplePair.class, true);
        }

        @Override
        public int compare(WritableComparable w1, WritableComparable w2) {
            SimplePair ip1 = (SimplePair) w1;
            SimplePair ip2 = (SimplePair) w2;

            if (ip1.long1 != ip2.long1) {
                return ip1.long1 < ip2.long1 ? -1 : 1;
            }
            return 0;
        }
    }

    public static class SimpleSorting extends WritableComparator {

        protected SimpleSorting(){
            super(SimplePair.class, true);
        }

        @Override
        // Compare two WritableComparables.
        public int compare(WritableComparable w1, WritableComparable w2) {
            SimplePair ip1 = (SimplePair) w1;
            SimplePair ip2 = (SimplePair) w2;

            if (ip1.long2 != ip2.long2) {
                return ip1.long2 < ip2.long2 ? -1 : 1;
            } else if (ip1.long3 != ip2.long3) {
                return ip1.long3 < ip2.long3 ? -1 : 1;
            } else {
                return 0;
            }
        }
    }

    @Override
    public int run(String[] arg0) throws Exception {
        Job job;
        if (conf != null) {
            job = new Job(conf);
        } else {
            job = new Job();
        }
        job.setMapperClass(SimpleMap.class);
        job.setReducerClass(SimpleReduce.class);
        job.setMapOutputKeyClass(SimplePair.class);
        job.setMapOutputValueClass(SimplePair.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(LongWritable.class);
        // 文件分片会根据任务数来决定，所以任务数量决定分片的数量
        job.setNumReduceTasks(3);
        job.setPartitionerClass(SimplePartitioner.class);
        job.setGroupingComparatorClass(SimpleGrouping.class);
        // job.setSortComparatorClass(SimpleSorting.class);
        FileInputFormat.setInputPaths(job, new Path(arg0[0]));
        FileOutputFormat.setOutputPath(job, new Path(arg0[1]));

        // 设置用户名和密码 （如果需要）
        // this.getConf().set("hadoop.job.ugi", "luo,luo");
        // this.getConf().set("mapred.system.dir",
        // "/home/luo/hadoop/tmp/mapred/system/");
        job.getConfiguration().writeXml(new FileOutputStream(new File(System.currentTimeMillis() + "")));
        return job.waitForCompletion(true) ? 0 : 1;
    }
}
