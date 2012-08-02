package org.sourceopen.hadoop.zookeeper.core;

import java.util.List;
import java.util.UUID;

import org.apache.hadoop.hbase.util.Bytes;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

public class ZNodeProxy extends ZNode {

    static ZooKeeper           ZK          = null;
    public static final String LOCK_SUFFIX = "l-o-c-k";

    public ZNodeProxy(ZNode parent, String name, boolean igno, Watcher watch) throws KeeperException,
                                                                             InterruptedException{
        super(parent, name);
        if (ZK == null) {
            throw new NullPointerException("can not create ZNodeProxy, please invoke ZNodeFactory.setZooKeeperProxy!");
        }
        Stat stat = ZK.exists(parent.getPath(), false);
        if (igno && stat == null) {
            
        }
    }

    public ZNodeProxy(ZNode parent, String name){
        this(parent, name, false);
    }

    public ZNodeProxy(String name){
        this(null, name, true);
    }

    @Override
    public byte[] getData() {
        // TODO Auto-generated method stub
        return super.getData();
    }

    @Override
    public void setData(byte[] data) {
        // TODO Auto-generated method stub
        super.setData(data);
    }

    @Override
    public List<ZNode> getChilds() {
        // TODO Auto-generated method stub
        return super.getChilds();
    }

    @Override
    public void addChild(ZNode znode) {
        System.out.println();
        super.addChild(znode);
    }

    // ===========================================================
    //
    // Thread Lock sg!
    //
    // ===========================================================
    protected String              lockPath   = null;
    protected ThreadLocal<String> threadUUID = new ThreadLocal<String>();

    private String getLockPath() {
        if (lockPath == null) {
            lockPath = getPath() + ZNode.SPLIT + LOCK_SUFFIX;
        }
        return lockPath;
    }

    private byte[] getLockData() {
        return Bytes.toBytes(getUuid());
    }

    private String setLockData(byte[] data) {
        return Bytes.toString(data);
    }

    public boolean lock() {
        try {
            Stat stat = ZK.exists(getPath(), false);
            if (stat != null) {
                return false;
            }
            ZK.create(getPath(), getLockData(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            return true;
        } catch (Exception e) {
        }
        return false;
    }

    public boolean unlock() {
        try {
            Stat stat = ZK.exists(getLockPath(), false);
            if (stat != null) {
                String uuid = setLockData(ZK.getData(getLockPath(), false, stat));
                if (getUuid().equals(uuid)) {
                    ZK.delete(getLockPath(), stat.getVersion());
                }
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    protected String getUuid() {
        if (threadUUID.get() == null) {
            threadUUID.set(UUID.randomUUID().toString());
        }
        return threadUUID.get();
    }
}
