package org.sourceopen.hadoop.zookeeper.core;

public class ZNodeProxy extends ZNode {

    public ZNodeProxy(String name){
        super(name);
    }

    // protected static ZooKeeper ZK = null;
    //
    // ZNodeProxy(String name){
    // super(null, name);
    // if (ZK == null) {
    // throw new NullPointerException("can not create ZNodeProxy, please invoke ZNodeFactory.setZooKeeperProxy!");
    // }
    // }
    //
    // ZNodeProxy(ZNode parent, String name, List<ACL> ids, CreateMode createMode, Watcher watcher){
    // super(parent, name, ids, createMode, watcher);
    // if (ZK == null) {
    // throw new NullPointerException("can not create ZNodeProxy, please invoke ZNodeFactory.setZooKeeperProxy!");
    // }
    // }
    //
    // public byte[] getData() {
    // if (this.data == null) {
    // Stat stat;
    // try {
    // stat = ZK.exists(getPath(), false);
    // if (stat != null) {
    // this.data = ZK.getData(getPath(), watcher, stat);
    // }
    // } catch (Exception e) {
    // throw new ZNodeProxyException(e);
    // }
    // }
    // return data;
    // }
    //
    // @Override
    // public void setData(byte[] bt) {
    // if (this.data != null) {
    // Stat stat;
    // try {
    // stat = ZK.exists(getPath(), false);
    // if (stat != null) {
    // ZK.setData(getPath(), data, stat.getVersion());
    // }
    // } catch (Exception e) {
    // throw new ZNodeProxyException(e);
    // }
    // }
    // }
    //
    // @Override
    // public List<ZNode> getChilds() {
    // List<ZNode> nodes = new ArrayList<ZNode>();
    // try {
    // List<String> childs = ZK.getChildren(getPath(), false);
    // for (String c : childs) {
    // nodes.add(ZNodeProxyFactory.createZNode(parent, name, ids, createMode, watcher, false));
    // }
    // } catch (Exception e) {
    // throw new ZNodeProxyException(e);
    // }
    // return nodes;
    // }
    //
    // @Override
    // public void addChild(ZNode znode) {
    // if (znode.getParent() == null) znode.setParent(this);
    // if (this.getParent().equals(znode)) {
    // try {
    // ZK.exists(znode.getPath(), watcher == null ? false : true);
    // } catch (Exception e) {
    // throw new ZNodeProxyException(e);
    // }
    // }
    // }

}
