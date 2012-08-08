package org.sourceopen.hadoop.zookeeper.connect;

import java.io.IOException;

import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

public class ZooKeeperProxy extends ZooKeeper implements AdvZooKeeper {
    
    public ZooKeeperProxy(String connectString, int sessionTimeout, Watcher watcher) throws IOException{
        super(connectString, sessionTimeout, watcher);
    }

    @Override
    public ZooKeeper getZooKeeper() {
        return this;
    }
}
