package com.alibaba.hbase.replication.producer.crossidc;

import java.io.IOException;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.regionserver.wal.HLog.Entry;
import org.apache.hadoop.hbase.zookeeper.RecoverableZooKeeper;
import org.apache.hadoop.hbase.zookeeper.ZKUtil;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.NodeExistsException;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;

import com.alibaba.hbase.replication.hlog.HLogService;
import com.alibaba.hbase.replication.hlog.domain.HLogEntry;
import com.alibaba.hbase.replication.hlog.reader.HLogReader;
import com.alibaba.hbase.replication.protocol.Body;
import com.alibaba.hbase.replication.protocol.FileAdapter;
import com.alibaba.hbase.replication.protocol.Head;
import com.alibaba.hbase.replication.protocol.ProtocolAdapter;
import com.alibaba.hbase.replication.protocol.Version1;
import com.alibaba.hbase.replication.utility.HLogUtil;
import com.alibaba.hbase.replication.utility.ProducerConstants;
import com.alibaba.hbase.replication.zookeeper.NothingZookeeperWatch;

/**
 * HLogGroup 扫描器<BR>
 * 类HLogGroupZookeeperScanner.java的实现描述：TODO 类实现描述
 * 
 * @author zalot.zhaoh Mar 1, 2012 10:44:45 AM
 */
public class HBaseReplicationRejectScanner implements Runnable {

    protected static final Log     LOG            = LogFactory.getLog(HBaseReplicationRejectScanner.class);
    protected String               name;
    protected String               zooScanBasePath;
    protected String               zooRejectScanLockPath;
    protected Path                 dfsRejectHLogPath;

    protected int                  errorCount     = 0;
    // 休息时间
    // 争抢到 reject scanner 后 间隔时间
    protected long                 rejectFlushSleepTime;
    // reject scanner 争抢重试时间
    protected long                 scannerTryLockTime;
    protected boolean              isLock         = false;
    protected boolean              init           = false;

    protected HLogService          hlogService;
    protected RecoverableZooKeeper zooKeeper;
    protected ProtocolAdapter      adapter;

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

    public HBaseReplicationRejectScanner(Configuration conf) throws KeeperException, InterruptedException, IOException{
        this(UUID.randomUUID().toString(), conf, null);
    }

    public HBaseReplicationRejectScanner(Configuration conf, RecoverableZooKeeper zoo) throws KeeperException,
                                                                                      InterruptedException, IOException{
        this(UUID.randomUUID().toString(), conf, zoo);
    }

    public HBaseReplicationRejectScanner(String name, Configuration conf, RecoverableZooKeeper zoo)
                                                                                                   throws KeeperException,
                                                                                                   InterruptedException,
                                                                                                   IOException{
        if (zoo == null) zoo = ZKUtil.connect(conf, new NothingZookeeperWatch());
        this.zooKeeper = zoo;
        this.name = name;
    }

    private void init() throws KeeperException, InterruptedException {
        zooScanBasePath = hlogService.getConf().get(ProducerConstants.CONFKEY_ZOO_SCAN_ROOT,
                                                    ProducerConstants.ZOO_SCAN_ROOT);
        zooRejectScanLockPath = zooScanBasePath + ProducerConstants.ZOO_REJECT_SCAN_LOCK;

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
        Stat stat = zooKeeper.exists(zooRejectScanLockPath, false);
        if (stat != null) {
            return false;
        }
        zooKeeper.create(zooRejectScanLockPath, null, Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        return true;
    }

    private void unlock() {
        try {
            Stat stat = zooKeeper.exists(zooRejectScanLockPath, false);
            if (stat != null) zooKeeper.delete(zooRejectScanLockPath, stat.getVersion());
        } catch (Exception e) {
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (!init) {
                    init();
                }
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

    private void scanning() throws Exception {
        while (true) {
            // TODO : 如果 flushSleepTime 这段时间内有 Hlog - > OldHlog 那么就要更换策略
            // 所以常情保持 Hlog 的数量和大小，确保 在 flushSleepTime 时间段内， Hlog 一直都在 .logs 目录中
            Thread.sleep(rejectFlushSleepTime);
            LOG.debug(Thread.currentThread().getName() + " scanning ....");
            for (FileStatus fstat : hlogService.getFileSystem().listStatus(dfsRejectHLogPath)) {
                doPart(fstat);
            }
            LOG.debug(Thread.currentThread().getName() + " scanning ok");
        }
    }

    private void doPart(FileStatus fstat) {
        String fileName = fstat.getPath().getName();
        Head head = FileAdapter.validataFileName(fileName);

        HLogEntry entry = HLogUtil.getHLogEntryByHead(head);
        Entry ent = null;
        Body body = new Body();
        try {
            HLogReader reader = hlogService.getReader(entry);
            if (reader != null) {
                while ((ent = reader.next()) != null) {
                    HLogUtil.put2Body(ent, body);
                    if (reader.getPosition() == head.getEndOffset()) {
                        break;
                    }
                }
                if (doAdapter(head, body)) {

                }
                reader.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean doAdapter(Head head, Body body) {
        Version1 version1 = new Version1(head, body);
        try {
            adapter.write(version1);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
