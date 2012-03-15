package com.alibaba.hbase.replication.producer;

import java.io.IOException;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.NodeExistsException;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;

import com.alibaba.hbase.replication.hlog.HLogEntryPersistence;
import com.alibaba.hbase.replication.hlog.HLogService;
import com.alibaba.hbase.replication.hlog.domain.HLogEntryGroup;
import com.alibaba.hbase.replication.hlog.domain.HLogEntryGroups;
import com.alibaba.hbase.replication.utility.ProducerConstants;
import com.alibaba.hbase.replication.utility.ZKUtil;
import com.alibaba.hbase.replication.zookeeper.NothingZookeeperWatch;
import com.alibaba.hbase.replication.zookeeper.RecoverableZooKeeper;

/**
 * HLogGroup 扫描器<BR>
 * 类HLogGroupZookeeperScanner.java的实现描述：TODO 类实现描述
 * 
 * @author zalot.zhaoh Mar 1, 2012 10:44:45 AM
 */
public class HLogGroupZookeeperScanner implements Runnable {

    protected static final Log     LOG            = LogFactory.getLog(HLogGroupZookeeperScanner.class);
    protected String               name;
    protected String               zooScanBasePath;
    protected String               zooScanLockPath;

    protected int                  errorCount     = 0;
    // 休息时间
    // 争抢到 scanner 后 间隔时间
    protected long                 flushSleepTime;
    // scanner 争抢重试时间
    protected long                 scannerTryLockTime;
    protected boolean              isLock         = false;

    protected long                 scanOldHlogTimeOut;
    protected boolean              hasScanOldHLog = false;
    protected boolean              init           = false;
    protected HLogService          hlogService;
    protected RecoverableZooKeeper zooKeeper;

    // 外部对象引用
    protected HLogEntryPersistence hlogEntryPersistence;

    public HLogEntryPersistence getHlogEntryPersistence() {
        return hlogEntryPersistence;
    }

    public void setHlogEntryPersistence(HLogEntryPersistence hlogEntryPersistence) {
        this.hlogEntryPersistence = hlogEntryPersistence;
    }

    public HLogService getHlogService() {
        return hlogService;
    }

    public void setHlogService(HLogService hlogService) {
        this.hlogService = hlogService;
    }

    public RecoverableZooKeeper getZooKeeper() {
        return zooKeeper;
    }

    public void setZooKeeper(RecoverableZooKeeper zooKeeper) {
        this.zooKeeper = zooKeeper;
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
        if (zoo == null) zoo = ZKUtil.connect(conf, new NothingZookeeperWatch());
        this.zooKeeper = zoo;
        this.name = name;
        init(conf);
    }

    public void init(Configuration conf) throws KeeperException, InterruptedException {
        zooScanBasePath = conf.get(ProducerConstants.CONFKEY_ZOO_LOCK_ROOT,
                                                    ProducerConstants.ZOO_LOCK_ROOT);
        zooScanLockPath = zooScanBasePath + ProducerConstants.ZOO_LOCK_SCAN;
        flushSleepTime = conf.getLong(ProducerConstants.CONFKEY_ZOO_SCAN_LOCK_FLUSHSLEEPTIME,
                                                       ProducerConstants.ZOO_SCAN_LOCK_FLUSHSLEEPTIME);
        scannerTryLockTime = conf.getLong(ProducerConstants.CONFKEY_ZOO_SCAN_LOCK_RETRYTIME,
                                                           ProducerConstants.ZOO_SCAN_LOCK_RETRYTIME);
        scanOldHlogTimeOut = conf.getLong(ProducerConstants.CONFKEY_ZOO_SCAN_OLDHLOG_INTERVAL,
                                                           ProducerConstants.ZOO_SCAN_OLDHLOG_INTERVAL);

        Stat stat = zooKeeper.exists(zooScanBasePath, false);
        if (stat == null) {
            try {
                zooKeeper.create(zooScanBasePath, null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            } catch (NodeExistsException e) {
            }
        }
        init = true;
    }

    public boolean lock() throws KeeperException, InterruptedException {
        Stat stat = zooKeeper.exists(zooScanLockPath, false);
        if (stat != null) {
            return false;
        }
        zooKeeper.create(zooScanLockPath, null, Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        return true;
    }

    private void unlock() {
        try {
            Stat stat = zooKeeper.exists(zooScanLockPath, false);
            if (stat != null) zooKeeper.delete(zooScanLockPath, stat.getVersion());
        } catch (Exception e) {
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                LOG.debug("Scanner Start ....");
                Thread.sleep(scannerTryLockTime);
                isLock = lock();
                if (isLock) {
                    scanning();
                }
            } catch (Exception e) {
                e.printStackTrace();
                isLock = false;
                LOG.error(e);
                errorCount++;
                // 如果 超过 3次错误则释放 lock
                if (errorCount > 3) {
                    reinitZookeeper();
                }
            } finally {
                if (isLock) {
                    unlock();
                }
            }
        }
    }

    private void reinitZookeeper() {
        if (zooKeeper != null) {
            try {
                zooKeeper.close();
                zooKeeper = ZKUtil.connect(hlogService.getConf(), new NothingZookeeperWatch());
            } catch (Exception e) {
                LOG.error(e.getMessage());
            }
        }
    }

    protected void scanning() throws Exception {
        while (true) {
            // TODO : 如果 flushSleepTime 这段时间内有 Hlog - > OldHlog 那么就要更换策略
            // 所以常情保持 Hlog 的数量和大小，确保 在 flushSleepTime 时间段内， Hlog 一直都在 .logs 目录中
            Thread.sleep(flushSleepTime);
            doScan();
        }
    }

    public void doScan() throws Exception {
        HLogEntryGroups groups = new HLogEntryGroups();
        putHLog(groups);
        putOldHLog(groups);
        for (HLogEntryGroup group : groups.getGroups()) {
            hlogEntryPersistence.createOrUpdateGroup(group, true);
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

    public long getFlushSleepTime() {
        return flushSleepTime;
    }

    public byte[] getLastScanTime() {
        return Bytes.toBytes(System.currentTimeMillis());
    }
}
