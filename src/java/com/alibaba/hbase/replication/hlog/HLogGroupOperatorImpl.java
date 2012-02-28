package com.alibaba.hbase.replication.hlog;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.regionserver.wal.HLog.Entry;
import org.apache.zookeeper.KeeperException;

import com.alibaba.hbase.replication.domain.HLogGroup;
import com.alibaba.hbase.replication.domain.HLogInfo;

/**
 * 多线程的 HLogReader
 * 
 * @author zalot.zhaoh
 */
public class HLogGroupOperatorImpl extends ZookeeperHLogOperator implements HLogOperatorTransaction {

    public HLogGroupOperatorImpl(Configuration conf) throws IOException, KeeperException, InterruptedException{
    }

    public static class NoFoundEntryInfoException extends Exception {

        /**
		 * 
		 */
        private static final long serialVersionUID = 1895927503779821652L;

        public NoFoundEntryInfoException(){
        }
    }

    protected HLogInfo rollHlog() {
        HLogGroup hgroup = currentGroup.get();
        if (hgroup == null) {
            synchronized (_hogs) {
                hgroup = _hogs.nextGroup();
                if (hgroup != null) {
                    hgroup.sort();
                    currentGroup.set(hgroup);
                } else {
                    return null;
                }
            }
        }

        HLogInfo info = hgroup.next();
        if (info == null) {
            currentGroup.set(null);
            return rollHlog();
        }
        return info;
    }

    protected HLogReader getReader() {
        return null;
    }

    @Override
    public Entry next() {
        HLogReader reader = getReader();
        return reader;
    }

    @Override
    public boolean commit() {
        try {
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean process(ReplicationCallBack call) {
        
        return false;
    }

    @Override
    public boolean process(ReplicationCallBack call, int count) {
        Entry entry = null;
        int curCount = 0;
        while (true) {
            if (curCount == count) break;
            entry = next();
            if (entry == null) break;
            if (call.next(currentHLog.get(), entry)) {
                curCount++;
                break;
            } else {
                return false;
            }
        }
        if (curCount > 0) {
            return commit();
        }
        return true;
    }

    @Override
    public void start() {
        for (int x = 0; x < threadSize; x++) {
            threadPool.execute(getThreadByType());
        }
    }

    private Runnable getThreadByType() {
        if (threadType == 1) {
            return getThread1();
        } else {
            return getThread2();
        }
    }

    protected Runnable getThread1() {
        Runnable run = new Runnable() {
            @Override
            public void run() {
                Entry entry = next();
                if (call.next(currentHLog.get(), entry)) {
                    return commit();
                }
            }
        };
        return run;
    }

    protected Runnable getThread2() {
        Runnable run = new Runnable() {

            @Override
            public void run() {
                // List<EntryInfo> entryInfos = null;
                // EntryInfo entryInfo = null;
                // while(!isStop){
                // try {
                // entryInfos = new ArrayList<EntryInfo>();
                // while((entryInfo = hlogOperator.next()) != null){
                // entryInfos.add(entryInfo);
                // }
                // if(replication.puts(entryInfos)){
                // hlogOperator.commit(entryInfo);
                // LOG.info("[ReplicationManager][Commits]" + entryInfos);
                // }
                // } catch (Exception e) {
                // LOG.error(e);
                // }
                // }
            }
        };
        return run;
    }
}
