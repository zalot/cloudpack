package com.alibaba.hbase.replication.protocol;

import java.io.Serializable;

/**
 * 类Head.java的实现描述：TODO 类实现描述 
 * 
 * 协议中的 Head 信息
 *
 * 用于描述
 * 1. 传输的基本信息 
 * 2. 失败重传的信息
 * 
 * @author zalot.zhaoh Feb 28, 2012 3:53:56 PM
 */
public class Head implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = 8360136866546325565L;


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

    
    public long getFileTimestamp() {
        return fileTimestamp;
    }


    
    public void setFileTimestamp(long fileTimestamp) {
        this.fileTimestamp = fileTimestamp;
    }


    
    public long getHeadTimestamp() {
        return headTimestamp;
    }


    
    public void setHeadTimestamp(long headTimestamp) {
        this.headTimestamp = headTimestamp;
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


    
    public long getCount() {
        return count;
    }


    
    public void setCount(long count) {
        this.count = count;
    }


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
     * 文件时间
     * 
     * HLog 文件名 = groupName + timeStamp
     */
    protected long   fileTimestamp;
    
    /**
     * 头部时间
     * 
     * 头部产生的时间，可用于 HLog文件名归类后的 二次排序
     */
    protected long   headTimestamp;
    
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

}
