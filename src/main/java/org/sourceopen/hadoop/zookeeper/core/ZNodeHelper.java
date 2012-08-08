package org.sourceopen.hadoop.zookeeper.core;

import java.lang.management.ManagementFactory;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;
import org.sourceopen.hadoop.zookeeper.connect.AdvZooKeeper;

public class ZNodeHelper {

    protected static final Log           LOG        = LogFactory.getLog(ZNodeHelper.class);
    protected static ThreadLocal<byte[]> threadLock = new ThreadLocal<byte[]>();

    public static void createZNode(AdvZooKeeper zk, ZNode zn, boolean isParent, boolean isChild)
                                                                                                throws KeeperException,
                                                                                                InterruptedException {
        if (zn.getParent() != null && isParent == true) {
            createZNode(zk, zn.getParent(), true, false);
        }
        Stat stat = zk.exists(zn.getPath(), zn.watcher);
        if (stat == null) {
            zk.create(zn.getPath(), zn.getData(), zn.ids, zn.createMode);
        }
        if (isChild) for (ZNode child : zn.getChilds()) {
            createZNode(zk, child, false, true);
        }
    }

    public static boolean lock(AdvZooKeeper zk, ZNode zn) {
        return lock(zk, zn, null);
    }

    public static boolean lock(AdvZooKeeper zk, ZNode zn, String lockName) {
        try {
            String lockPath = lockName == null ? getLockPath(zn) : zn.getPath() + ZNodeConstants.SPLIT + lockName;
            Stat stat = zk.exists(lockPath, false);
            if (stat != null) {
                return false;
            }
            zk.create(lockPath, getLockData(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            return true;
        } catch (Exception e) {
        }
        return false;
    }

    public static boolean unlock(AdvZooKeeper zk, ZNode zn) {
        return unlock(zk, zn, null);
    }

    public static boolean unlock(AdvZooKeeper zk, ZNode zn, String lockName) {
        try {
            String lockPath = lockName == null ? getLockPath(zn) : zn.getPath() + ZNodeConstants.SPLIT + lockName;
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
        return zn.getPath() + ZNodeConstants.SPLIT + ZNodeConstants.LOCK_PREFIX
               + ManagementFactory.getRuntimeMXBean().getName();
    }

    protected static byte[] getLockData() {
        if (threadLock.get() == null) {
            threadLock.set(UUID.randomUUID().toString().getBytes());
        }
        return threadLock.get();
    }

    public static void main(String[] args) {
        System.out.println(ManagementFactory.getRuntimeMXBean().getName());
    }
}
