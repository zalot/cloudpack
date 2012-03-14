package com.alibaba.hbase.replication.zookeeper;

public class ZookeeperLock {

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
