package org.sourceopen.hadoop.hbase.replication.producer;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.util.Bytes;
import org.sourceopen.hadoop.hbase.replication.core.HBaseService;
import org.sourceopen.hadoop.hbase.replication.core.hlog.domain.HLogEntry;
import org.sourceopen.hadoop.hbase.replication.core.hlog.domain.HLogGroup;
import org.sourceopen.hadoop.hbase.replication.core.hlog.domain.HLogPersistence;
import org.sourceopen.hadoop.zookeeper.concurrent.ZDaemonThread;
import org.sourceopen.hadoop.zookeeper.connect.AdvZooKeeper;
import org.sourceopen.hadoop.zookeeper.core.ZNode;

/**
 * HLogGroup 扫描器<BR>
 * 类HLogGroupZookeeperScanner.java的实现描述：TODO 类实现描述
 * 
 * @author zalot.zhaoh Mar 1, 2012 10:44:45 AM
 */
public class HLogScanner extends ZDaemonThread {

    protected static final String THREADLOCKKEY = "hlogscaner";
    protected static final Log    LOG           = LogFactory.getLog(HLogScanner.class);
    protected static final long   LOCKTIME      = 500;

    /**
     * Group and Entry Map Object <BR>
     * 类HLogGroupZookeeperScannerThread.java的实现描述：TODO 类实现描述
     * 
     * @author zalot.zhaoh Aug 7, 2012 3:59:40 PM
     */
    public static class GEMap {

        // name , path
        protected Map<String, HLogGroup> groups = new ConcurrentHashMap<String, HLogGroup>();

        protected Configuration          conf   = null;

        public HLogGroup get(String name) {
            return groups.get(name);
        }

        public void put(Path path) {
            if (path == null || path.getName().length() <= 0) return;
            HLogEntry entry = new HLogEntry(path);
            putGroup(entry);
        }

        protected void putGroup(HLogEntry entry) {
            HLogGroup group = groups.get(entry.getGroupName());
            if (group == null) {
                group = new HLogGroup(entry.getGroupName());
                groups.put(entry.getGroupName(), group);
            }
            group.put(entry);
        }

        public void put(List<Path> paths) {
            if (paths == null || paths.size() <= 0) return;
            for (Path path : paths) {
                put(path);
            }
        }

        public void clear() {
            groups.clear();
        }

        public int size() {
            return groups.size();
        }

        public Collection<HLogGroup> getGroups() {
            return groups.values();
        }
    }

    protected HBaseService    hbaseService;
    protected HLogPersistence hlogPersistence;

    public HLogScanner(AdvZooKeeper zk, ZNode root, long tryLockTime, long onceSleepTime){
        super(zk, root, THREADLOCKKEY, tryLockTime, onceSleepTime);
    }

    public HLogPersistence getHLogPersistence() {
        return hlogPersistence;
    }

    public void setHLogPersistence(HLogPersistence hlogPersistence) {
        this.hlogPersistence = hlogPersistence;
    }

    public HBaseService getHlogService() {
        return hbaseService;
    }

    public void setHBaseService(HBaseService hlogService) {
        this.hbaseService = hlogService;
    }

    public void doScan() throws Exception {
        GEMap groups = new GEMap();
        putHLog(groups);
        putOldHLog(groups);

        // 清理 group
        for (String groupStr : hlogPersistence.listGroupName()) {
            if (groups.get(groupStr) == null) {
                hlogPersistence.deleteGroup(groupStr);
                if (LOG.isDebugEnabled()) {
                    LOG.debug(getThreadName() + " scan delete group[" + groupStr + "]");
                }
            }
        }

        // 清理 entry
        for (HLogGroup group : groups.getGroups()) {
            HLogGroup tmpGroup = hlogPersistence.getGroupByName(group.getGroupName(), false);
            if (tmpGroup == null) {
                hlogPersistence.createGroup(group, false);
            }

            for (HLogEntry entry : hlogPersistence.listEntry(group.getGroupName())) {
                if (!group.contains(entry)) {
                    hlogPersistence.deleteEntry(entry);
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(getThreadName() + " scan delete group-entry[" + entry + "]");
                    }
                }
            }
            boolean isLock = false;
            while (!isLock) {
                isLock = hlogPersistence.lockGroup(group.getGroupName());
                if (isLock) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(getThreadName() + " scan lock group[" + group.getGroupName() + "]");
                    }
                    break;
                }
                Thread.sleep(LOCKTIME);
            }

            if (isLock) {
                try {
                    hlogPersistence.updateGroup(group, true);
                    if (LOG.isInfoEnabled()) {
                        LOG.info(getThreadName() + " scan update group[" + group.getGroupName() + "]");
                    }
                } finally {
                    hlogPersistence.unlockGroup(group.getGroupName());
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(getThreadName() + " scan unlock group[" + group.getGroupName() + "]");
                    }
                }
            }

        }
    }

    protected void putHLog(GEMap groups) throws IOException {
        groups.put(hbaseService.getAllHLogs());
    }

    // **********************************
    // for old log scan optimization
    // **********************************
    protected long    scanOldHlogTimeOut;
    protected boolean hasScanOldHLog = false;

    protected void putOldHLog(GEMap groups) throws IOException {
        boolean isOpt = false;
        if (isOpt) {
            // need to scan oldlogs ?

            // Stat stat = zoo.exists(scanBasePath, false);
            // byte[] tstTmp = zoo.getData(scanBasePath, false, stat);
            // long lastScanTst = -1;
            // if (tstTmp != null) {
            // lastScanTst = Bytes.toLong(tstTmp, -1);
            // }

            // yes, scan oldlogs

            // if (lastScanTst == -1 || lastScanTst + scanOldHlogTimeOut >= System.currentTimeMillis()) {
            // groups.put(HLogUtil.getHLogsByHDFS(fs, oldHlogPath)); }

            // if (!hasScanOldHLog) {
            // groups.put(hbaseService.getAllOldHLogs());
            // hasScanOldHLog = true;
            // }
        } else {
            groups.put(hbaseService.getAllOldHLogs());
        }
    }

    public byte[] getLastScanTime() {
        return Bytes.toBytes(System.currentTimeMillis());
    }

    @Override
    public void deamon() throws Exception {
        doScan();
    }

    public static HLogScanner newInstance(AdvZooKeeper zk, ZNode root, HLogPersistence hlog, HBaseService hb,
                                          long tryLockTime, long onceSleepTime) {
        HLogScanner scaner = new HLogScanner(zk, root, tryLockTime, onceSleepTime);
        scaner.setHLogPersistence(hlog);
        scaner.setHBaseService(hb);
        return scaner;
    }
}
