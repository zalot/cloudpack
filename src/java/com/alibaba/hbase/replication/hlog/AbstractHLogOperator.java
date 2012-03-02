package com.alibaba.hbase.replication.hlog;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.zookeeper.KeeperException;

import com.alibaba.hbase.replication.domain.HLogEntry;
import com.alibaba.hbase.replication.utility.AliHBaseConstants;

/**
 * 日志操作 1. 统一管理 HLogReader
 * 
 * @author zalot.zhaoh
 */
public abstract class AbstractHLogOperator implements HLogOperator {

    protected static final Log LOG          = LogFactory.getLog(AbstractHLogOperator.class);
    protected Configuration    conf;
    protected FileSystem       fs;
    protected String           basePath;
    protected Path             logsPath;
    protected Path             oldLogsPath;

    public AbstractHLogOperator(Configuration conf) throws IOException, KeeperException, InterruptedException{
        this(conf, null);
    }

    public AbstractHLogOperator(Configuration conf, FileSystem fs) throws IOException, KeeperException,
                                                                  InterruptedException{
        if (fs == null) this.fs = FileSystem.get(conf);
        else this.fs = fs;
        logsPath = new Path(basePath + "/" + AliHBaseConstants.PATH_BASE_HLOG);
        oldLogsPath = new Path(basePath + "/" + AliHBaseConstants.PATH_BASE_OLDHLOG);
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
}
