package org.sourceopen.hadoop.zookeeper.core;

import java.util.ArrayList;
import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.ACL;

public abstract class ZNode {

    protected List<ZNode> childs     = new ArrayList<ZNode>();
    protected byte[]      data       = null;
    protected List<ACL>   ids        = Ids.READ_ACL_UNSAFE;
    protected CreateMode  createMode = CreateMode.EPHEMERAL;
    protected Watcher     watcher    = null;

    protected ZNode       parent     = null;
    protected String      name       = null;

    public ZNode(ZNode parent, String name){
        this(parent, name, null, null, null);
    }

    public ZNode(String name){
        this(null, name, null, null, null);
    }

    public ZNode(ZNode parent, String name, List<ACL> ids, CreateMode createMode, Watcher watcher){
        this.parent = parent;
        this.name = name;
        validate(this.parent, this);
        if (ids != null) this.ids = ids;
        if (createMode != null) this.createMode = createMode;
        if (watcher != null) this.watcher = watcher;
    }

    public List<ACL> getIds() {
        return ids;
    }

    public void setIds(List<ACL> ids) {
        this.ids = ids;
    }

    public CreateMode getCreateMode() {
        return createMode;
    }

    public void setCreateMode(CreateMode createMode) {
        this.createMode = createMode;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    private static void validate(ZNode parent, ZNode child) {
        if (child.getName() == null) {
            throw new RuntimeException("ZNode' name is null!");
        }
    }

    public List<ZNode> getChilds() {
        return childs;
    }

    public void addChild(ZNode znode) {
        if (znode != null) {
            childs.add(znode);
        }
    }

    public String getName() {
        return this.name;
    }

    public ZNode getParent() {
        return this.parent;
    }

    public void setParent(ZNode znode) {
        this.parent = znode;
    }

    public boolean isRoot() {
        if (parent == null) return true;
        return false;
    }

    public String getPath() {
        if (parent != null) {
            return parent.getPath() + ZNodeConstants.SPLIT + getName();
        }
        return ZNodeConstants.SPLIT + getName();
    }

    public boolean equals(Object znode) {
        if (znode instanceof ZNode && znode != null && this.getPath().equals(((ZNode) znode).getPath())) {
            return true;
        }
        return false;
    }
}
