package org.sourceopen.hadoop.zookeeper.core;

import java.util.ArrayList;
import java.util.List;

public abstract class ZNode {

    protected final static String SPLIT  = "/";
    protected ZNode               parent;
    protected List<ZNode>         childs = new ArrayList<ZNode>();
    protected String              name;

    public ZNode(ZNode parent, String name){
        this.parent = parent;
        this.name = name;
        check(name);
    }

    private void check(String name) {
        if(name == null){
            throw new RuntimeException("ZNode' name is null!");
        }
        if(name.indexOf("/") > -1 || name.indexOf("\\") > -1){
            throw new RuntimeException("ZNode' name illegalException ! not use [\\ or /]");
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

    final public String getPath() {
        if (parent != null) {
            return parent.getPath() + SPLIT + getName();
        }
        return SPLIT + getName();
    }

    public ZNode getParent() {
        return parent;
    }

    public List<ZNode> getChilds() {
        return childs;
    }

    public void addChild(ZNode znode) {
        if (znode != null) childs.add(znode);
    }

    public byte[] getDate() {
        return new byte[0];
    }
}
