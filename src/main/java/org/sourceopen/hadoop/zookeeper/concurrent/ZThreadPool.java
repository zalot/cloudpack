package org.sourceopen.hadoop.zookeeper.concurrent;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.sourceopen.hadoop.zookeeper.connect.AdvZooKeeper;
import org.sourceopen.hadoop.zookeeper.core.ZNode;

public class ZThreadPool {

    protected ThreadPoolExecutor exePool = null;
    protected ZNode              root;
    protected AdvZooKeeper       zk;

    public ZThreadPool(ZNode root, AdvZooKeeper zk, int corePoolSize, int maximumPoolSize, long keepAliveTime,
                       TimeUnit unit, BlockingQueue<Runnable> workQueue){
        exePool = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    public void execute(ZThread z) {
        exePool.execute(z);
    }
    
    public ThreadPoolExecutor getExecutor(){
        return this.exePool;
    }
}
