package com.alibaba.hbase.replication.producer;

import java.io.IOException;
import java.util.UUID;

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

    protected String               name;
    protected String               scanBasePath;
    protected String               scanLockPath;

    // 休息时间
    // 争抢到 scanner 后 间隔时间
    protected long                 sleepTime;

    // scanner 争抢重试时间
    protected long                 scannerTryLockTime;
    protected Path                 hlogPath;
    protected Path                 oldHlogPath;
    protected boolean              isLock = false;

    protected HLogPersistence      hlogDAO;
    protected FileSystem           fs;
    protected RecoverableZooKeeper zoo;

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
        this.fs = fs;
        this.hlogDAO = dao;
        zoo = ZKUtil.connect(conf, new ReplicationZookeeperWatch());
        this.name = name;
        init(conf);
    }

    private void init(Configuration conf) throws KeeperException, InterruptedException {
        String rootdir = "";
        scanBasePath = rootdir + conf.get(AliHBaseConstants.CONFKEY_ZOO_SCAN_ROOT, AliHBaseConstants.ZOO_SCAN_ROOT);
        scanLockPath = scanBasePath + AliHBaseConstants.ZOO_SCAN_LOCK;
        sleepTime = conf.getLong(AliHBaseConstants.CONFKEY_ZOO_SCAN_LOCK_SLEEPTIME,
                                 AliHBaseConstants.ZOO_SCAN_LOCK_SLEEPTIME);
        scannerTryLockTime = conf.getLong(AliHBaseConstants.CONFKEY_ZOO_SCAN_LOCK_RETRYTIME,
                                          AliHBaseConstants.ZOO_SCAN_LOCK_RETRYTIME);
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
                Thread.sleep(scannerTryLockTime);
                isLock = lock();
                if (isLock) {
                    scanning();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (isLock) {
                    unlock();
                }

            }
        }
    }

    private void scanning() throws Exception {
        while (true) {
            System.out.println(name + " scanning ..");
            HLogEntryGroups groups = new HLogEntryGroups();
            groups.put(HLogUtil.getHLogsByHDFS(fs, hlogPath));
            groups.put(HLogUtil.getHLogsByHDFS(fs, oldHlogPath));
            for (HLogEntryGroup group : groups.getGroups()) {
                hlogDAO.createOrUpdateGroup(group, true);
            }
            Thread.sleep(sleepTime);
        }
    }

    public long getSleepTime() {
        return sleepTime;
    }

    public byte[] getData() {
        return Bytes.toBytes(System.currentTimeMillis());
    }

    public void setData(byte[] data) {
        
    }
}
