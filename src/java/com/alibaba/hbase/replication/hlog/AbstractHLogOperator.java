package com.alibaba.hbase.replication.hlog;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.zookeeper.KeeperException;

import com.alibaba.hbase.replication.domain.DefaultHLogs;
import com.alibaba.hbase.replication.domain.HLogInfo;
import com.alibaba.hbase.replication.domain.HLogs;
import com.alibaba.hbase.replication.utility.HLogUtil;

/**
 * 日志操作
 * 
 * @author zalot.zhaoh
 */
public abstract class AbstractHLogOperator implements HLogOperator {

    protected static final Log LOG          = LogFactory.getLog(AbstractHLogOperator.class);
    protected Configuration    conf;
    protected FileSystem       fs;
    protected Path             logsPath;
    protected Path             oldLogsPath;
    protected boolean          isClosed     = false;
    protected AtomicInteger    openFileSize = new AtomicInteger(0);

    public AbstractHLogOperator(Configuration conf) throws IOException, KeeperException, InterruptedException{
        this(conf, null);
    }

    public AbstractHLogOperator(Configuration conf, FileSystem fs) throws IOException, KeeperException,
                                                                  InterruptedException{
        if (fs == null) this.fs = FileSystem.get(conf);
        else this.fs = fs;
        this.conf = conf;

    }

    @Override
    public synchronized void close() {
        isClosed = true;
    }

    @Override
    public void commit(HLogReader reader) throws Exception {
        openFileSize.getAndDecrement();
    }

    @Override
    public boolean flush() {
        return false;
    }

    public Configuration getConf() {
        return conf;
    }

    @Override
    public FileSystem getFileSystem() {
        return fs;
    }

    @Override
    public HLogs getHLogs() {
        try {
            return initHLogs(fs, logsPath, logsPath);
        } catch (IOException e) {
            return new DefaultHLogs();
        }
    }

    @Override
    public int getOpenFileSize() {
        return openFileSize.get();
    }

    public HLogReader getReader(HLogInfo info) throws Exception {
        if (info != null) {
            DefaultHLogReader reader = new DefaultHLogReader();
            reader.init(this, info);
            openFileSize.getAndIncrement();
            return reader;
        }
        return null;
    }

    protected HLogs initHLogs(FileSystem fs, Path logPath, Path oldPath) throws IOException {
        HLogs _hogs = new DefaultHLogs();
        if (logPath != null) _hogs.put(HLogUtil.getHLogsByHDFS(fs, logPath));
        if (oldPath != null) _hogs.put(HLogUtil.getHLogsByHDFS(fs, oldPath));
        return _hogs;
    }

    @Override
    public boolean isClosed() {
        return false;
    }

    @Override
    public void open() {
        // TODO Auto-generated method stub

    }
}
