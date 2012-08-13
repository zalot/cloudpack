package org.sourceopen.hadoop.zookeeper.concurrent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sourceopen.hadoop.hbase.replication.utility.HLogUtil;
import org.sourceopen.hadoop.zookeeper.connect.AdvZooKeeper;
import org.sourceopen.hadoop.zookeeper.core.ZNode;
import org.sourceopen.hadoop.zookeeper.core.ZNodeHelper;

/**
 * 提供 Zookeeper Thread <BR>
 * 类ZookeeperLockThread.java的实现描述：TODO 类实现描述
 * 
 * @author zalot.zhaoh Mar 14, 2012 4:28:37 PM
 */
public abstract class ZThread extends Thread {

    protected static final Log LOG      = LogFactory.getLog(ZThread.class);

    // 休息时间
    // 争抢到 reject scanner 后 间隔时间
    // reject scanner 争抢重试时间
    protected boolean          isLock   = false;
    protected AdvZooKeeper     zoo      = null;
    protected ZNode            znode    = null;
    protected String           lockName = null;

    public ZThread(AdvZooKeeper zk, ZNode baseNode, String lockName){
        this.znode = baseNode;
        this.zoo = zk;
        this.lockName = lockName;
    }

    public AdvZooKeeper getAdvZooKeeper() {
        return zoo;
    }

    public void setAdvZooKeeper(AdvZooKeeper zooKeeper) {
        this.zoo = zooKeeper;
    }

    @Override
    public void run() {
        try {
            if (LOG.isDebugEnabled()) LOG.debug(HLogUtil.getBaseInfo(this) + " try lock ....");
            isLock = ZNodeHelper.lock(zoo, znode, lockName);
            if (isLock) {
                if (LOG.isInfoEnabled()) LOG.info(HLogUtil.getBaseInfo(this) + " lock ....");
                thread();
            }
        } catch (Exception e) {
            isLock = false;
            LOG.error(HLogUtil.getBaseInfo(this) + " run - error ....", e);
        } finally {
            if (isLock) {
                if (ZNodeHelper.unlock(zoo, znode, lockName)) {
                    if (LOG.isDebugEnabled()) LOG.debug(HLogUtil.getBaseInfo(this) + " unlock ....");
                }
            }
        }
    }

    public abstract void thread() throws Exception;

    public void shutdown() {
        if (this.isAlive()) {
        }
    }
}
