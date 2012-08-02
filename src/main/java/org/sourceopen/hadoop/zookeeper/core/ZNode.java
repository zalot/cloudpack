package org.sourceopen.hadoop.zookeeper.core;

import java.util.ArrayList;
import java.util.List;

/**
 * 类ZNode.java的实现描述：TODO 类实现描述
 * 
 * @author zalot.zhaoh Aug 1, 2012 4:34:06 PM
 */
public class ZNode implements ZLockSupport {

    protected final static String SPLIT  = "/";
    protected ZNode               parent = null;
    protected List<ZNode>         childs = new ArrayList<ZNode>();
    protected String              name   = null;
    protected byte[]              data   = null;

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public ZNode(ZNode parent, String name){
        this.parent = parent;
        this.name = name;
        validate(this.parent, this);
    }

    private static void validate(ZNode parent, ZNode child) {
        if (child.getName() == null) {
            throw new RuntimeException("ZNode' name is null!");
        }
    }

    public ZNode(String name){
        this(null, name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        if (parent != null) {
            return parent.getPath() + SPLIT + getName();
        }
        return SPLIT + getName();
    }

    public ZNode getParent() {
        return parent;
    }

    public void setParent(ZNode parent) {
        this.parent = parent;
    }

    public List<ZNode> getChilds() {
        return childs;
    }

    public void addChild(ZNode znode) {
        if (znode != null) {
            znode.setParent(this);
            childs.add(znode);
        }
    }

    @Override
    public boolean lock() {
        return false;
    }

    @Override
    public boolean unlock() {
        return false;
    }
}
