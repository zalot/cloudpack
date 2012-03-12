package com.alibaba.hbase.replication.hlog;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import com.alibaba.hbase.replication.hlog.domain.HLogEntry;
import com.alibaba.hbase.replication.utility.ProducerConstants;

/**
 * 日志操作 1. 统一管理 HLogReader
 * 注意：此类的方法并非线程安全，一个线程需要new一个
 * @author zalot.zhaoh
 */
public class DefaultHLogService implements HLogService {

    protected static final Log LOG = LogFactory.getLog(DefaultHLogService.class);
    protected Configuration    conf;
    protected FileSystem       fs;
    protected Path             rootDir;
    protected Path             logsPath;
    protected Path             oldLogsPath;

    public DefaultHLogService(Configuration conf) throws IOException{
        this(conf, null);
    }

    public DefaultHLogService(Configuration conf, FileSystem fs) throws IOException{
        if (fs == null) this.fs = FileSystem.get(conf);
        else this.fs = fs;
        rootDir = new Path(conf.get("hbase.rootdir"));
        logsPath = new Path(rootDir, ProducerConstants.PATH_BASE_HLOG);
        oldLogsPath = new Path(rootDir, ProducerConstants.PATH_BASE_OLDHLOG);
        this.conf = conf;
    }

    public Configuration getConf() {
        return conf;
    }

    @Override
    public FileSystem getFileSystem() {
        return fs;
    }

    public HLogReader getReader(HLogEntry info) throws Exception {
        if (info != null) {
            LazyOpenHLogReader reader = new LazyOpenHLogReader();
            reader.init(this, info);
            return reader;
        }
        return null;
    }

    @Override
    public Path getRootDir() {
        return rootDir;
    }

    @Override
    public Path getOldHlogDir() {
        return oldLogsPath;
    }

    @Override
    public Path getHlogDir() {
        return logsPath;
    }
}
