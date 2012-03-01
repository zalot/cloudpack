package com.alibaba.hbase.replication.protocol;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;

import com.alibaba.hbase.replication.protocol.exception.FileNotFoundException;
import com.alibaba.hbase.replication.protocol.exception.FileParsingException;

/**
 * 文件适配器 类FileAdapter.java的实现描述：TODO 类实现描述
 * 
 * @author zalot.zhaoh Feb 28, 2012 2:26:28 PM
 */
public class FileAdapter implements ProtocolAdapter {

    protected String filePath;

    public static Head validataFileName(String fileName) {
        String[] info = fileName.split("\\.");
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
        return head.version + "." // [0]
               + head.groupName + "." // [1]
               + head.fileTimestamp + "." // [2]
               + head.headTimestamp + "." // [3]
               + head.startOffset + "." // [4]
               + head.endOffset + "." // [5]
               + head.count + "." // [6]
               + head.retry + "." // [7]
        ;
    }

    @Override
    public void write(MetaData data) throws Exception {
        Body body = data.getBody();
        String fileName = head2FileName(data.getHead());

    }

    public static MetaData read(Head head, FileSystem fs) throws FileParsingException {
        // TODO 读取文件
        return null;
    }

    @Override
    public void init(Configuration conf) {
        conf.get("");
    }

    /**
     * 清理producer端已处理的中间文件和slave端的临时文件
     * 
     * @param fileHead 中间文件文件名
     * @param fs
     */
    public static void clean(Head fileHead, FileSystem fs) {
        // TODO Auto-generated method stub

    }

    /**
     * 中间文件处理失败，退回重做
     * 
     * @param fileHead 中间文件文件名
     * @param fs
     */
    public static void reject(Head fileHead, FileSystem fs) {
        // TODO Auto-generated method stub

    }
}
