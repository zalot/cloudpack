package com.alibaba.hbase.replication.hlog.reader;

import java.io.IOException;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.hbase.regionserver.wal.HLog.Entry;

import com.alibaba.hbase.replication.domain.HLogInfo;
import com.alibaba.hbase.replication.hlog.HLogOperator;


/**
 * 日志读取接口
 * 
 * 类HLogReader.java的实现描述：TODO 类实现描述 
 * @author zalot.zhaoh Feb 28, 2012 2:28:11 PM
 */
public interface HLogReader{
	HLogInfo getHLogInfo();
    void close() throws IOException;
    void open() throws IOException;
//    boolean hasMoreNext();
    Entry next() throws IOException;
    Entry next(Entry reuse) throws IOException;
    void seek(long pos) throws IOException;
    long getPosition() throws IOException;
    boolean isOpen();
    boolean hasOpened();
    void init(HLogOperator operator, HLogInfo info);
}
