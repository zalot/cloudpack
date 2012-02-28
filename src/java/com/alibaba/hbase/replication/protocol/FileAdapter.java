package com.alibaba.hbase.replication.protocol;

/**
 * 文件适配器
 * 
 * 类FileAdapter.java的实现描述：TODO 类实现描述 
 * @author zalot.zhaoh Feb 28, 2012 2:26:28 PM
 */
public class FileAdapter implements ProtocolAdapter {

    public static Head validataFileName(String fileName){
        String[] info = fileName.split("\\.");
        if(info.length == 6){
            try{
                Head head = new Head();
                head.setVersion(Integer.parseInt(info[0]));
                head.setGroupName(info[1]);
                head.setTimestamp(Long.parseLong(info[2]));
                head.setStartOffset(Integer.parseInt(info[3]));
                head.setEndOffset(Integer.parseInt(info[4]));
                head.setCount(Integer.parseInt(info[5]));
                return head;
            }catch(Exception e){
                return null;
            }
        }
        return null;
    }
    
    public static String head2FileName(Head head){
        return head.version + "." + head.groupName + "." + head.timestamp + "." + head.startOffset + "." + head.endOffset;
    }
    
    @Override
    public void write(MetaData data) {
        Body body = data.getBody();
        String fileName = head2FileName(data.getHead());
        
    }

    @Override
    public MetaData read(Head head) {
        // TODO 读取文件
        return null;
    }
}
