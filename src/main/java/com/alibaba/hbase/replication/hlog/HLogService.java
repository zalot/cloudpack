package com.alibaba.hbase.replication.hlog;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HConstants;

import com.alibaba.hbase.replication.hlog.domain.HLogEntry;
import com.alibaba.hbase.replication.hlog.reader.HLogReader;
import com.alibaba.hbase.replication.hlog.reader.LazyOpenHLogReader;
import com.alibaba.hbase.replication.utility.HLogUtil;
import com.alibaba.hbase.replication.utility.ProducerConstants;

/**
 * HLog资源服务 <BR>
 * 1. 通过HLog条目获取一个HLogReader <BR>
 * 2. 统一管理 FileSystem 和 Path <BR>
 * 3. 读取 HLog Path
 * 
 * @author zalot.zhaoh
 */
public class HLogService {

    protected static final Log            LOG = LogFactory.getLog(HLogService.class);
    protected Configuration               conf;
    protected FileSystem                  fs;
    protected Path                        rootDir;
    protected Path                        logsPath;
    protected Path                        oldLogsPath;
    protected Class<? extends HLogReader> logReaderClass;

    public HLogService(Configuration conf) throws IOException{
        this(conf, null);
    }

    public HLogService(Configuration conf, FileSystem fs) throws IOException{
        if (fs == null) this.fs = FileSystem.get(conf);
        else this.fs = fs;
        String strRootDir = conf.get(ProducerConstants.CONFKEY_HDFS_HBASE_ROOT);
        if (strRootDir == null) strRootDir = conf.get(HConstants.HBASE_DIR);
        rootDir = new Path(strRootDir);
        logsPath = new Path(rootDir, ProducerConstants.PATH_BASE_HLOG);
        oldLogsPath = new Path(rootDir, ProducerConstants.PATH_BASE_OLDHLOG);
        this.conf = conf;
    }

    public Configuration getConf() {
        return conf;
    }

    public FileSystem getFileSystem() {
        return fs;
    }

    public HLogReader getReader(HLogEntry info) throws Exception {
        if (info != null) {
            try {
                if (logReaderClass == null) {
                    logReaderClass = conf.getClass(ProducerConstants.CONFKEY_LOGREADER_CLASS, LazyOpenHLogReader.class,
                                                   HLogReader.class);
                }
                HLogReader reader = logReaderClass.newInstance();
                reader.init(this, info);
                return reader;
            } catch (Exception e) {
                throw new IOException("Cannot get hlogreader", e);
            }
        }
        return null;
    }

    public Path getHBaseRootDir() {
        return rootDir;
    }

    public Path getOldHLogDir() {
        return oldLogsPath;
    }

    public Path getHLogDir() {
        return logsPath;
    }

    @SuppressWarnings("unchecked")
    public List<Path> getAllHLogs() {
        try {
            return HLogUtil.getHLogsByHDFS(getFileSystem(), getHLogDir());
        } catch (IOException e) {
            LOG.error("reader all HLogs error " , e);
            return Collections.EMPTY_LIST;
        }
    }

    @SuppressWarnings("unchecked")
    public List<Path> getAllOldHLogs() {
        try {
            return HLogUtil.getHLogsByHDFS(getFileSystem(), getOldHLogDir());
        } catch (IOException e) {
            LOG.error("reader all OldHLogs error " , e);
            return Collections.EMPTY_LIST;
        }
    }
}
