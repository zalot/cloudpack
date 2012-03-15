package com.alibaba.hbase.replication.zookeeper;

/**
 * Zookeeper 锁，用于 ZookeeperLockThread <BR>
 * 类ZookeeperLock.java的实现描述：TODO 类实现描述
 * 
 * @author zalot.zhaoh Mar 15, 2012 12:44:14 PM
 */
public class ZookeeperLock {

    public ZookeeperLock(String basePath, String lockPath, long sleepTime, long tryLockTime){
        super();
        this.basePath = basePath;
        this.lockPath = lockPath;
        this.sleepTime = sleepTime;
        this.tryLockTime = tryLockTime;
    }

    public ZookeeperLock(){
    };

    protected String basePath;

    protected String lockPath;

    protected long   sleepTime;

    protected long   tryLockTime;

    public String getBasePath() {
        return basePath;
    }

    public String getLockPath() {
        return basePath + lockPath;
    }

    public long getSleepTime() {
        return sleepTime;
    }

    public long getTryLockTime() {
        return tryLockTime;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public void setLockPath(String lockPath) {
        this.lockPath = lockPath;
    }

    public void setSleepTime(long sleepTime) {
        this.sleepTime = sleepTime;
    }

    public void setTryLockTime(long tryLockTime) {
        this.tryLockTime = tryLockTime;
    }
}
