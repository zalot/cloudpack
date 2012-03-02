package com.alibaba.hbase.replication.hlog;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;

import com.alibaba.hbase.replication.domain.HLogEntry;

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
    public HLogReader getReader(HLogEntry entry) throws Exception;
    public FileSystem getFileSystem();
    public Configuration getConf();
}
