package com.alibaba.hbase.replication.hlog;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import com.alibaba.hbase.replication.hlog.domain.HLogEntry;

/**
 * 日志管理总接口
 * 
 * 1. 通过一个 HLog 信息获取一个 HLogReader
 * 2. 获取该 Operator 的 FileSystem
 * 
 * @author zalot.zhaoh
 */
public interface HLogService {
    public HLogReader getReader(HLogEntry entry) throws Exception;
    public Path getRootDir();
    public Path getOldHlogDir();
    public Path getHlogDir();
    public FileSystem getFileSystem();
    public Configuration getConf();
}
