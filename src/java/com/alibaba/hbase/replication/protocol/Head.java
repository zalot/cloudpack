package com.alibaba.hbase.replication.protocol;

/**
 * 协议中的 Head 信息
 *
 * 
 * 类Head.java的实现描述：TODO 类实现描述 
 * @author zalot.zhaoh Feb 28, 2012 3:53:56 PM
 */
public class Head {

    /**
     * 版本号
     */
    protected int    version;
    
    
    /**
     * 组名
     * 
     * HLog 文件名 = groupName + timeStamp
     */
    protected String groupName;
    
    
    /**
     * 时间
     * 
     * HLog 文件名 = groupName + timeStamp
     */
    protected long   timestamp;
    
    
    /**
     * 起始偏移量 （用于 HLog.Reader 中的 position ）
     */
    protected int    startOffset;
    
    /**
     * 结束偏移量 （用于 HLog.Reader 中的 position ）
     */
    protected int    endOffset;
    
    
    /**
     * 数量
     */
    protected long   count;

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getStartOffset() {
        return startOffset;
    }

    public void setStartOffset(int startOffset) {
        this.startOffset = startOffset;
    }

    public int getEndOffset() {
        return endOffset;
    }

    public void setEndOffset(int endOffset) {
        this.endOffset = endOffset;
    }

}
