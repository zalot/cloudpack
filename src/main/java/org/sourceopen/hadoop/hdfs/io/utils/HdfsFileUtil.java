package org.sourceopen.hadoop.hdfs.io.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Logger;

import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.io.IOUtils;

/**
 * Hadoop HDFS File Util
 * 
 * @author zalot
 */
public class HdfsFileUtil {

    protected final static Logger log = Logger.getLogger(HdfsFileUtil.class.getName());

    public static abstract class FileGenerator {

        protected static final String DEFAULT_FORMAT_STRING = "\t";
        protected static final char   DEFAULT_FORMAT_CHAR   = '\t';
        protected static final String DEFAULT_END_STRING    = "\r\n";
        private String                formatString          = DEFAULT_FORMAT_STRING;
        private FsPermission          fsPer                 = FsPermission.getDefault();
        private Writer                writer;

        /**
         * generator
         */
        public abstract void gen() throws IOException;

        public void setWriter(Writer writer) {
            this.writer = writer;
        }

        /**
         * @param format String example "<font color='red'>\t</font>" or "<font color='red'>,</font>"
         */
        public FileGenerator(String format){
            this(format, FsPermission.getDefault());
        }

        public FileGenerator(String format, FsPermission fs){
            this(null, format, fs);
        }

        public FileGenerator(Writer w, String format, FsPermission fs){
            this.formatString = format;
            this.fsPer = fs;
        }

        public void addline(String string) throws IOException {
            writer.append(string).append(DEFAULT_END_STRING);
            endLine();
        }

        public void addFormatLine(String... strings) throws IOException {
            for (String string : strings) {
                writer.append(string).append(formatString);
            }
            endLine();
        }

        private void endLine() throws IOException {
            writer.append(DEFAULT_END_STRING);
        }
    }

    private static String genRandomFilePath() {
        String uuid = UUID.randomUUID().toString();
        Properties props = System.getProperties();
        String userhome = (String) props.get("user.home");
        String tempFilePath = userhome + "/" + uuid;
        return tempFilePath;
    }

    private static File createFile(String fileName) {
        File file = new File(fileName);
        try {
            file.createNewFile();
            return file;
        } catch (IOException e) {
            log.info(e.getMessage());
        }
        return null;
    }

    private static boolean write2File(FileGenerator gen, File file) {
        FileWriter w = null;
        try {
            w = new FileWriter(file);
            gen.setWriter(w);
            gen.gen();
            return true;
        } catch (IOException e) {
            return false;
        } finally {
            if (w != null) try {
                w.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            w = null;
        }

    }

    /**
     * @param gen generator HDFS file example<font color='red'> <BR>
     * HdfsFileUtil.genHdfsFile(new FileGenerator(4096){ <BR>
     * public void gen() { <BR>
     * Random random = new Random(); <BR>
     * for(int x=0; x<1000; x++){ <BR>
     * addFormat(random.nextInt(1000) , // 1 ID <BR>
     * random.nextInt(999999), // 2 ID2 <BR>
     * random.nextInt(999999)); // 3 ID3 <BR>
     * endLine(); <BR>
     * <BR>
     * <BR>
     * ,"/test/") <BR>
     * </font>
     * @param path
     * @param overwrite
     * @throws Exception
     */
    public static void genHdfsFile(FileGenerator gen, Path path, boolean overwrite) throws Exception {
        genHdfsFile(gen, path, overwrite, false);
    }

    public static void genHdfsFile(FileSystem fs, FileGenerator gen, Path path, boolean overwrite) throws Exception {
        genHdfsFile(fs, gen, path, overwrite, false);
    }

    public static void genHdfsFile(FileGenerator gen, Path path, boolean overwrite, boolean fromlocal) throws Exception {
        genHdfsFile(null, gen, path, overwrite, fromlocal);
    }

    public static void genHdfsFile(FileSystem fs, FileGenerator gen, Path path, boolean overwrite, boolean fromlocal)
                                                                                                                     throws Exception {
        if (!overwrite && fs.exists(path)) {
            return;
        }

        if (fromlocal) {
            File tmpFile = genLocalFile(gen, null, false);
            try {
                if (tmpFile != null) {
                    file2Hdfs(fs, tmpFile, path);
                }
            } finally {
                if (tmpFile != null) {
                    while (!tmpFile.delete()) {
                        Thread.sleep(200);
                    }
                }
            }
        } else {
            FSDataOutputStream output = fs.create(path);
            BufferedWriter w = new BufferedWriter(new OutputStreamWriter(output));
            gen.setWriter(w);
            gen.gen();
            fs.setPermission(path, gen.fsPer);
        }
    }

    public static File genLocalFile(FileGenerator gen, String path, boolean overwrite) throws Exception {
        File tmpFile = null;
        if (path != null && path.trim().length() > 0) {
            tmpFile = createFile(path);
        } else {
            tmpFile = createFile(genRandomFilePath());
        }

        if (write2File(gen, tmpFile)) {
            return tmpFile;
        }
        return null;
    }

    public static boolean file2Hdfs(FileSystem fs, File file, Path path) {
        FSDataOutputStream output = null;
        FileInputStream input = null;
        try {
            output = fs.create(path);
            input = new FileInputStream(file);
            IOUtils.copyBytes(input, output, fs.getConf());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            IOUtils.closeStream(input);
            IOUtils.closeStream(output);
        }
    }

    private static boolean exist(FileSystem fs, String path) throws IOException {
        return fs.exists(new Path(path));
    }
}
