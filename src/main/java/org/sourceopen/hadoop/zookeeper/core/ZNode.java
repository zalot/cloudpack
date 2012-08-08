package org.sourceopen.hadoop.zookeeper.core;

import java.util.ArrayList;
import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.ACL;
import org.sourceopen.hadoop.zookeeper.connect.NothingZookeeperWatch;

public class ZNode {

    protected final static Watcher DEFAULT_WATCHER = new NothingZookeeperWatch();

    protected List<ZNode>          childs          = new ArrayList<ZNode>();
    protected byte[]               data            = null;
    protected List<ACL>            ids             = Ids.OPEN_ACL_UNSAFE;
    protected CreateMode           createMode      = CreateMode.PERSISTENT;
    protected Watcher              watcher         = DEFAULT_WATCHER;

    protected ZNode                parent          = null;
    protected String               name            = null;

    public ZNode(ZNode parent, String name){
        this(parent, name, null, null, null);
    }

    public ZNode(String name){
        this(null, name, null, null, null);
    }

    public ZNode(ZNode parent, String name, List<ACL> ids, CreateMode createMode, Watcher watcher){
        this.parent = parent;
        this.name = name.toLowerCase();
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

    public ZNode addChild(ZNode znode) {
        if (znode != null) {
            znode.setParent(this);
            childs.add(znode);
        }
        return znode;
    }

    public ZNode addChild(String name) {
        ZNode zn = null;
        if (name != null) {
            zn = getChild(name);
            if (zn == null) {
                zn = new ZNode(this, name);
                childs.add(zn);
            }
        }
        return zn;
    }
    
    public void delChild(String name){
        
    }

    public ZNode getChild(String name) {
        for (ZNode child : childs) {
            if (child.getName().equals(name)) {
                return child;
            }
        }
        return null;
    }

    public List<ZNode> getChilds() {
        return this.childs;
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
