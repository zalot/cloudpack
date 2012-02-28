package com.alibaba.hbase.replication.hlog;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;

import com.alibaba.hbase.replication.hlog.domain.HLogInfo;
import com.alibaba.hbase.replication.hlog.domain.HLogs;

/**
 * 日志管理总接口
 * 
 * 1. 通过一个 HLog 信息获取一个 HLogReader
 * 2. 获取该 Operator 的 FileSystem
 * 3. 监控被打开的 HLogReader 的数量
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
    public int getOpenFileSize();
}
