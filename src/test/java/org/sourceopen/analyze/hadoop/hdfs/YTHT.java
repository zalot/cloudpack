package org.sourceopen.analyze.hadoop.hdfs;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.SequenceFile.CompressionType;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.CompressionCodecFactory;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.util.ReflectionUtils;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class YTHT implements Tool {

    public static final Log           LOG           = LogFactory.getLog(YTHT.class);
    private static final NumberFormat NUMBER_FORMAT = NumberFormat.getInstance();
    static {
        NUMBER_FORMAT.setGroupingUsed(false);
    }
    private final static String       MERGE_TEMP    = "_merge_tmp";
    private JobConf                   conf;

    public YTHT(JobConf conf){
        setConf(conf);
    }

    @Override
    public Configuration getConf() {
        return this.conf;
    }

    @Override
    public void setConf(Configuration conf) {
        if (conf instanceof JobConf) {
            this.conf = (JobConf) conf;
        } else {
            this.conf = new JobConf(conf);
        }
    }

    static class MergeFilesMapper implements Mapper<LongWritable, Text, WritableComparable<?>, Text> {

        private Path          root;
        private Path          bak;
        private FileSystem    fs;
        private Configuration conf;

        @Override
        public void configure(JobConf job) {
            root = new Path(job.get("ytht.root.dir"));
            bak = new Path(job.get("ytht.backup.dir"));
            LOG.info("root dir: " + root);
            LOG.info("backup dir: " + bak);

            conf = job;
            try {
                fs = FileSystem.get(job);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void map(LongWritable key, Text value, OutputCollector<WritableComparable<?>, Text> output,
                        Reporter reporter) throws IOException {
            String dir = value.toString();
            reporter.setStatus("merging dir: " + dir);
            LOG.info("Dir for merge: " + dir);
            mergeDir(root, new Path(dir), bak, fs, conf, reporter);
        }

        @Override
        public void close() throws IOException {

        }
    }

    private static CompressionType getCompressionType(SequenceFile.Reader in) {
        if (in.isCompressed()) if (in.isBlockCompressed()) return CompressionType.BLOCK;
        else return CompressionType.RECORD;
        else return CompressionType.NONE;
    }

    @SuppressWarnings("rawtypes")
    private static void copyKeyValue(SequenceFile.Reader in, SequenceFile.Writer out, Configuration conf)
                                                                                                         throws IOException {
        WritableComparable key = ReflectionUtils.newInstance(in.getKeyClass().asSubclass(WritableComparable.class),
                                                             conf);
        Writable val = ReflectionUtils.newInstance(in.getValueClass().asSubclass(Writable.class), conf);
        try {
            while (in.next(key, val)) {
                out.append(key, val);
            }
        } catch (IOException ee) {
            ee.printStackTrace();
            System.out.println("Got IOException when reading seq file. " + "Maybe EOFException, but continue...");
        }
    }

    private static void mergeSequenceFile(FileSystem srcFS, Path srcDir, FileSystem dstFS, Path dstFile,
                                          Configuration conf, Reporter reporter) throws IOException {
        FileStatus contents[] = srcFS.listStatus(srcDir);
        int len = contents.length;
        SequenceFile.Reader in = null;
        for (int i = 0; i < contents.length; i++) {
            if (!contents[i].isDir()) {
                in = new SequenceFile.Reader(srcFS, contents[i].getPath(), conf);
                break;
            }
        }

        if (in == null) return;

        SequenceFile.Writer out = SequenceFile.createWriter(dstFS, conf, dstFile, in.getKeyClass(), in.getValueClass(),
                                                            getCompressionType(in), in.getCompressionCodec());

        try {
            for (int i = 0; i < contents.length; i++) {
                if (!contents[i].isDir() && contents[i].getLen() > 0) {
                    in = new SequenceFile.Reader(srcFS, contents[i].getPath(), conf);
                    try {
                        reporter.setStatus("merging dir: " + srcDir + " (" + (i + 1) + "/" + len + ")");
                        copyKeyValue(in, out, conf);
                    } finally {
                        in.close();
                    }
                }
            }
        } finally {
            out.close();
        }
    }

    private static void mergeCompressedFile(FileSystem srcFS, Path srcDir, FileSystem dstFS, Path dstFile,
                                            Configuration conf, CompressionCodec codec, Reporter reporter)
                                                                                                          throws IOException {
        OutputStream out = codec.createOutputStream(dstFS.create(dstFile));

        long maxSize = conf.getLong("ytht.compressed.file.maxsize", -1);
        long totalSize = 0L;

        try {
            FileStatus contents[] = srcFS.listStatus(srcDir);
            int totalFiles = contents.length;
            for (int curFile = 0; curFile < contents.length; curFile++) {
                if (!contents[curFile].isDir() && contents[curFile].getLen() > 0) {
                    long fileSize = contents[curFile].getLen();
                    totalSize += fileSize;
                    if (maxSize != -1 && totalSize > maxSize && fileSize != totalSize) {
                        // generate new file
                        out.close();

                        totalSize = fileSize;
                        dstFile = getNextFileName(dstFile);
                        out = codec.createOutputStream(dstFS.create(dstFile));
                    }

                    InputStream in = codec.createInputStream(srcFS.open(contents[curFile].getPath()));
                    try {
                        reporter.setStatus("merging dir: " + srcDir + " (" + (curFile + 1) + "/" + totalFiles + ")");
                        IOUtils.copyBytes(in, out, conf, false);
                    } finally {
                        in.close();
                    }
                }
            }
        } finally {
            out.close();
        }
    }

    static Path getNextFileName(Path dstFile) {
        String prev = dstFile.getName();
        String next = null;

        Pattern p1 = Pattern.compile("part-(\\d{5,6})" + // part-xxx
                                     "(\\.(deflate|gz|bz2))?"); // suffix
        Pattern p2 = Pattern.compile("attempt_\\d{12}_\\d{4,7}_[rm]_(\\d{6})_\\d" + // attempt_xxx
                                     "(\\.(deflate|gz|bz2))?"); // suffix
        Matcher m1 = p1.matcher(prev);
        Matcher m2 = p2.matcher(prev);
        if (m1.find()) {
            String num = m1.group(1);
            int n = Integer.parseInt(num) + 1;
            NUMBER_FORMAT.setMinimumIntegerDigits(num.length());
            next = prev.replace("part-" + num, "part-" + NUMBER_FORMAT.format(n));
        } else if (m2.find()) {
            String num = m2.group(1);
            int n = Integer.parseInt(num) + 1;
            NUMBER_FORMAT.setMinimumIntegerDigits(num.length());
            next = prev.replace("_" + num, "_" + NUMBER_FORMAT.format(n));
        }
        return new Path(dstFile.getParent(), next);
    }

    static boolean validFileName(FileStatus path, String custom_p) {
        if (path.isDir()) return false;
        // file pattern
        // part-00000
        // part-00000.deflate
        // attempt_201003291206_202753_r_000159_0
        // attempt_201003291206_311567_r_000128_0.deflate
        String part = "(part-\\d{5,6})";
        String attempt = "(attempt_\\d{12}_\\d{4,7}_[rm]_\\d{6}_\\d)";
        String suffix = "(\\.(deflate|gz|bz2))?";
        String default_p = "(" + part + "|" + attempt + ")" + suffix;

        Pattern filePattern;
        if (custom_p != null && !custom_p.equals("")) filePattern = Pattern.compile("(" + default_p + ")|(" + custom_p
                                                                                    + ")");
        else filePattern = Pattern.compile(default_p);
        return filePattern.matcher(path.getPath().getName()).matches();
    }

    private static void mergeNormalFile(FileSystem srcFS, Path srcDir, FileSystem dstFS, Path dstFile,
                                        Configuration conf, Reporter reporter) throws IOException {
        OutputStream out = dstFS.create(dstFile);

        try {
            FileStatus contents[] = srcFS.listStatus(srcDir);
            int len = contents.length;
            for (int i = 0; i < contents.length; i++) {
                if (!contents[i].isDir()) {
                    InputStream in = srcFS.open(contents[i].getPath());
                    try {
                        reporter.setStatus("merging dir: " + srcDir + " (" + (i + 1) + "/" + len + ")");
                        IOUtils.copyBytes(in, out, conf, false);
                    } finally {
                        in.close();
                    }
                }
            }
        } finally {
            out.close();
        }
    }

    /*
     * input path must be subdir of root root: /to/merge/root input: /to/merge/root/dir1/dir2 backup: /to/backup 1.
     * merge dir input to file: /to/backup/_merge_tmp/dir1/dir2/part-00000 2. rename dir /to/merge/root/dir1/dir2 to
     * /to/backup/dir1/dir2 3. rename dir /to/backup/_merge_tmp/dir1/dir2 to /to/merge/root/dir1/dir2
     */
    private static void mergeDir(Path root, Path input, Path backupRoot, FileSystem fs, Configuration conf,
                                 Reporter reporter) throws IOException {
        Path output = null;
        Path backupPath = null;

        // because the input is actually a file, get its parent as input.
        // it's just a trick.
        String testFileName = input.getName();
        input = input.getParent();

        if (!needMerge(input, fs, conf, null, null)) {
            return;
        }

        if (input.equals(root)) {
            output = new Path(new Path(backupRoot, MERGE_TEMP), input.getName());
            backupPath = new Path(backupRoot, input.getName());
        } else {
            String relative = input.toString().substring(root.toString().length() + 1);
            Path relPath = new Path(relative);
            output = new Path(new Path(backupRoot, MERGE_TEMP), relPath);
            backupPath = new Path(backupRoot, relPath);
        }

        // check the test file (eg. input/part-00000) and determine file type
        // seqfile or text file or deflate file
        Path testFile = new Path(input, testFileName);
        FSDataInputStream i = null;
        try {
            i = fs.open(testFile);
        } catch (IOException e) {
            // file or directory removed, skip this dir
            reporter.getCounter("YTHT", "dir skipped").increment(1);
            return;
        }

        boolean isSeqFile = false;
        try {
            if (i.readByte() == 'S' && i.readByte() == 'E' && i.readByte() == 'Q') {
                isSeqFile = true;
            }
        } catch (EOFException e) {
            isSeqFile = false;
        }

        try {
            if (isSeqFile) {
                LOG.info("Merging sequence file.");
                mergeSequenceFile(fs, input, fs, new Path(output, testFileName), conf, reporter);
            } else {
                CompressionCodecFactory compressionCodecs = new CompressionCodecFactory(conf);
                final CompressionCodec codec = compressionCodecs.getCodec(testFile);
                if (codec == null) {
                    LOG.info("Merging text file.");
                    mergeNormalFile(fs, input, fs, new Path(output, testFileName), conf, reporter);
                } else {
                    LOG.info("Merging compressed file.");
                    mergeCompressedFile(fs, input, fs, new Path(output, testFileName), conf, codec, reporter);
                }
            }
        } catch (Exception e) {
            // Maybe file to be write in _merge_tmp is created by other attempt.
            // Catch the execption and skip this directory
            LOG.info("Got exception when merge dir.");
            e.printStackTrace();
            reporter.getCounter("YTHT", "dir skipped").increment(1);
            return;
        }

        fs.mkdirs(backupPath.getParent());
        fs.rename(input, backupPath);
        LOG.info("Original directory " + input + " moved to path " + backupPath);
        fs.rename(output, input);
        LOG.info("Merged directory " + output + " moved to path " + input);
        reporter.getCounter("YTHT", "dir merged").increment(1);
    }

    /*
     * dir can contain files like: _logs, part-00000 part-00001 ... part-000xx parameter speedUpFiles is used to reduce
     * calls of listStatus
     */
    private static boolean needMerge(Path dir, FileSystem fs, Configuration conf, List<FileStatus> speedUpFiles,
                                     List<Path> oneFile) throws IOException {
        if (speedUpFiles != null) {
            speedUpFiles.clear();
        }

        long avgSize = conf.getLong("ytht.average.filesize", 64 * 1024 * 1024);

        FileStatus[] files = fs.listStatus(dir);
        if (files == null) {
            // dir deleted
            return false;
        }
        long totalSize = 0;
        int num = 0;
        String firstFile = null;
        for (FileStatus f : files) {
            String fileName = f.getPath().getName();
            if (fileName.equals("_logs")) {
                continue;
            }

            if (!validFileName(f, conf.get("ytht.file.pattern")) || f.isDir()) {
                // if not all files are part-000xx
                // use speed up file list
                if (speedUpFiles != null) {
                    speedUpFiles.addAll(Arrays.asList(files));
                }
                return false;
            } else {
                if (firstFile == null) {
                    firstFile = fileName;
                }
                num++;
                totalSize += f.getLen();
            }
        }
        if (num < 2 || (totalSize / num > avgSize)) // files too large, no need
                                                    // to merge
        return false;

        if (conf.get("ytht.dir.pattern") != null && !conf.get("ytht.dir.pattern").equals("")) {
            Pattern dir_p = Pattern.compile(conf.get("ytht.dir.pattern"));
            if (!dir_p.matcher(dir.getName()).matches()) // dir pattern not matches
            return false;
        }

        LOG.info("dir " + dir.toString() + " (" + num + ") need merge");
        // trick: set a file such as part-00000
        if (oneFile != null) {
            oneFile.clear();
            oneFile.add(new Path(dir, firstFile));
        }
        return true;
    }

    private void generateDirListForMerge(Path dir, FileSystem fs, JobConf conf, List<Path> toMerge) throws IOException {
        List<FileStatus> speedup = new LinkedList<FileStatus>();
        List<Path> oneFile = new ArrayList<Path>(1);

        if (needMerge(dir, fs, conf, speedup, oneFile)) {
            toMerge.add(oneFile.get(0));
        } else {
            if (!speedup.isEmpty()) {
                for (FileStatus f : speedup) {
                    if (f.isDir()) {
                        generateDirListForMerge(f.getPath(), fs, conf, toMerge);
                    }
                }
            }
        }
    }

    private void setup(JobConf conf, String targetPath, String backupPath) throws IOException {
        if (conf.get("ytht.file.pattern") != null && conf.get("ytht.compressed.file.maxsize") != null) {
            throw new IOException("ytht.compressed.file.maxsize and " + "file pattern cann't set together");
        }

        conf.setJobName("YTHT");

        FileSystem fs = FileSystem.get(conf);
        Path root = new Path(targetPath);
        root = root.makeQualified(fs);
        Path bak = new Path(backupPath);
        bak = bak.makeQualified(fs);

        if (!fs.getFileStatus(root).isDir()) {
            throw new IOException("Target path for YTHT is not a dir.");
        }
        if (fs.exists(bak)) {
            if (!fs.getFileStatus(bak).isDir()) {
                throw new IOException("Backup path for YTHT is not a dir.");
            }
        }
        fs.mkdirs(new Path(bak, MERGE_TEMP));

        List<Path> toMerge = new LinkedList<Path>();
        generateDirListForMerge(root, fs, conf, toMerge);

        int dirNum = toMerge.size();
        if (dirNum == 0) {
            LOG.info("No dir need to be merged. Exiting...");
        } else {
            // set up a job
            String randomId = Integer.toString(new Random().nextInt(Integer.MAX_VALUE), 36);

            Path jobDir = new Path(bak, "_ytht_files_" + randomId);
            Path input = new Path(jobDir, "to_merge");
            int num = conf.getNumMapTasks();

            for (int i = 0; i < num; i++) {
                PrintWriter out = new PrintWriter(fs.create(new Path(input, "input-" + i)));
                for (int j = i; j < dirNum; j += num) {
                    out.println(toMerge.get(j));
                }
                out.close();
            }

            Path output = new Path(jobDir, "result");
            FileInputFormat.setInputPaths(conf, input);
            FileOutputFormat.setOutputPath(conf, output);
            conf.set("ytht.root.dir", root.toString());
            conf.set("ytht.backup.dir", bak.toString());
            conf.set("ytht.job.dir", jobDir.toString());
            conf.setMapSpeculativeExecution(false);
            conf.setNumReduceTasks(0);
            conf.setMapperClass(MergeFilesMapper.class);
            conf.setInt("mapred.task.timeout", 0);
        }
    }

    private void cleanup(JobConf conf, String backupPath) throws IOException {
        FileSystem fs = FileSystem.get(conf);
        fs.delete(new Path(backupPath, MERGE_TEMP), true);
        String jobDir = conf.get("ytht.job.dir");
        if (jobDir != null) {
            fs.delete(new Path(jobDir), true);
        }
    }

    private List<String> parseArgument(String[] args) {
        List<String> other_args = new ArrayList<String>();
        for (int idx = 0; idx < args.length; idx++) {
            if ("-m".equals(args[idx])) {
                if (++idx == args.length) {
                    throw new IllegalArgumentException("map number not specified in -m");
                }
                try {
                    int maps = Integer.parseInt(args[idx]);
                    if (maps <= 0) {
                        throw new IllegalArgumentException("map number must be larger than 0");
                    }
                    conf.setNumMapTasks(maps);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Integer expected but got " + args[idx]);
                }
            } else if ("-p".equals(args[idx])) {
                if (++idx == args.length) {
                    throw new IllegalArgumentException("dir pattern for merge not specified in -p");
                }
                conf.set("ytht.dir.pattern", args[idx]);
            } else if ("-f".equals(args[idx])) {
                if (++idx == args.length) {
                    throw new IllegalArgumentException("file pattern for merge not specified in -f");
                }
                conf.set("ytht.file.pattern", args[idx]);
            } else {
                other_args.add(args[idx]);
            }
        }
        if (other_args.size() < 2) {
            throw new IllegalArgumentException("not enough arguments");
        }
        return other_args;
    }

    @Override
    public int run(String[] args) throws Exception {
        try {
            List<String> other_args = parseArgument(args);

            if (conf.getNumMapTasks() > 2) {
                LOG.info("Max running threads: " + conf.getNumMapTasks());
            }

            // TODO: if no backup dir is set, using /path/to/merge/../_ytht_backup
            // TODO: parameter settings
            setup(conf, other_args.get(0), other_args.get(1));
            if (conf.get("ytht.job.dir") != null) {
                JobClient.runJob(conf);
            }
            cleanup(conf, args[1]);
        } catch (IllegalArgumentException e) {
            printUsage();
            return -1;
        } catch (IOException e) {
            LOG.error("YTHT: get exception when merging files");
            e.printStackTrace();
            return -1;
        }

        return 0;
    }

    private void printUsage() {
        System.out.println("Usage: YTHT " + "[ -p dir_pattern ] [-f file_pattern] [ -m map_num ] " + "dir backup_dir ");
    }

    public static void main(String[] args) throws Exception {
        JobConf conf = new JobConf(YTHT.class);
        int res = ToolRunner.run(new YTHT(conf), args);
        System.exit(res);
    }
}
