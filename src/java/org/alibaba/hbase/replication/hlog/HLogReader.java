package org.alibaba.hbase.replication.hlog;

import java.io.IOException;

import org.alibaba.hbase.replication.domain.HLogInfo;
import org.apache.hadoop.hbase.regionserver.wal.HLog.Entry;

/**
 * 日志读取
 * 
 * @author zalot.zhaoh
 *
 */
public interface HLogReader extends Comparable<HLogReader>{
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
}
