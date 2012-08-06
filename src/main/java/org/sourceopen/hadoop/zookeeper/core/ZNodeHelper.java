package org.sourceopen.hadoop.zookeeper.core;

import java.util.List;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;
import org.sourceopen.hadoop.zookeeper.connect.AdvZooKeeper;

public class ZNodeHelper {

    protected static final Log           LOG        = LogFactory.getLog(ZNodeHelper.class);
    protected static ThreadLocal<byte[]> threadLock = new ThreadLocal<byte[]>();

    public static void createZNode(AdvZooKeeper zk, ZNode zNode, List<ACL> ids, CreateMode cm, Watcher watch,
                                   boolean relation) throws KeeperException, InterruptedException {
        if (zNode.getParent() != null && relation == true) {
            Stat stat = zk.exists(zNode.getParent().getPath(), watch);
            if (stat == null) createZNode(zk, zNode.getParent(), ids, cm, watch, relation);
        }
        Stat stat = zk.exists(zNode.getPath(), watch);
        if (stat == null) {
            zk.create(zNode.getPath(), zNode.getData(), ids, cm);
        }
    }

    public static boolean lock(AdvZooKeeper zk, ZNode zNode) {
        try {
            Stat stat = zk.exists(zNode.getPath(), false);
            if (stat != null) {
                return false;
            }
            zk.create(zNode.getPath(), getLockData(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            return true;
        } catch (Exception e) {
        }
        return false;
    }

    public static boolean unlock(AdvZooKeeper zk, ZNode zn) {
        try {
            String lockPath = getLockPath(zn);
            Stat stat = zk.exists(lockPath, false);
            if (stat != null) {
                byte[] data = zk.getData(lockPath, false, stat);
                if (getLockData().equals(data)) {
                    zk.delete(lockPath, stat.getVersion());
                    return true;
                }
            }
        } catch (Exception e) {
        }
        return false;
    }

    protected static String getLockPath(ZNode zn) {
        return zn.getPath() + ZNodeConstants.SPLIT + ZNodeConstants.LOCK_SUFFIX;
    }

    protected static byte[] getLockData() {
        if (threadLock.get() == null) {
            threadLock.set(UUID.randomUUID().toString().getBytes());
        }
        return threadLock.get();
    }
}
