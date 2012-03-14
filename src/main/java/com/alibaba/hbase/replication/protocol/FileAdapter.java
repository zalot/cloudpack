package com.alibaba.hbase.replication.protocol;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.annotation.PostConstruct;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.hbase.replication.protocol.exception.FileParsingException;
import com.alibaba.hbase.replication.protocol.exception.FileReadingException;
import com.alibaba.hbase.replication.protocol.protobuf.BodySerializingHandler;
import com.alibaba.hbase.replication.utility.ConsumerConstants;
import com.google.protobuf.InvalidProtocolBufferException;

/**
 * 文件适配器 类FileAdapter.java的实现描述：TODO 类实现描述
 * 
 * @author zalot.zhaoh Feb 28, 2012 2:26:28 PM
 */
@Service("fileAdapter")
public class FileAdapter implements ProtocolAdapter {

    public static final String SPLIT_SYMBOL = "|";
    protected static final Log LOG          = LogFactory.getLog(FileAdapter.class);
    /**
     * 待处理的中间文件存放位置
     */
    protected Path             targetPath;
    /**
     * md5摘要文件位置
     */
    protected Path             digestPath;
    /**
     * 已处理的中间文件存放位置
     */
    protected Path             oldPath;
    /**
     * 退回的中间文件存放位置（需要producer端重做）
     */
    protected Path             rejectPath;
    /**
     * 生成文件时使用的临时目录
     */
    protected Path             targetTmpPath;

    protected FileSystem       fs;

    @Autowired
    protected Configuration    conf;

    public void setFileSystem(FileSystem fs) {
        this.fs = fs;
    }

    public Path getPath() {
        return targetPath;
    }

    public static Head validataFileName(String fileName) {
        String[] info = StringUtils.split(fileName, SPLIT_SYMBOL);
        if (info.length == 8) {
            try {
                Head head = new Head();
                head.setVersion(Integer.parseInt(info[0]));
                if (StringUtils.isBlank(info[1])) {
                    return null;
                }
                head.setGroupName(info[1]);
                head.setFileTimestamp(Long.parseLong(info[2]));
                head.setHeadTimestamp(Long.parseLong(info[3]));
                head.setStartOffset(Integer.parseInt(info[4]));
                head.setEndOffset(Integer.parseInt(info[5]));
                head.setCount(Integer.parseInt(info[6]));
                head.setRetry(Integer.parseInt(info[7]));
                return head;
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    public static String head2FileName(Head head) {
        return head.version + SPLIT_SYMBOL // [0]
               + head.groupName + SPLIT_SYMBOL // [1]
               + head.fileTimestamp + SPLIT_SYMBOL // [2]
               + head.headTimestamp + SPLIT_SYMBOL // [3]
               + head.startOffset + SPLIT_SYMBOL // [4]
               + head.endOffset + SPLIT_SYMBOL // [5]
               + head.count + SPLIT_SYMBOL // [6]
               + head.retry + SPLIT_SYMBOL // [7]
        ;
    }

    @Override
    public MetaData read(Head head) throws Exception {
        return read(head, fs);
    }

    @Override
    public void write(MetaData data) throws Exception {
        write(data, fs);
    }

    public MetaData read(Head head, FileSystem fs) throws FileParsingException, FileReadingException {
        FSDataInputStream in = null;
        byte[] byteArray = null;
        FSDataInputStream md5In = null;
        byte[] md5ByteArray = null;
        try {
            in = fs.open(new Path(targetPath, head2FileName(head)));
            byteArray = IOUtils.toByteArray(in);
            md5In = fs.open(new Path(targetPath, head2FileName(head)));
            md5ByteArray = IOUtils.toByteArray(in);
        } catch (IOException e1) {
            throw new FileReadingException("error while reading hdfs file to bytes. file: " + head2FileName(head), e1);
        } finally {
            org.apache.hadoop.io.IOUtils.closeStream(in);
            org.apache.hadoop.io.IOUtils.closeStream(md5In);
        }
        if (byteArray != null && byteArray.length > 0 && md5ByteArray != null && md5ByteArray.length > 0) {
            try {
                MessageDigest digest = MessageDigest.getInstance("MD5");
                if (Bytes.equals(md5ByteArray, digest.digest(byteArray))) {
                    Body body = BodySerializingHandler.deserialize(byteArray);
                    Version1 result = new Version1(head, body);
                    return result;
                } else {
                    throw new FileParsingException("Fail with MD5 digest.The file corrupts probably.");
                }
            } catch (InvalidProtocolBufferException e) {
                throw new FileParsingException("error while parsing body with protobuf.", e);
            } catch (NoSuchAlgorithmException e) {
                if (LOG.isErrorEnabled()) {
                    LOG.error("Coding error!", e);
                }
            }
        }
        return null;
    }

    public void init(Configuration conf) {
        this.conf = conf;
        setPath();
    }

    /**
     * 清理producer端已处理的中间文件和slave端的临时文件
     * 
     * @param fileHead 中间文件文件名
     * @param fs
     * @throws IOException
     */
    public void clean(Head head, FileSystem fs) throws IOException {
        fs.rename(new Path(targetPath, head2FileName(head)), new Path(oldPath, head2FileName(head)));
        fs.rename(new Path(digestPath, head2FileName(head) + ConsumerConstants.MD5_SUFFIX),
                  new Path(oldPath, head2FileName(head) + ConsumerConstants.MD5_SUFFIX));
    }

    /**
     * 中间文件处理失败，退回重做
     * 
     * @param fileHead 中间文件文件名
     * @param fs
     * @throws IOException
     */
    public void reject(Head head, FileSystem fs) throws IOException {
        fs.rename(new Path(targetPath, head2FileName(head)), new Path(rejectPath, head2FileName(head)));
        fs.rename(new Path(digestPath, head2FileName(head) + ConsumerConstants.MD5_SUFFIX),
                  new Path(rejectPath, head2FileName(head) + ConsumerConstants.MD5_SUFFIX));
    }

    @PostConstruct
    public void setPath() {
        if (fs == null) {
            try {
                fs = FileSystem.get(conf);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        targetPath = new Path(conf.get(ConsumerConstants.CONFKEY_PRODUCER_FS) +
                              conf.get(ConsumerConstants.CONFKEY_TMPFILE_TARGETPATH,
                                       ConsumerConstants.TMPFILE_TARGETPATH));
        targetTmpPath = new Path(conf.get(ConsumerConstants.CONFKEY_PRODUCER_FS) + 
                                 conf.get(ConsumerConstants.CONFKEY_TMPFILE_TARGETTMPPATH,
                                          ConsumerConstants.TMPFILE_TARGETTMPPATH));
        oldPath = new Path(conf.get(ConsumerConstants.CONFKEY_PRODUCER_FS) + 
                           conf.get(ConsumerConstants.CONFKEY_TMPFILE_OLDPATH, ConsumerConstants.TMPFILE_OLDPATH));
        rejectPath = new Path(conf.get(ConsumerConstants.CONFKEY_PRODUCER_FS) +
                              conf.get(ConsumerConstants.CONFKEY_TMPFILE_REJECTPATH,
                                       ConsumerConstants.TMPFILE_REJECTPATH));

        digestPath = new Path(targetPath, ConsumerConstants.MD5_DIR);
        producter_check();
    }

    protected void producter_check() {
        try {
            if (!fs.exists(targetPath)) {
                fs.mkdirs(targetPath);
            }
            if (!fs.exists(targetTmpPath)) {
                fs.mkdirs(targetTmpPath);
            }
            if (!fs.exists(digestPath)) {
                fs.mkdirs(digestPath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void write(MetaData data, FileSystem fs) {
        FSDataOutputStream targetOutput = null;
        FSDataOutputStream targetMD5Output = null;
        try {
            // write tmpFile
            String fileName = head2FileName(data.getHead());
            byte[] bodyBytes = BodySerializingHandler.serialize(data.getBody());
            Path targetTmpFilePath = new Path(targetTmpPath, fileName);
            targetOutput = fs.create(targetTmpFilePath, true);
            targetOutput.write(bodyBytes);
            targetOutput.close();
            
            // write MD5
            MessageDigest digest = MessageDigest.getInstance("MD5");
            Path tmpMD5Path = new Path(targetTmpPath, fileName + ConsumerConstants.MD5_SUFFIX);
            targetMD5Output = fs.create(tmpMD5Path, true);
            targetMD5Output.write(digest.digest(bodyBytes));
            targetMD5Output.close();
            
            // move tmpFile and MD5File to source directory
            Path sourceFilePath = new Path(targetPath, targetTmpFilePath.getName());
            Path sourceMd5FilePath = new Path(digestPath, tmpMD5Path.getName());
            if (!fs.rename(targetTmpFilePath, sourceFilePath)) {
                throw new RuntimeException(targetTmpPath + " rename to " + sourceFilePath);
            }

            if (!fs.rename(tmpMD5Path, sourceMd5FilePath)) {
                throw new RuntimeException(targetTmpPath + " rename to " + sourceFilePath);
            }
        } catch (IOException e) {
            if (LOG.isErrorEnabled()) {
                LOG.error("Create file failed.", e);
            }
        } catch (NoSuchAlgorithmException e) {
            if (LOG.isErrorEnabled()) {
                LOG.error("Coding error!", e);
            }
        } finally {
            if (targetOutput != null) {
                try {
                    targetOutput.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    targetOutput = null;
                }
            }
            if (targetMD5Output != null) {
                try {
                    targetMD5Output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    targetMD5Output = null;
                }
            }
        }
    }
}
