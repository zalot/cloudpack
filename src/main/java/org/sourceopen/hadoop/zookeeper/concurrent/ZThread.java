package org.sourceopen.hadoop.zookeeper.concurrent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.zookeeper.ZooKeeper;
import org.sourceopen.hadoop.hbase.replication.utility.HLogUtil;
import org.sourceopen.hadoop.zookeeper.connect.AdvZooKeeper;
import org.sourceopen.hadoop.zookeeper.core.ZNode;

/**
 * 提供 Zookeeper Thread <BR>
 * 类ZookeeperLockThread.java的实现描述：TODO 类实现描述
 * 
 * @author zalot.zhaoh Mar 14, 2012 4:28:37 PM
 */
public abstract class ZThread extends Thread {

    protected static final Log LOG        = LogFactory.getLog(ZThread.class);

    protected int              errorCount = 0;
    // 休息时间
    // 争抢到 reject scanner 后 间隔时间
    // reject scanner 争抢重试时间
    protected boolean          isLock     = false;
    protected boolean          init       = false;
    protected AdvZooKeeper     zk;
    protected ZNode            baseNode;

    public ZThread(ZNode baseNode, long retryTime, long sleepTime, boolean isDemaon){
        this.baseNode = baseNode;
    }

    public AdvZooKeeper getZooKeeper() {
        return zk;
    }

    public void setAdvZooKeeper(AdvZooKeeper zooKeeper) {
        this.zk = zooKeeper;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(lock.getRetrytime());
                if (LOG.isDebugEnabled()) LOG.debug(getThreadName() + " try lock ....");
                isLock = lock.lock();
                if (isLock) {
                    if (LOG.isInfoEnabled()) LOG.info(getThreadName() + " lock ....");
                    innnerRun();
                }
            } catch (Exception e) {
                isLock = false;
                LOG.error(getThreadName() + " error ....", e);
            } finally {
                if (isLock) {
                    if (lock.unlock()) {
                        if (LOG.isDebugEnabled()) LOG.debug(getThreadName() + " unlock ....");
                    }
                }
            }
        }
    }

    public void innnerRun() throws Exception {
        while (true) {
            Thread.sleep(lock.getSleeptime());
            doRun();
        }
    }

    public abstract void doRun() throws Exception;

    public String getThreadName() {
        return HLogUtil.getBaseInfo(this);
    }
}
