package org.sourceopen.hadoop.hbase.replication.core.hlog.reader;

import java.io.IOException;

import org.apache.hadoop.hbase.regionserver.wal.HLog.Entry;

import org.sourceopen.hadoop.hbase.replication.core.HBaseService;
import org.sourceopen.hadoop.hbase.replication.core.hlog.domain.HLogEntry;


/**
 * 日志读取类
 * 
 * 类HLogReader.java的实现描述：TODO 类实现描述 
 * @author zalot.zhaoh Feb 28, 2012 2:28:11 PM
 */
public interface HLogReader{
    void close() throws IOException;
    void open() throws IOException;
    Entry next() throws IOException;
    Entry next(Entry reuse) throws IOException;
    void seek(long pos) throws IOException;
    long getPosition() throws IOException;
    boolean isOpen();
    boolean hasOpened();
    void init(HBaseService operator, HLogEntry info);
    HLogEntry getHLogEntry();
}
