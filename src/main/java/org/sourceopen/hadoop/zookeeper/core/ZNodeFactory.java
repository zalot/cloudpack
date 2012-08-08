package org.sourceopen.hadoop.zookeeper.core;

import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.data.ACL;
import org.sourceopen.hadoop.zookeeper.connect.AdvZooKeeper;

public class ZNodeFactory {

    protected static AdvZooKeeper zk;

    public static void setAdvZooKeeper(AdvZooKeeper zk) {
        ZNodeFactory.zk = zk;
    }

    public static ZNode createZNode(AdvZooKeeper zk, ZNode parent, String name, List<ACL> ids, CreateMode cm,
                                    Watcher wt, boolean create) {
        ZNodeProxy zn = new ZNodeProxy(zk, parent, name, ids, cm, wt);
        if (create) try {
            ZNodeHelper.createZNode(zk, zn, false, false);
        } catch (Exception e) {
            throw new ZNodeProxyRuntimeException(e);
        }
        return zn;
    }

    public static ZNode createZNode(AdvZooKeeper zk, String name, boolean create) {
        return createZNode(zk, null, name, null, null, null, create);
    }

    public static ZNode createZNode(AdvZooKeeper zk, ZNode parent, String name, boolean create) {
        return createZNode(zk, parent, name, null, null, null, create);
    }

    public static ZNode createZNode(AdvZooKeeper zk, ZNode parent, String name) {
        return createZNode(zk, parent, name, null, null, null, true);
    }

    public static ZNode createZNode(AdvZooKeeper zk, String name) {
        return createZNode(zk, null, name, null, null, null, true);
    }
    //
    // public static ZNodeProxy createZNode(ZNode parent, String name, List<ACL> ids, CreateMode cm, Watcher watcher) {
    // try {
    // return createZNode(parent, name, ids, cm, watcher, true);
    // } catch (Exception e) {
    // throw new RuntimeException(e);
    // }
    // }

}
