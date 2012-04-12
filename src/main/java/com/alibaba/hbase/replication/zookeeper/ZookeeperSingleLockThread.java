package com.alibaba.hbase.replication.zookeeper;

import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.NodeExistsException;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;

import com.alibaba.hbase.replication.utility.HLogUtil;

/**
 * 提供 Zookeeper Lock 支持 <BR>
 * 类ZookeeperLockThread.java的实现描述：TODO 类实现描述
 * 
 * @author zalot.zhaoh Mar 14, 2012 4:28:37 PM
 */
public abstract class ZookeeperSingleLockThread implements Runnable {

    protected static final Log     LOG        = LogFactory.getLog(ZookeeperSingleLockThread.class);
    protected ThreadLocal<String>  uuid       = new ThreadLocal<String>();
    protected int                  errorCount = 0;
    // 休息时间
    // 争抢到 reject scanner 后 间隔时间

    // reject scanner 争抢重试时间
    protected boolean              isLock     = false;
    protected boolean              init       = false;

    protected RecoverableZooKeeper zooKeeper;
    protected ZookeeperLock        lock;

    public ZookeeperLock getLock() {
        return lock;
    }

    public void setLock(ZookeeperLock lock) {
        this.lock = lock;
    }

    public String getUuid() {
        if (uuid.get() == null) {
            uuid.set(UUID.randomUUID().toString());
        }
        return uuid.get();
    }

    public RecoverableZooKeeper getZooKeeper() {
        return zooKeeper;
    }

    public void setZooKeeper(RecoverableZooKeeper zooKeeper) {
        this.zooKeeper = zooKeeper;
    }

    protected void init() throws KeeperException, InterruptedException {
        Stat stat = zooKeeper.exists(lock.getBasePath(), false);
        if (stat == null) {
            try {
                zooKeeper.create(lock.getBasePath(), null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            } catch (NodeExistsException e) {
            }
        }
        init = true;
    }

    public boolean lock() {
        try {
            Stat stat = zooKeeper.exists(lock.getLockPath(), false);
            if (stat != null) {
                return false;
            }
            zooKeeper.create(lock.getLockPath(), getLockData(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            return true;
        } catch (Exception e) {
        }
        return false;
    }

    private byte[] getLockData() {
        return Bytes.toBytes(getUuid());
    }

    private String setLockData(byte[] data) {
        return Bytes.toString(data);
    }

    private boolean unlock() {
        try {
            Stat stat = zooKeeper.exists(lock.getLockPath(), false);
            if (stat != null) {
                String uuid = setLockData(zooKeeper.getData(lock.getLockPath(), false, stat));
                if (getUuid().equals(uuid)) {
                    zooKeeper.delete(lock.getLockPath(), stat.getVersion());
                }
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(lock.getTryLockTime());
                if (!init) {
                    init();
                }
                isLock = lock();
                if (LOG.isInfoEnabled()) LOG.info(getJobName() + " try lock ....");
                if (isLock) {
                    if (LOG.isInfoEnabled()) LOG.info(getJobName() + " lock ....");
                    innnerRun();
                }
            } catch (Exception e) {
                isLock = false;
                LOG.error(getJobName() + " error ....", e);
            } finally {
                if (isLock) {
                    if (unlock()) {
                        if (LOG.isInfoEnabled()) LOG.info(getJobName() + " unlock ....");
                    }
                }
            }
        }
    }

    public void innnerRun() throws Exception {
        while (true) {
            Thread.sleep(lock.getSleepTime());
            doRun();
        }
    }

    public abstract void doRun() throws Exception;

    public String getJobName() {
        return HLogUtil.getBaseInfo(this);
    }
}
