package org.sourceopen.hadoop.zookeeper.core;

import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;
import org.sourceopen.hadoop.zookeeper.connect.AdvZooKeeper;

public class ZNodeProxy extends ZNode {

    protected AdvZooKeeper zk;

    ZNodeProxy(AdvZooKeeper zk, ZNode parent, String name, List<ACL> ids, CreateMode createMode, Watcher watcher){
        super(parent, name, ids, createMode, watcher);
        this.zk = zk;
    }

    ZNodeProxy(AdvZooKeeper zk, ZNode parent, String name){
        super(parent, name);
        this.zk = zk;
    }

    public byte[] getData() {
        Stat stat;
        try {
            stat = zk.exists(getPath(), false);
            if (stat != null) {
                zk.getData(getPath(), false, stat);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    @Override
    public void setData(byte[] data) {
        try {
            Stat stat = zk.exists(getPath(), watcher);
            if (stat != null) {
                zk.setData(getPath(), data, stat.getVersion());
            }
        } catch (Exception e) {
            throw new ZNodeProxyRuntimeException(e);
        }
    }

    public List<ZNode> getChilds() {
        try {
            childs.clear();
            for (String childName : zk.getChildren(getPath(), watcher)) {
                childs.add(new ZNodeProxy(zk, this, childName));
            }
        } catch (Exception e) {
            throw new ZNodeProxyRuntimeException(e);
        }
        return childs;
    }

    @Override
    public ZNode addChild(ZNode znode) {
        znode.setParent(this);
        try {
            ZNodeHelper.createZNode(zk, znode, false, false);
        } catch (Exception e) {
            throw new ZNodeProxyRuntimeException(e);
        }
        return znode;
    }

    @Override
    public ZNode addChild(String name) {
        ZNodeProxy znode = new ZNodeProxy(zk, this, name);
        try {
            ZNodeHelper.createZNode(zk, znode, false, false);
        } catch (Exception e) {
            throw new ZNodeProxyRuntimeException(e);
        }
        return znode;
    }

    @Override
    public ZNode getChild(String name) {
        ZNodeProxy znode = new ZNodeProxy(zk, this, name);
        try {
            Stat stat = zk.exists(znode.getPath(), false);
            if (stat == null) return null;
        } catch (Exception e) {
            throw new ZNodeProxyRuntimeException(e);
        }
        return znode;
    }

    public void delChild(String name) {
        ZNodeProxy znode = new ZNodeProxy(zk, this, name);
        try {
            Stat stat = zk.exists(znode.getPath(), false);
            if (stat != null) zk.delete(znode.getPath(), stat.getVersion());
        } catch (Exception e) {
            throw new ZNodeProxyRuntimeException(e);
        }
    }
}
