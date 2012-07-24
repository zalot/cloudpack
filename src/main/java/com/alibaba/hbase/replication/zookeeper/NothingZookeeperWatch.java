package com.alibaba.hbase.replication.zookeeper;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

/**
 */
public class NothingZookeeperWatch implements Watcher {

    // public ReplicationZookeeperWatch(Stoppable stop){
    //
    // }

    @Override
    public void process(WatchedEvent event) {
        // if(event.getState() == KeeperState.SyncConnected){
        //
        // }
    }

}
