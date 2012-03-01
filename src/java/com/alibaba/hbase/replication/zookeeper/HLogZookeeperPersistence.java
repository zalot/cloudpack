package com.alibaba.hbase.replication.zookeeper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.zookeeper.RecoverableZooKeeper;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;

import com.alibaba.hbase.replication.domain.HLogEntry;
import com.alibaba.hbase.replication.domain.HLogEntry.Type;
import com.alibaba.hbase.replication.domain.HLogEntryGroup;
import com.alibaba.hbase.replication.persistence.HLogPersistence;
import com.alibaba.hbase.replication.utility.AliHBaseConstants;

public class HLogZookeeperPersistence implements HLogPersistence {

    protected RecoverableZooKeeper zoo;
    protected String               baseDir;

    public void setZookeeper(RecoverableZooKeeper zoo) {
        this.zoo = zoo;
    }

    @Override
    public void setConf(Configuration conf) {

    }

    @Override
    public Configuration getConf() {
        return null;
    }

    @Override
    public void init(Configuration conf) throws KeeperException, InterruptedException {
        String rootDir = conf.get(AliHBaseConstants.CONFKEY_ZOO_ROOT, AliHBaseConstants.ZOO_ROOT);
        Stat stat = zoo.exists(rootDir, false);
        if (stat == null) {
            zoo.create(rootDir, null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }

        baseDir = rootDir + AliHBaseConstants.ZOO_PERSISTENCE_HLOG_GROUP;
        stat = zoo.exists(baseDir, false);
        if (stat == null) {
            zoo.create(baseDir, null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
    }

    @Override
    public void createEntry(HLogEntry entry) throws Exception {
        zoo.create(getEntryPath(entry), getEntryData(entry), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    }

    @Override
    public void deleteEntry(HLogEntry entry) throws Exception {
        String path = getEntryPath(entry);
        Stat stat = zoo.exists(path, false);
        if (stat != null) {
            zoo.delete(path, stat.getVersion());
        }
    }

    @Override
    public void updateEntry(HLogEntry entry) throws Exception {
        String path = getEntryPath(entry);
        Stat stat = zoo.exists(path, false);
        if (stat != null) {
            zoo.setData(path, getEntryData(entry), stat.getVersion());
        }
    }

    public List<HLogEntry> listEntry(String groupName) throws Exception {
        String path = getGroupPath(groupName);
        Stat stat = zoo.exists(path, false);
        if (stat == null) return Collections.EMPTY_LIST;
        List<HLogEntry> entrys = new ArrayList<HLogEntry>();
        List<String> ls = zoo.getChildren(path, false);
        HLogEntry entry = null;
        for (String name : ls) {
            entry = getHLogEntry(groupName, name);
            if (entry != null) entrys.add(entry);
        }
        return entrys;
    }

    @Override
    public HLogEntry getHLogEntry(String groupName, String name) throws Exception {
        String path = getEntryPath(groupName, name);
        Stat stat = zoo.exists(path, false);
        if (stat != null) {
            HLogEntry entry = new HLogEntry(name);
            setEntryData(entry, zoo.getData(path, false, stat));
            return entry;
        }
        return null;
    }

    //
    //
    //

    @Override
    public List<String> listGroupName() throws KeeperException, InterruptedException {
        return zoo.getChildren(baseDir, false);
    }

    @Override
    public void createGroup(HLogEntryGroup group, boolean createChild) throws Exception {
        zoo.create(getGroupPath(group.getGroupName()), getGroupData(group), Ids.OPEN_ACL_UNSAFE,
                   CreateMode.PERSISTENT);
        if (createChild) {
            for (HLogEntry entry : group.getEntrys()) {
                createEntry(entry);
            }
        }
    }

    @Override
    public void deleteGroup(HLogEntryGroup group) throws Exception {
        Stat stat = getGroupStat(group);
        if (stat != null) {
            zoo.delete(getGroupPath(group.getGroupName()), stat.getVersion());
        }
    }

    @Override
    public HLogEntryGroup getGroupByName(String groupName, boolean getChild) throws Exception {
        String path = getGroupPath(groupName);
        Stat stat = zoo.exists(path, false);
        if (stat == null) return null;
        HLogEntryGroup group = new HLogEntryGroup(groupName);
        if (getChild) {
            List<String> ls = zoo.getChildren(path, false);
            HLogEntry entry = null;
            for (String name : ls) {
                entry = getHLogEntry(groupName, name);
                if (entry != null) group.put(entry);
            }
        }
        return group;
    }

    @Override
    public void updateGroup(HLogEntryGroup group, boolean updateChild) throws Exception {
        HLogEntryGroup tmpGroup = getGroupByName(group.getGroupName(), false);
        if (tmpGroup == null) {

        }
    }

    @Override
    public boolean isLockGroup(String groupName) throws Exception {
        Stat stat = getLockGroupStat(groupName);
        if (stat != null) {
            return true;
        }
        return false;
    }

    @Override
    public boolean lockGroup(String groupName) throws Exception {
        if (!isLockGroup(groupName)) {
            zoo.create(groupName + AliHBaseConstants.ZOO_PERSISTENCE_HLOG_GROUP_LOCK, null, Ids.OPEN_ACL_UNSAFE,
                       CreateMode.EPHEMERAL);
            return true;
        }
        return false;
    }

    @Override
    public void unlockGroup(String groupName) throws Exception {
        Stat stat = getLockGroupStat(groupName);
        if (stat == null) return;
        zoo.delete(getGroupLockPath(groupName), stat.getVersion());
    }

    //
    //
    //

    private byte[] getEntryData(HLogEntry entry) {
        String data = entry.getPos() + "." + entry.getType().getTypeValue();
        return Bytes.toBytes(data);
    }

    private void setEntryData(HLogEntry entry, byte[] data) {
        if (data != null && entry != null) {
            String dataString = Bytes.toString(data);
            int idx = dataString.lastIndexOf(".");
            if (idx > 0) {
                entry.setPos(Long.parseLong(dataString.substring(0, idx)));
                entry.setType(Type.toType(Integer.valueOf(dataString.substring(idx + 1, dataString.length()))));
            }
        }
    }

    private String getEntryPath(String groupName, String name) {
        return baseDir + "/" + groupName + "/" + name;
    }

    private String getEntryPath(HLogEntry entry) {
        return getEntryPath(entry.getGroupName(), entry.getName());
    }

    //
    //
    //
    protected byte[] getGroupData(HLogEntryGroup group) {
        return null;
    }

    protected void setGroupData(HLogEntryGroup entry, byte[] data) {
        return;
    }

    protected String getGroupPath(String groupName) {
        return baseDir + "/" + groupName;
    }

    protected String getGroupLockPath(String groupName) {
        return baseDir + "/" + groupName + AliHBaseConstants.ZOO_PERSISTENCE_HLOG_GROUP_LOCK;
    }

    protected Stat getGroupStat(HLogEntryGroup group) throws Exception {
        String lockpath = getGroupPath(group.getGroupName());
        return zoo.exists(lockpath, false);
    }

    protected Stat getLockGroupStat(String groupName) throws Exception {
        HLogEntryGroup group = getGroupByName(groupName, false);
        if (group != null) {
            String lockpath = getGroupLockPath(groupName);
            Stat stat = zoo.exists(lockpath, false);
            return stat;
        }
        return null;
    }

}
