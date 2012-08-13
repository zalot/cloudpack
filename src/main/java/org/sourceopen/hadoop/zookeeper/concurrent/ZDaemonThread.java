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
public abstract class ZDaemonThread extends ZThread {

    protected static final Log LOG       = LogFactory.getLog(ZDaemonThread.class);
    protected boolean          isRunning = false;
    protected boolean          run       = true;
    protected boolean          yield     = false;
    protected CallBack         stopped   = null;
    protected long             tryLockTime;
    protected long             onceSleepTime;

    public ZDaemonThread(AdvZooKeeper zk, long tryLockTime, long onceSleepTime){
        this(zk, null, tryLockTime, onceSleepTime);
    }

    public ZDaemonThread(AdvZooKeeper zk, ZNode znode, long tryLockTime, long onceSleepTime){
        this(zk, znode, null, tryLockTime, onceSleepTime);
    }

    public ZDaemonThread(AdvZooKeeper zk, ZNode znode, String lockName, long tryLockTime, long onceSleepTime){
        super(zk, znode, lockName);
        this.tryLockTime = tryLockTime;
        this.onceSleepTime = onceSleepTime;
    }

    @Override
    public void run() {
        while (run) {
            try {
                Thread.sleep(this.tryLockTime);
                if (LOG.isDebugEnabled()) LOG.debug(getThreadName() + " try lock ....");
                isLock = ZNodeHelper.lock(zoo, znode, lockName);
                if (isLock) {
                    if (LOG.isInfoEnabled()) LOG.info(getThreadName() + " lock ....");
                    isRunning = true;
                    thread();
                } else {
                    yield = false;
                }
            } catch (Exception e) {
                isLock = false;
                LOG.error(getThreadName() + " error ....", e);
            } finally {
                if (isLock) {
                    if (ZNodeHelper.unlock(zoo, znode, lockName)) {
                        if (LOG.isInfoEnabled()) LOG.info(getThreadName() + " unlock ....");
                    }
                }
                isRunning = false;
            }
        }
        if (stopped != null) stopped.stopped();
    }

    public static interface CallBack {

        public void stopped();
    }

    public void thread() throws Exception {
        while (run) {
            Thread.sleep(this.onceSleepTime);
            if (!yield) deamon();
            else break;
        }
    }

    public abstract void deamon() throws Exception;

    public String getThreadName() {
        return HLogUtil.getBaseInfo(this);
    }

    public void yieldzt() {
        yield = true;
    }

    public void shutdownzt() {
        shutdownzt(null);
    }

    public void shutdownzt(CallBack cb) {
        if (this.isRunning) {
            run = false;
            this.stopped = cb;
        }
    }

    public boolean isRunning() {
        return isRunning;
    }
}
