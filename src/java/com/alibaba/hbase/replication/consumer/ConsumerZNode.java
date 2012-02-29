/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.hbase.replication.consumer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * 类ConsumerZNode.java的实现描述：一个中间文件被处理的状态节点
 * 
 * @author dongsh 2012-2-29 下午01:58:10
 */
public class ConsumerZNode implements Serializable {

    private static final long serialVersionUID = -8921421815693312454L;
    private String            fileName;
    private Status            status;
    private long              fileLastModified;
    private long              statusLastModified;
    private String            processorName;
    private int               version;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
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

    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(this);
        oos.flush();
        return baos.toByteArray();
    }

    public static ConsumerZNode getFromByteArray(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bi = new ByteArrayInputStream(bytes);
        ObjectInputStream oi = new ObjectInputStream(bi);
        return (ConsumerZNode) oi.readObject();
    }

    public static enum Status {
        New, Processing, End, Error;
    }

}
