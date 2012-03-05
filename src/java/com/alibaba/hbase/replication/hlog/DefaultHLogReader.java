package com.alibaba.hbase.replication.hlog;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.regionserver.wal.HLog;
import org.apache.hadoop.hbase.regionserver.wal.HLog.Entry;

import com.alibaba.hbase.replication.domain.HLogEntry;
import com.alibaba.hbase.replication.utility.HLogUtil;

/**
 * 负责日志细节的读取工作，该类被 HLogOperator 统一管理 1. 被 HLogOperator 统一管理读取的类 2. 延迟打开
 * 
 * @author zalot.zhaoh Feb 28, 2012 2:28:25 PM
 */
public class DefaultHLogReader implements HLogReader {

    protected static final Log LOG       = LogFactory.getLog(DefaultHLogReader.class);
    HLogOperator               operator;
    private HLogEntry          entry;
    private HLog.Reader        reader;
    private long               seek;
    private boolean            hasOpened = false;
    private boolean            isOpen    = false;

    public DefaultHLogReader(){
    }

    @Override
    public void close() throws IOException {
        if (reader != null && isOpen) reader.close();
        reader = null;
        isOpen = false;
    }

    public long getPosition() throws IOException {
        lazyOpen();
        return reader.getPosition();
    }

    @Override
    public boolean hasOpened() {
        return hasOpened;
    }

    public boolean isOpen() {
        return isOpen;
    }

    private void lazyOpen() {
        try {
            if (reader == null && !hasOpened) {
                Path path = getHLogPath();
                if (path == null) return;
                reader = HLog.getReader(operator.getFileSystem(), path, operator.getConf());
                if (reader != null) {
                    isOpen = true;
                    if (seek > 0) {
                        reader.seek(seek);
                    }
                }
            }
        } catch (Exception e) {
            isOpen = false;
        } finally {
            hasOpened = true;
        }
    }

    public Entry next() throws IOException {
        lazyOpen();
        if (isOpen) {
            return reader.next();
        }
        return null;
    }

    @Override
    public Entry next(Entry reuse) throws IOException {
        lazyOpen();
        if (isOpen) return reader.next(reuse);
        return null;
    }

    @Override
    public void open() throws IOException {
        lazyOpen();
    }

    public void seek(long pos){
        if (!isOpen) {
            seek = pos;
        }
    }

    @Override
    public String toString() {
        try {
            return "LazyOpenHLogReader [reader=" + reader + ", path=" + getHLogPath() + ", type=" + entry.getType()
                   + ", seek=" + seek + ", isOpen=" + isOpen + ", hasOpened=" + hasOpened + "]";
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "ERROR";
    }

    @Override
    public void init(HLogOperator operator, HLogEntry entry) {
        this.operator = operator;
        this.entry = entry;
        seek(entry.getPos());
    }

    protected Path getHLogPath() throws IOException {
        if (HLogEntry.Type.END == entry.getType() || HLogEntry.Type.UNKNOW == entry.getType()) return null;
        Path path = HLogUtil.getPathByHLogEntry(operator.getFileSystem(), operator.getRootDir() , entry);
        if (path == null && HLogEntry.Type.LIFE == entry.getType()) {
            entry.setType(HLogEntry.Type.OLD);
            path = HLogUtil.getPathByHLogEntry(operator.getFileSystem(), operator.getRootDir() , entry);
            return path;
        }
        return path;
    }
}
