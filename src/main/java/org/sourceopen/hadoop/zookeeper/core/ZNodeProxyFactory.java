package org.sourceopen.hadoop.zookeeper.core;


public class ZNodeProxyFactory {
    //
    // public static void setZooKeeperProxy(ZooKeeper zk) {
    // ZNodeProxy.ZK = zk;
    // }
    //
    // public static ZNodeProxy createZNode(ZNode parent, String name, List<ACL> ids, CreateMode cm, Watcher watcher,
    // boolean r) throws KeeperException, InterruptedException {
    // ZNodeProxy zn = new ZNodeProxy(parent, name, ids, cm, watcher);
    // zn.ids = ids;
    // zn.createMode = cm;
    // zn.watcher = watcher;
    // if (r) ZNodeHelper.createZNode(ZNodeProxy.ZK, zn, ids, cm, zn.watcher, r);
    // return zn;
    // }
    //
    // public static ZNodeProxy createZNode(ZNode parent, String name, List<ACL> ids, CreateMode cm, Watcher watcher) {
    // try {
    // return createZNode(parent, name, ids, cm, watcher, true);
    // } catch (Exception e) {
    // throw new RuntimeException(e);
    // }
    // }

}
