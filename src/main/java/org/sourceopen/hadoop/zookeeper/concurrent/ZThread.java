package org.sourceopen.hadoop.zookeeper.concurrent;

import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.NodeExistsException;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.sourceopen.hadoop.hbase.replication.utility.HLogUtil;

/**
 * 提供 Zookeeper Thread <BR>
 * 类ZookeeperLockThread.java的实现描述：TODO 类实现描述
 * 
 * @author zalot.zhaoh Mar 14, 2012 4:28:37 PM
 */
public abstract class ZThread extends Thread {

    protected static final Log    LOG        = LogFactory.getLog(ZThread.class);
    protected ThreadLocal<String> threadUUID = new ThreadLocal<String>();
    protected int                 errorCount = 0;
    // 休息时间
    // 争抢到 reject scanner 后 间隔时间
    // reject scanner 争抢重试时间
    protected boolean             isLock     = false;
    protected boolean             init       = false;
    protected ZooKeeper           zk;
    protected ZLock               zlock;

    public ZLock getLock() {
        return zlock;
    }

    public void setLock(ZLock lock) {
        this.zlock = lock;
    }

    public String getUuid() {
        if (threadUUID.get() == null) {
            threadUUID.set(UUID.randomUUID().toString());
        }
        return threadUUID.get();
    }

    public ZooKeeper getZooKeeper() {
        return zk;
    }

    public void setZooKeeper(ZooKeeper zooKeeper) {
        this.zk = zooKeeper;
    }

    protected void init() throws KeeperException, InterruptedException {
        Stat stat = zk.exists(zlock.getBasePath(), false);
        if (stat == null) {
            try {
                zk.create(zlock.getBasePath(), null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            } catch (NodeExistsException e) {
            } catch (Exception oe) {
                init = false;
            }
        }
        init = true;
    }

    public boolean lock() {
        try {
            Stat stat = zk.exists(zlock.getLockPath(), false);
            if (stat != null) {
                return false;
            }
            zk.create(zlock.getLockPath(), getLockData(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
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
            Stat stat = zk.exists(zlock.getLockPath(), false);
            if (stat != null) {
                String uuid = setLockData(zk.getData(zlock.getLockPath(), false, stat));
                if (getUuid().equals(uuid)) {
                    zk.delete(zlock.getLockPath(), stat.getVersion());
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
                Thread.sleep(zlock.getTryLockTime());
                if (!init) {
                    init();
                }
                if (LOG.isDebugEnabled()) LOG.debug(getJobName() + " try lock ....");
                isLock = lock();
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
                        if (LOG.isDebugEnabled()) LOG.debug(getJobName() + " unlock ....");
                    }
                }
            }
        }
    }

    public void innnerRun() throws Exception {
        while (true) {
            Thread.sleep(zlock.getSleepTime());
            doRun();
        }
    }

    public abstract void doRun() throws Exception;

    public String getJobName() {
        return HLogUtil.getBaseInfo(this);
    }
}
