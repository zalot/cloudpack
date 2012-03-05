package com.alibaba.hbase.replication.protocol;

import java.io.IOException;
import java.security.MessageDigest;

import javax.annotation.PostConstruct;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.hbase.replication.consumer.Constants;
import com.alibaba.hbase.replication.protocol.exception.FileParsingException;
import com.alibaba.hbase.replication.protocol.exception.FileReadingException;
import com.alibaba.hbase.replication.protocol.protobuf.BodySerializingHandler;
import com.google.protobuf.InvalidProtocolBufferException;

/**
 * 文件适配器 类FileAdapter.java的实现描述：TODO 类实现描述
 * 
 * @author zalot.zhaoh Feb 28, 2012 2:26:28 PM
 */
@Service("fileAdapter")
public class FileAdapter implements ProtocolAdapter {

    /**
     * 待处理的中间文件存放位置
     */
    protected String            filePath;
    /**
     * 已处理的中间文件存放位置
     */
    protected String            oldPath;
    /**
     * 退回的中间文件存放位置（需要producer端重做）
     */
    protected String            rejectPath;
    @Autowired
    protected Configuration     conf;
    public static final String  SPLIT_SYMBOL = "|";
    public static MessageDigest digest       = null;
    protected Path              targetPath;
    protected Path              againPath;
    protected Path              tmpPath;
    protected FileSystem        fs;

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
    public void write(MetaData data) throws Exception {
        Path target = write(data, fs, tmpPath);
        if (target != null) {
            Path ot = new Path(targetPath.toString() + "/" + target.getName());
            if (!fs.rename(target, ot)) {
                throw new RuntimeException(target + " rename to " + ot);
            }
        }
    }

    public MetaData read(Head head, FileSystem fs) throws FileParsingException, FileReadingException {
        FSDataInputStream in = null;
        byte[] byteArray = null;
        try {
            in = fs.open(new Path(conf.get(Constants.PRODUCER_FS), filePath + head2FileName(head)));
            byteArray = IOUtils.toByteArray(in);
        } catch (IOException e1) {
            throw new FileReadingException("error while reading hdfs file to bytes. file: " + head2FileName(head), e1);
        } finally {
            org.apache.hadoop.io.IOUtils.closeStream(in);
        }
        if (byteArray != null && byteArray.length > 0) {
            try {
                Body body = BodySerializingHandler.deserialize(byteArray);
                Version1 result = new Version1(head, body);
                return result;
            } catch (InvalidProtocolBufferException e) {
                throw new FileParsingException("error while parsing body with protobuf.", e);
            }
        }
        return null;
    }

    public static Path write(MetaData meta, FileSystem fs, Path root) {
        FSDataOutputStream output = null;
        FSDataOutputStream sigoutput = null;
        Path target = null;
        try {
            Body body = meta.getBody();
            String fileName = head2FileName(meta.getHead());
            byte[] data = BodySerializingHandler.serialize(meta.getBody());
            digest.update(data);
            target = new Path(root, fileName);
            output = fs.create(target, true);
            output.write(data);
            sigoutput = fs.create(new Path(fileName + "_md5"));
            sigoutput.write(digest.digest());
            return target;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    output = null;
                }
            }
            if (sigoutput != null) {
                try {
                    sigoutput.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    sigoutput = null;
                }
            }
        }
        return null;
    }

    @Override
    public void init(Configuration conf) {
        String hbaseDir = conf.get("hbase.rootdir");
        try {
            tmpPath = new Path(hbaseDir + "/alirepstmp");
            targetPath = new Path(hbaseDir + "/alireps");
            fs = FileSystem.get(conf);
            if (!fs.exists(tmpPath)) {
                fs.mkdirs(tmpPath);
            }
            if (!fs.exists(targetPath)) {
                fs.mkdirs(targetPath);
            }
            digest = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
        }
    }

    /**
     * 清理producer端已处理的中间文件和slave端的临时文件
     * 
     * @param fileHead 中间文件文件名
     * @param fs
     * @throws IOException
     */
    public void clean(Head head, FileSystem fs) throws IOException {
        fs.rename(new Path(conf.get(Constants.PRODUCER_FS), filePath + head2FileName(head)),
                  new Path(conf.get(Constants.PRODUCER_FS), oldPath + head2FileName(head)));
    }

    /**
     * 中间文件处理失败，退回重做
     * 
     * @param fileHead 中间文件文件名
     * @param fs
     */
    public void reject(Head head, FileSystem fs) {
        // FIXME

    }

    @PostConstruct
    public void setPath() {
        filePath = conf.get(Constants.TMPFILE_FILEPATH);
        oldPath = conf.get(Constants.TMPFILE_OLDPATH);
        rejectPath = conf.get(Constants.TMPFILE_REJECTPATH);
    }

}
