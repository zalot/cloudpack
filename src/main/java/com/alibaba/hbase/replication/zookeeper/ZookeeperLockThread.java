package com.alibaba.hbase.replication.zookeeper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.NodeExistsException;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;

/**
 * 提供 Zookeeper Lock 支持 类ZookeeperLockThread.java的实现描述：TODO 类实现描述
 * 
 * @author zalot.zhaoh Mar 14, 2012 4:28:37 PM
 */
public abstract class ZookeeperLockThread implements Runnable {

    public abstract void doRun();

    protected static final Log     LOG        = LogFactory.getLog(ZookeeperLockThread.class);
    protected String               name;
    protected int                  errorCount = 0;
    // 休息时间
    // 争抢到 reject scanner 后 间隔时间
    
    // reject scanner 争抢重试时间
    protected boolean              isLock     = false;
    protected boolean              init       = false;

    protected RecoverableZooKeeper zooKeeper;
    protected ZookeeperLock        lock;

    public ZookeeperLockThread(ZookeeperLock lock){
        this.lock = lock;
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

    public boolean lock() throws KeeperException, InterruptedException {
        Stat stat = zooKeeper.exists(lock.getLockPath(), false);
        if (stat != null) {
            return false;
        }
        zooKeeper.create(lock.getLockPath(), null, Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        return true;
    }

    private void unlock() {
        try {
            Stat stat = zooKeeper.exists(lock.getLockPath(), false);
            if (stat != null) zooKeeper.delete(lock.getLockPath(), stat.getVersion());
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
                Thread.sleep(lock.getTryLockTime());
                isLock = lock();
                if (isLock) {
                    doRun();
                }
            } catch (Exception e) {
                e.printStackTrace();
                isLock = false;
                LOG.error(e);
            } finally {
                if (isLock) {
                    unlock();
                }
            }
        }
    }
}
