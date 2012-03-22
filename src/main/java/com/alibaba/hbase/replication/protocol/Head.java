package com.alibaba.hbase.replication.protocol;

import java.io.Serializable;

/**
 * 协议中的 Head 信息 用于 <BR>
 * 1. 传输的基本信息 2. 失败重传的信息 <BR>
 * 
 * @author zalot.zhaoh Feb 28, 2012 3:53:56 PM
 */
public class Head implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 8360136866546325565L;

    /**
     * 数量
     */
    protected long   count;

    /**
     * 结束偏移量 （用于 HLog.Reader 中的 position ）
     */
    protected long   endOffset;

    /**
     * 文件时间 HLog 文件名 = groupName + timeStamp
     */
    protected long   fileTimestamp;

    /**
     * 组名 HLog 文件名 = groupName + timeStamp
     */
    protected String groupName;

    /**
     * 头部时间 头部产生的时间，可用于 HLog文件名归类后的 二次排序
     */
    protected long   headTimestamp;

    /**
     * 重试次数，对于正常文件此项为0
     */
    protected int    retry = 0;

    /**
     * 起始偏移量 （用于 HLog.Reader 中的 position ）
     */
    protected long   startOffset;

    /**
     * 版本号
     */
    protected int    version = 1;

    public Head(){
        this.setHeadTimestamp(System.currentTimeMillis());
    }
    public long getCount() {
        return count;
    }

    public long getEndOffset() {
        return endOffset;
    }

    public long getFileTimestamp() {
        return fileTimestamp;
    }

    public String getGroupName() {
        return groupName;
    }

    public long getHeadTimestamp() {
        return headTimestamp;
    }

    /**
     * @return the retry
     */
    public int getRetry() {
        return retry;
    }

    public long getStartOffset() {
        return startOffset;
    }

    public int getVersion() {
        return version;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public void setEndOffset(long endOffset) {
        this.endOffset = endOffset;
    }

    public void setFileTimestamp(long fileTimestamp) {
        this.fileTimestamp = fileTimestamp;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    protected void setHeadTimestamp(long headTimestamp) {
        this.headTimestamp = headTimestamp;
    }

    /**
     * @param retry the retry to set
     */
    public void setRetry(int retry) {
        this.retry = retry;
    }

    public void setStartOffset(long startOffset) {
        this.startOffset = startOffset;
    }

    protected void setVersion(int version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "Head [version=" + version + ", groupName=" + groupName + ", fileTimestamp=" + fileTimestamp
               + ", headTimestamp=" + headTimestamp + ", startOffset=" + startOffset + ", endOffset=" + endOffset
               + ", count=" + count + ", retry=" + retry + "]";
    }

}
