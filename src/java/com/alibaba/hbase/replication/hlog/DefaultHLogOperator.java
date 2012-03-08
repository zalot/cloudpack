package com.alibaba.hbase.replication.hlog;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import com.alibaba.hbase.replication.hlog.domain.HLogEntry;
import com.alibaba.hbase.replication.utility.AliHBaseConstants;

/**
 * 日志操作 1. 统一管理 HLogReader
 * 
 * @author zalot.zhaoh
 */
public class DefaultHLogOperator implements HLogOperator {

    protected static final Log LOG = LogFactory.getLog(DefaultHLogOperator.class);
    protected Configuration    conf;
    protected FileSystem       fs;
    protected Path             rootDir;
    protected Path             logsPath;
    protected Path             oldLogsPath;

    public DefaultHLogOperator(Configuration conf) throws IOException{
        this(conf, null);
    }

    public DefaultHLogOperator(Configuration conf, FileSystem fs) throws IOException{
        if (fs == null) this.fs = FileSystem.get(conf);
        else this.fs = fs;
        rootDir = new Path(conf.get("hbase.rootdir"));
        logsPath = new Path(rootDir, AliHBaseConstants.PATH_BASE_HLOG);
        oldLogsPath = new Path(rootDir, AliHBaseConstants.PATH_BASE_OLDHLOG);
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
            DefaultHLogReader reader = new DefaultHLogReader();
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
