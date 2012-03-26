package com.alibaba.hbase.replication.producer;

import java.io.IOException;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.zookeeper.KeeperException;

import com.alibaba.hbase.replication.hlog.HLogEntryPoolPersistence;
import com.alibaba.hbase.replication.hlog.HLogService;
import com.alibaba.hbase.replication.hlog.domain.HLogEntry;
import com.alibaba.hbase.replication.hlog.domain.HLogEntryGroup;
import com.alibaba.hbase.replication.hlog.domain.HLogEntryGroups;
import com.alibaba.hbase.replication.utility.ProducerConstants;
import com.alibaba.hbase.replication.zookeeper.RecoverableZooKeeper;
import com.alibaba.hbase.replication.zookeeper.ZookeeperLock;
import com.alibaba.hbase.replication.zookeeper.ZookeeperSingleLockThread;

/**
 * HLogGroup 扫描器<BR>
 * 类HLogGroupZookeeperScanner.java的实现描述：TODO 类实现描述
 * 
 * @author zalot.zhaoh Mar 1, 2012 10:44:45 AM
 */
public class HLogGroupZookeeperScanner extends ZookeeperSingleLockThread {

    protected static final Log         LOG            = LogFactory.getLog(HLogGroupZookeeperScanner.class);
    protected String                   name;
    protected long                     scanOldHlogTimeOut;
    protected boolean                  hasScanOldHLog = false;
    protected HLogService              hlogService;

    // 外部对象引用
    protected HLogEntryPoolPersistence hlogEntryPersistence;

    public HLogEntryPoolPersistence getHlogEntryPersistence() {
        return hlogEntryPersistence;
    }

    public void setHlogEntryPersistence(HLogEntryPoolPersistence hlogEntryPersistence) {
        this.hlogEntryPersistence = hlogEntryPersistence;
    }

    public HLogService getHlogService() {
        return hlogService;
    }

    public void setHlogService(HLogService hlogService) {
        this.hlogService = hlogService;
    }

    public HLogGroupZookeeperScanner(Configuration conf) throws KeeperException, InterruptedException, IOException{
        this(UUID.randomUUID().toString(), conf, null);
    }

    public HLogGroupZookeeperScanner(Configuration conf, RecoverableZooKeeper zoo) throws KeeperException,
                                                                                  InterruptedException, IOException{
        this(UUID.randomUUID().toString(), conf, zoo);
    }

    public HLogGroupZookeeperScanner(String name, Configuration conf, RecoverableZooKeeper zoo) throws KeeperException,
                                                                                               InterruptedException,
                                                                                               IOException{
        this.zooKeeper = zoo;
        this.name = name;
        init(conf);
    }

    public void init(Configuration conf) throws KeeperException, InterruptedException {
        ZookeeperLock lock = new ZookeeperLock();
        lock.setBasePath(conf.get(ProducerConstants.CONFKEY_ZOO_LOCK_ROOT, ProducerConstants.ZOO_LOCK_ROOT));
        lock.setLockPath(ProducerConstants.ZOO_LOCK_SCAN);
        lock.setSleepTime(conf.getLong(ProducerConstants.CONFKEY_ZOO_SCAN_LOCK_FLUSHSLEEPTIME,
                                       ProducerConstants.ZOO_SCAN_LOCK_FLUSHSLEEPTIME));
        lock.setTryLockTime(conf.getLong(ProducerConstants.CONFKEY_ZOO_SCAN_LOCK_RETRYTIME,
                                         ProducerConstants.ZOO_SCAN_LOCK_RETRYTIME));
        this.setLock(lock);
    }

    @Override
    public void doRun() throws Exception {
        doScan();
    }

    public void doScan() throws Exception {
        HLogEntryGroups groups = new HLogEntryGroups();
        putHLog(groups);
        putOldHLog(groups);

        // 清理 group
        for (String groupStr : hlogEntryPersistence.listGroupName()) {
            if (groups.get(groupStr) == null) {
                hlogEntryPersistence.deleteGroup(groupStr);
            }
        }

        // 清理 entry

        for (HLogEntryGroup group : groups.getGroups()) {
            HLogEntryGroup tmpGroup = hlogEntryPersistence.getGroupByName(group.getGroupName(), false);
            if (tmpGroup == null) {
                hlogEntryPersistence.createGroup(group, true);
            } else {
                for (HLogEntry entry : hlogEntryPersistence.listEntry(group.getGroupName())) {
                    if(!group.contains(entry)){
                        hlogEntryPersistence.deleteEntry(entry);
                    }
                }
                hlogEntryPersistence.updateGroup(group, true);
            }
        }
    }

    protected void putHLog(HLogEntryGroups groups) throws IOException {
        groups.put(hlogService.getAllHLogs());
    }

    protected void putOldHLog(HLogEntryGroups groups) throws IOException {
        // need to scan oldlogs ?
        /*
         * Stat stat = zoo.exists(scanBasePath, false); byte[] tstTmp = zoo.getData(scanBasePath, false, stat); long
         * lastScanTst = -1; if (tstTmp != null) { lastScanTst = Bytes.toLong(tstTmp, -1); }
         */
        // yes, scan oldlogs
        /*
         * if (lastScanTst == -1 || lastScanTst + scanOldHlogTimeOut >= System.currentTimeMillis()) {
         * groups.put(HLogUtil.getHLogsByHDFS(fs, oldHlogPath)); }
         */
        if (!hasScanOldHLog) {
            groups.put(hlogService.getAllOldHLogs());
            hasScanOldHLog = true;
        }
    }

    public byte[] getLastScanTime() {
        return Bytes.toBytes(System.currentTimeMillis());
    }

}
