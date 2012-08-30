package org.sourceopen.hadoop.zookeeper.concurrent.locks;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import org.sourceopen.hadoop.zookeeper.connect.AdvZooKeeper;
import org.sourceopen.hadoop.zookeeper.core.ZNode;
import org.sourceopen.hadoop.zookeeper.core.ZNodeHelper;

public class ZkLock implements Lock, java.io.Serializable {

    private static final long     serialVersionUID = -9021809041854096738L;
    protected static AdvZooKeeper zk;
    static {
        zk = initStaticZooKeeper();
    }

    private static AdvZooKeeper initStaticZooKeeper() {
        return null;
    }

    private static void checkAdvZooKeeper() {

    }

    protected static ThreadLocal<String> uid = new ThreadLocal<String>();
    protected ZNode                      znode;

    public ZkLock(ZNode node){
        this.znode = node;
    }

    public void lock() {
        checkAdvZooKeeper();
        boolean isLock = ZNodeHelper.lock(zk, znode, znode.getName());
        if (!isLock) {
            Thread.currentThread().interrupt();
            new Thread();
        }
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {

    }

    @Override
    public boolean tryLock() {
        return false;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public void unlock() {

    }

    @Override
    public Condition newCondition() {
        return null;
    }

}
