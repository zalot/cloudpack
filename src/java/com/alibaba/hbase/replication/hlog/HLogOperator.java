package com.alibaba.hbase.replication.hlog;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;

import com.alibaba.hbase.replication.domain.HLogInfo;
import com.alibaba.hbase.replication.domain.HLogs;
import com.alibaba.hbase.replication.hlog.reader.HLogReader;

/**
 * 日志操作接口
 * 
 * @author zalot.zhaoh
 */
public interface HLogOperator {
    public static String HBASE_LOG    = ".logs";
    public static String HBASE_OLDLOG = ".oldlogs";
    public static final String SYMBOL       = "/";
    
    public HLogReader getReader(HLogInfo info) throws Exception;
    public void commit(HLogReader reader) throws Exception;
    public FileSystem getFileSystem();
    public Configuration getConf();
    public HLogs getHLogs();
    public boolean flush();
    public void close();
    public void open();
    public boolean isClosed();
}
