package com.alibaba.hbase.replication.hlog.reader;

import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.regionserver.wal.HLog;
import org.apache.hadoop.hbase.regionserver.wal.HLog.Entry;
import org.apache.hadoop.security.AccessControlException;

import com.alibaba.hbase.replication.hlog.HLogService;
import com.alibaba.hbase.replication.hlog.domain.HLogEntry;
import com.alibaba.hbase.replication.hlog.domain.HLogEntry.Type;
import com.alibaba.hbase.replication.utility.HLogUtil;

/**
 * 负责日志细节的读取工作，该类被 HLogOperator 统一管理 1. 被 HLogOperator 统一管理读取的类 2. 延迟打开
 * 
 * @author zalot.zhaoh Feb 28, 2012 2:28:25 PM
 */
public class LazyOpenHLogReader implements HLogReader {

    protected static final Log LOG          = LogFactory.getLog(LazyOpenHLogReader.class);
    protected HLogService      operator;
    protected HLogEntry        entry;
    protected HLog.Reader      reader;
    protected long             tmpSeek;
    protected long             tmpPosistion = -1;
    protected boolean          hasOpened    = false;
    protected boolean          isOpen       = false;

    public LazyOpenHLogReader(){
    }

    @Override
    public void close() throws IOException {
        if (reader != null && isOpen) reader.close();
        reader = null;
        isOpen = false;
    }

    public long getPosition() throws IOException {
        lazyOpen();
        if (isOpen) return reader.getPosition();
        return tmpPosistion;
    }

    @Override
    public boolean hasOpened() {
        return hasOpened;
    }

    public boolean isOpen() {
        return isOpen;
    }

    private void lazyOpen() {
        Path file = null;
        try {
            if (reader == null && !hasOpened) {
                file = getHLogPath();
                if (file == null) {
                    throw new FileNotFoundException(entry.toString());
                }
                reader = HLog.getReader(operator.getFileSystem(), file, operator.getConf());
                if (reader != null) {
                    isOpen = true;
                    if (tmpSeek > 0) {
                        reader.seek(tmpSeek);
                    }
                }
            }
        }catch(EOFException eof){
        } catch(AccessControlException ace){
            LOG.error("read hlog error " + ace.getMessage());
        }catch (FileNotFoundException e) {
            entry.setType(Type.NOFOUND);
        } catch (Exception e) {
            LOG.error("read hlog error " + file, e);
        } finally {
            hasOpened = true;
        }
    }

    public Entry next() throws IOException {
        lazyOpen();
        if (isOpen) {
            try {
                return reader.next();
            } catch (EOFException eof) {
                return null;
            }
        }
        return null;
    }

    @Override
    public Entry next(Entry reuse) throws IOException {
        lazyOpen();
        if (isOpen) return reader.next(reuse);
        return null;
    }

    @Override
    public void open() throws IOException {
        lazyOpen();
    }

    public void seek(long pos) {
        if (!isOpen) {
            tmpSeek = pos;
        } else {
            try {
                reader.seek(pos);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String toString() {
        try {
            return "LazyOpenHLogReader [reader=" + reader + ", path=" + getHLogPath() + ", type=" + entry.getType()
                   + ", seek=" + tmpSeek + ", isOpen=" + isOpen + ", hasOpened=" + hasOpened + "]";
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "ERROR";
    }

    protected Path getHLogPath() throws IOException {
        if (HLogEntry.Type.END == entry.getType() || HLogEntry.Type.UNKNOW == entry.getType()) return null;
        Path path = HLogUtil.getFileStatusByHLogEntry(operator.getFileSystem(), operator.getHBaseRootDir(), entry);
        if (path == null && HLogEntry.Type.LIFE == entry.getType()) {
            entry.setType(HLogEntry.Type.OLD);
            path = HLogUtil.getFileStatusByHLogEntry(operator.getFileSystem(), operator.getHBaseRootDir(), entry);
            return path;
        }
        return path;
    }

    @Override
    public void init(HLogService operator, HLogEntry info) {
        this.operator = operator;
        this.entry = info;
        seek(entry.getPos());
    }

    @Override
    public HLogEntry getEntry() throws IOException {
        if(isOpen())
            entry.setPos(reader.getPosition());
        return entry;
    }
}
