package org.sourceopen.hadoop.hdfs.io.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Logger;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsAction;
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
        private StringBuffer          buf                   = new StringBuffer();
        private String                FORMAT_STRING         = DEFAULT_FORMAT_STRING;
        private char                  FORMAT_CHAR           = DEFAULT_FORMAT_CHAR;
        private int                   bufferSize            = 64 * 1024 * 1024;
        private FsPermission          fs;

        /**
         * generator
         */
        public abstract void gen();

        /**
         * @param bufferSize NIO bufferSize;
         */
        public FileGenerator(int bufferSize){
            this(bufferSize, null);
        }

        /**
         * @param format String example "<font color='red'>\t</font>" or "<font color='red'>,</font>"
         */
        public FileGenerator(String format){
            this(-1, format);
        }

        /**
         * @param bufferSize NIO bufferSize;
         * @param format String example "<font color='red'>\t</font>" or "<font color='red'>,</font>"
         */
        public FileGenerator(int bufferSize, String format){
            if (format != null && format.trim().length() > 0) this.FORMAT_STRING = format;
            if (bufferSize > 0) this.bufferSize = bufferSize;
        }

        public FileGenerator(int bufferSize, char format){
            this(bufferSize, format, new FsPermission(FsAction.ALL, FsAction.ALL, FsAction.ALL));
        }

        public FileGenerator(int bufferSize, char format, FsPermission fs){
            this.FORMAT_STRING = null;
            this.FORMAT_CHAR = format;
            if (bufferSize > 0) this.bufferSize = bufferSize;
            this.fs = fs;
        }

        private int bufferSize() {
            return this.bufferSize;
        }

        public void addline(String string) {
            buf.append(string).append(DEFAULT_END_STRING);
        }

        public void addFormat(String... strings) {
            for (String string : strings) {
                if (FORMAT_STRING != null) buf.append(string).append(FORMAT_STRING);
                else buf.append(string).append(FORMAT_CHAR);
            }
        }

        public void addFormat(long... longs) {
            String[] strings = new String[longs.length];
            for (int x = 0; x < strings.length; x++) {
                strings[x] = String.valueOf(longs[x]);
            }
            addFormat(strings);
        }

        public void endLine() {
            buf.append(DEFAULT_END_STRING);
        }

        private byte[] getBytesBuffer() {
            return buf.toString().getBytes();
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
        FileOutputStream output = null;
        try {
            output = new FileOutputStream(file);
            gen.gen();
        } catch (FileNotFoundException e) {
            return false;
        }
        FileChannel channel = output.getChannel();
        ByteBuffer buffer = ByteBuffer.allocate(gen.bufferSize());
        buffer.put(gen.getBytesBuffer());
        buffer.flip();

        try {
            channel.write(buffer);
            return true;
        } catch (IOException e) {
            return false;
        } finally {
            try {
                buffer.clear();
                channel.close();
                output.close();
            } catch (IOException e) {
            }
            buffer = null;
            channel = null;
            channel = null;
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
    public static void genHdfsFile(FileGenerator gen, String path, boolean overwrite) throws Exception {
        genHdfsFile(gen, path, overwrite, false);
    }

    public static void genHdfsFile(FileGenerator gen, String path, boolean overwrite, boolean fromlocal)
                                                                                                        throws Exception {
        FileSystem fs = getFileSystem(path);
        if (!overwrite && exist(fs, path)) {
            return;
        }

        if (fromlocal) {
            File tmpFile = genLocalFile(gen, null, false);
            try {
                if (tmpFile != null) {
                    file2Hdfs(tmpFile, path);
                }
            } finally {
                if (tmpFile != null) {
                    while (!tmpFile.delete()) {
                        Thread.sleep(200);
                    }
                }
            }
        } else {
            Path p = new Path(path);
            gen.gen();
            FSDataOutputStream output = fs.create(p);
            output.write(gen.getBytesBuffer());
            output.flush();
            output.close();
            fs.setPermission(p, gen.fs);
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

    public static void genHdfsFile(FileGenerator gen, String path) throws Exception {
        genHdfsFile(gen, path, true, false);
    }

    public static boolean file2Hdfs(FileSystem fs, File file, String path) {
        Path pathObj = new Path(path);
        FSDataOutputStream output = null;
        FileInputStream input = null;
        try {
            if (fs == null) fs = getFileSystem(path);

            output = fs.create(pathObj);
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

    public static boolean file2Hdfs(File file, String path) {
        return file2Hdfs(null, file, path);
    }

    private static FileSystem getFileSystem(String path) {
        URI uri = URI.create(path);
        FileSystem fs = null;
        try {
            if (fs == null) fs = FileSystem.get(uri, new Configuration());
            return fs;
        } catch (IOException e1) {
            e1.printStackTrace();
            return null;
        }
    }

    private static boolean exist(FileSystem fs, String path) throws IOException {
        if (fs == null) fs = getFileSystem(path);
        return fs.exists(new Path(path));
    }
}
