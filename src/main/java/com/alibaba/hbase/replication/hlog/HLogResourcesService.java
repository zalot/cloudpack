package com.alibaba.hbase.replication.hlog;

import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import com.alibaba.hbase.replication.hlog.domain.HLogEntry;
import com.alibaba.hbase.replication.hlog.reader.HLogReader;

/**
 * HLog资源服务
 * 
 * <BR>1. 通过HLog条目获取一个HLogReader
 * <BR>2. 统一管理 FileSystem 和 Path
 * <BR>3. 读取 HLog Path 
 * 
 * @author zalot.zhaoh
 */
public interface HLogResourcesService{
    public HLogReader getReader(HLogEntry entry) throws Exception;
    public List<Path> getAllHLogs();
    public List<Path> getAllOldHLogs();
    public Path getHBaseRootDir();
    public Path getOldHLogDir();
    public Path getHLogDir();
    public FileSystem getFileSystem();
    public Configuration getConf();
}
