package org.sourceopen.hadoop.hbase.replication.consumer;

import java.io.IOException;
import java.io.Serializable;

import org.apache.hadoop.hbase.util.Bytes;

/**
 * 类ConsumerZNode.java的实现描述：一个中间文件被处理的状态节点
 * 
 * @author dongsh 2012-2-29 下午01:58:10
 * @author zalot.zhaoh<BR>
 * ------------------------------------------<BR>
 * toByteArray 替换成了普通的String byts<BR>
 * ------------------------------------------
 */
public class ConsumerZNode implements Serializable {

    public static final String SPLIT            = "|";
    private static final long  serialVersionUID = -8921421815693312454L;
    private String             groupName;
    private String             fileName;
    private long               fileLastModified;
    private long               statusLastModified;
    private String             processorName;
    private int                version;

    /**
     * @return the groupName
     */
    public String getGroupName() {
        return groupName;
    }

    /**
     * @param groupName the groupName to set
     */
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFileLastModified() {
        return fileLastModified;
    }

    public void setFileLastModified(long fileLastModified) {
        this.fileLastModified = fileLastModified;
    }

    public long getStatusLastModified() {
        return statusLastModified;
    }

    public void setStatusLastModified(long statusLastModified) {
        this.statusLastModified = statusLastModified;
    }

    public String getProcessorName() {
        return processorName;
    }

    public void setProcessorName(String processorName) {
        this.processorName = processorName;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public byte[] toByteArray() {
        // len = 6
        final String data = groupName // 0
                            + SPLIT + fileName // 1
                            + SPLIT + processorName // 2
                            + SPLIT + fileLastModified // 3
                            + SPLIT + statusLastModified // 4
                            + SPLIT + version; // 5

        // len = 7
        // final String data = "";

        // len = 8
        // final String data = "";
        return Bytes.toBytes(data);
    }

    public static ConsumerZNode getFromByteArray(byte[] bytes) throws IOException, ClassNotFoundException {
        String data = Bytes.toString(bytes);
        String[] datas = data.split("|");
        ConsumerZNode cz = new ConsumerZNode();
        if (datas.length == 6) {
            cz.setGroupName(datas[0]);
            cz.setFileName(datas[1]);
            cz.setProcessorName(datas[2]);
            cz.setFileLastModified(Long.parseLong(datas[3]));
            cz.setStatusLastModified(Long.parseLong(datas[4]));
            cz.setVersion(Integer.parseInt(datas[5]));
        }
        return cz;
    }

    public static enum Status {
        New, Processing, End, Error;
    }

}
