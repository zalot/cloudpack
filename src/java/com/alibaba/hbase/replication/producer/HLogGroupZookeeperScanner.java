package com.alibaba.hbase.replication.producer;

import java.io.IOException;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.zookeeper.RecoverableZooKeeper;
import org.apache.hadoop.hbase.zookeeper.ZKUtil;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.NodeExistsException;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;

import com.alibaba.hbase.replication.hlog.domain.HLogEntryGroup;
import com.alibaba.hbase.replication.hlog.domain.HLogEntryGroups;
import com.alibaba.hbase.replication.persistence.HLogPersistence;
import com.alibaba.hbase.replication.utility.AliHBaseConstants;
import com.alibaba.hbase.replication.utility.HLogUtil;
import com.alibaba.hbase.replication.zookeeper.ReplicationZookeeperWatch;

/**
 * HLogGroup 扫描器<BR>
 * 类HLogGroupZookeeperScanner.java的实现描述：TODO 类实现描述
 * 
 * @author zalot.zhaoh Mar 1, 2012 10:44:45 AM
 */
public class HLogGroupZookeeperScanner extends Thread {

    protected static final Log     LOG            = LogFactory.getLog(HLogGroupZookeeperScanner.class);
    protected String               name;
    protected String               scanBasePath;
    protected String               scanLockPath;
    protected int                  errorCount     = 0;
    // 休息时间
    // 争抢到 scanner 后 间隔时间
    protected long                 flushSleepTime;

    // scanner 争抢重试时间
    protected long                 scannerTryLockTime;
    protected Path                 hlogPath;
    protected Path                 oldHlogPath;
    protected boolean              isLock         = false;

    protected HLogPersistence      hlogDAO;
    protected FileSystem           fs;
    protected RecoverableZooKeeper zoo;
    protected long                 scanOldHlogTimeOut;
    protected Configuration conf;

    protected boolean              hasScanOldHLog = false;

    public Path getHlogPath() {
        return hlogPath;
    }

    public void setHlogPath(Path hlogPath) {
        this.hlogPath = hlogPath;
    }

    public Path getOldHlogPath() {
        return oldHlogPath;
    }

    public void setOldHlogPath(Path oldHlogPath) {
        this.oldHlogPath = oldHlogPath;
    }

    public HLogGroupZookeeperScanner(Configuration conf, FileSystem fs, HLogPersistence dao) throws KeeperException,
                                                                                            InterruptedException,
                                                                                            IOException{
        this(UUID.randomUUID().toString(), conf, fs, dao);
    }

    public HLogGroupZookeeperScanner(String name, Configuration conf, FileSystem fs, HLogPersistence dao)
                                                                                                         throws KeeperException,
                                                                                                         InterruptedException,
                                                                                                         IOException{
        zoo = ZKUtil.connect(conf, new ReplicationZookeeperWatch());
        this.fs = fs;
        this.hlogDAO = dao;
        this.name = name;
        this.conf = conf;
        init(conf);
    }

    private void init(Configuration conf) throws KeeperException, InterruptedException {
        scanBasePath = conf.get(AliHBaseConstants.CONFKEY_ZOO_SCAN_ROOT, AliHBaseConstants.ZOO_SCAN_ROOT);
        scanLockPath = scanBasePath + AliHBaseConstants.ZOO_SCAN_LOCK;
        flushSleepTime = conf.getLong(AliHBaseConstants.CONFKEY_ZOO_SCAN_LOCK_FLUSHSLEEPTIME,
                                      AliHBaseConstants.ZOO_SCAN_LOCK_FLUSHSLEEPTIME);
        scannerTryLockTime = conf.getLong(AliHBaseConstants.CONFKEY_ZOO_SCAN_LOCK_TRYLOCKTIME,
                                          AliHBaseConstants.ZOO_SCAN_LOCK_TRYLOCKTIME);
        scanOldHlogTimeOut = conf.getLong(AliHBaseConstants.CONFKEY_ZOO_SCAN_OLDHLOG_TIMEOUT,
                                          AliHBaseConstants.ZOO_SCAN_OLDHLOG_TIMEOUT);

        hlogPath = new Path(conf.get("hbase.rootdir") + "/" + AliHBaseConstants.PATH_BASE_HLOG);
        oldHlogPath = new Path(conf.get("hbase.rootdir") + "/" + AliHBaseConstants.PATH_BASE_OLDHLOG);
        Stat stat = zoo.exists(scanBasePath, false);
        if (stat == null) {
            try {
                zoo.create(scanBasePath, null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            } catch (NodeExistsException e) {
            }
        }
    }

    public boolean lock() throws KeeperException, InterruptedException {
        Stat stat = zoo.exists(scanLockPath, false);
        if (stat != null) {
            return false;
        }
        zoo.create(scanLockPath, null, Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        return true;
    }

    private void unlock() {
        try {
            Stat stat = zoo.exists(scanLockPath, false);
            if (stat != null) zoo.delete(scanLockPath, stat.getVersion());
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
                isLock = false;
                LOG.error(e);
                errorCount++;
                // 如果 超过 3次错误则释放 lock
                if (errorCount > 3) {
                    reinizoo();
                }
            } finally {
                if (isLock) {
                    unlock();
                }
            }
        }
    }

    private void reinizoo() {
        if (zoo != null) {
            try {
                zoo.close();
                zoo = ZKUtil.connect(conf, new ReplicationZookeeperWatch());
            } catch (Exception e) {
                LOG.error(e.getMessage());
            }
        }
    }

    private void scanning() throws Exception {
        while (true) {
            // TODO : 如果 flushSleepTime 这段时间内有 Hlog - > OldHlog 那么就要更换策略
            // 所以常情保持 Hlog 的数量和大小，确保 在 flushSleepTime 时间段内， Hlog 一直都在 .logs 目录中
            Thread.sleep(flushSleepTime);
            LOG.debug(Thread.currentThread().getName() + " scanning ....");
            HLogEntryGroups groups = new HLogEntryGroups();
            putHLog(groups);
            putOldHLog(groups);
            for (HLogEntryGroup group : groups.getGroups()) {
                hlogDAO.createOrUpdateGroup(group, true);
            }
            LOG.debug(Thread.currentThread().getName() + " scanning ok");
        }
    }

    protected void putHLog(HLogEntryGroups groups) throws IOException {
        groups.put(HLogUtil.getHLogsByHDFS(fs, hlogPath));
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
            groups.put(HLogUtil.getHLogsByHDFS(fs, oldHlogPath));
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
