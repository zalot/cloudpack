package org.sourceopen.hadoop.zookeeper.connect;

import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.RetryCounter;
import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooKeeper.States;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;

public interface AdvZooKeeper {

    public void delete(String path, int version) throws InterruptedException, KeeperException;

    public Stat exists(String path, Watcher watcher) throws KeeperException, InterruptedException;

    public Stat exists(String path, boolean watch) throws KeeperException, InterruptedException;

    public List<String> getChildren(String path, Watcher watcher) throws KeeperException, InterruptedException;

    public List<String> getChildren(String path, boolean watch) throws KeeperException, InterruptedException;

    public byte[] getData(String path, Watcher watcher, Stat stat) throws KeeperException, InterruptedException;

    public byte[] getData(String path, boolean watch, Stat stat) throws KeeperException, InterruptedException;

    public Stat setData(String path, byte[] data, int version) throws KeeperException, InterruptedException;

    public String create(String path, byte[] data, List<ACL> acl, CreateMode createMode) throws KeeperException, InterruptedException;

    public byte[] removeMetaData(byte[] data);

    public long getSessionId();

    public void close() throws InterruptedException;

    public States getState();

    public ZooKeeper getZooKeeper();

    public byte[] getSessionPasswd();

    public void sync(String path, AsyncCallback.VoidCallback cb, Object ctx);
}
