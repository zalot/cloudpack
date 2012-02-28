package com.alibaba.hbase.replication.protocol;

import org.apache.hadoop.conf.Configuration;

/**
 * 文件适配器 类FileAdapter.java的实现描述：TODO 类实现描述
 * 
 * @author zalot.zhaoh Feb 28, 2012 2:26:28 PM
 */
public class FileAdapter implements ProtocolAdapter {
    protected String filePath;
    public static Head validataFileName(String fileName) {
        String[] info = fileName.split("\\.");
        if (info.length == 7) {
            try {
                Head head = new Head();
                head.setVersion(Integer.parseInt(info[0]));
                head.setGroupName(info[1]);
                head.setFileTimestamp(Long.parseLong(info[2]));
                head.setHeadTimestamp(Long.parseLong(info[3]));
                head.setStartOffset(Integer.parseInt(info[4]));
                head.setEndOffset(Integer.parseInt(info[5]));
                head.setCount(Integer.parseInt(info[6]));
                return head;
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    public static String head2FileName(Head head) {
        return   head.version + "."         // [0]
               + head.groupName + "."       // [1]
               + head.fileTimestamp + "."   // [2]
               + head.headTimestamp + "."   // [3]
               + head.startOffset + "."     // [4]
               + head.endOffset+ "."        // [5]
               + head.count+ "."            // [6]
               ;           
    }

    @Override
    public void write(MetaData data) throws Exception {
        Body body = data.getBody();
        String fileName = head2FileName(data.getHead());
        
    }

    @Override
    public MetaData read(Head head) {
        // TODO 读取文件
        return null;
    }

    @Override
    public void init(Configuration conf) {
        conf.get("");
    }
}
