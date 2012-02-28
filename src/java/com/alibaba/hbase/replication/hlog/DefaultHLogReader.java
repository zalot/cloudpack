package com.alibaba.hbase.replication.hlog;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hbase.regionserver.wal.HLog;
import org.apache.hadoop.hbase.regionserver.wal.HLog.Entry;

import com.alibaba.hbase.replication.hlog.domain.HLogInfo;

/**
 * 负责日志细节的读取工作，该类被 HLogOperator 统一管理
 * 1. 被 HLogOperator 统一管理读取的类 
 * 2. 延迟打开
 * @author zalot.zhaoh Feb 28, 2012 2:28:25 PM
 */
public class DefaultHLogReader implements HLogReader {

    protected static final Log LOG       = LogFactory.getLog(DefaultHLogReader.class);
    HLogOperator               operator;
    private HLogInfo           info;
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
        LOG.debug("[HLogReader][Close][TYPE." + info.getType() + "]" + Thread.currentThread().getName() + " [File] "
                  + info.getPath().getName());
    }

    public long getPosition() throws IOException {
        if (operator.isClosed()) {
            return -1;
        }
        lazyOpen();
        return reader.getPosition();
    }

    @Override
    public HLogInfo getHLogInfo() {
        return info;
    }

    @Override
    public boolean hasOpened() {
        return hasOpened;
    }

    public boolean isOpen() {
        if (operator.isClosed()) {
            return true;
        }
        return isOpen;
    }

    private void lazyOpen() {
        try {
            if (!operator.isClosed() && reader == null && !hasOpened) {
                LOG.debug("[HLogReader][Open][TYPE." + info.getType() + "]" + Thread.currentThread().getName()
                          + " [File] " + info.getPath().getName());
                reader = HLog.getReader(operator.getFileSystem(), info.getPath(), operator.getConf());
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
        if (operator.isClosed()) {
            close();
            return null;
        }
        lazyOpen();
        if (isOpen) {
            return reader.next();
        }
        return null;
    }

    @Override
    public Entry next(Entry reuse) throws IOException {
        if (operator.isClosed()) {
            close();
            return null;
        }
        lazyOpen();
        if (isOpen) return reader.next(reuse);
        return null;
    }

    // @Override
    // public boolean hasMoreNext() {
    // lazyOpen();
    // try{
    // if(isOpen()){
    // long pos = reader.getPosition();
    // Entry entry = reader.next();
    // if(entry != null){
    // if(reader.getPosition() != pos)
    // reader.seek(pos);
    // return true;
    // }else{
    // // 如果该文件已经没有内容则关闭
    // close();
    // }
    // }
    // }catch(Exception e){
    // try {
    // close();
    // } catch (IOException e1) {
    // }
    // }
    // return false;
    // }

    @Override
    public void open() throws IOException {
        lazyOpen();
    }

    public void seek(long pos) throws IOException {
        if (!isOpen) {
            seek = pos;
        }
    }

    @Override
    public String toString() {
        return "LazyOpenHLogReader [reader=" + reader + ", path=" + info.getPath() + ", type=" + info.getType() + ", seek="
               + seek + ", isOpen=" + isOpen + ", hasOpened=" + hasOpened + "]";
    }

    @Override
    public void init(HLogOperator operator, HLogInfo info) {
        this.operator = operator;
        this.info = info;
    }
}
