package com.alibaba.hbase.replication.zookeeper;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.regionserver.wal.HLog;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.zookeeper.RecoverableZooKeeper;
import org.apache.hadoop.hbase.zookeeper.ZKUtil;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;

import com.alibaba.hbase.replication.consumer.ReplicationZookeeperWatcher;
import com.alibaba.hbase.replication.hlog.domain.HLogEntry;
import com.alibaba.hbase.replication.hlog.domain.HLogEntry.Type;
import com.alibaba.hbase.replication.hlog.domain.HLogEntryGroup;
import com.alibaba.hbase.replication.utility.ProducerConstants;

/**
 * HLogPersistence 持久化操作 类HLogZookeeperPersistence.java的实现描述：利用zk记录HLog处理的偏移量
 * 注意：此类的方法并非线程安全，一个线程需要new一个
 * @author zalot.zhaoh Mar 7, 2012 10:24:18 AM
 */
public class HLogZookeeperPersistence {

    protected RecoverableZooKeeper zoo;
    protected String               baseDir;
    protected String               name = UUID.randomUUID().toString().substring(0, 10);

    // public ArrayList<ACL> perms;

    public String getName() {
        return name;
    }
    
    public HLogZookeeperPersistence(Configuration conf) throws IOException, KeeperException, InterruptedException{
        RecoverableZooKeeper zoo = ZKUtil.connect(conf, new ReplicationZookeeperWatcher());
        String rootDir = conf.get(ProducerConstants.CONFKEY_ZOO_ROOT, ProducerConstants.ZOO_ROOT);
        Stat stat = zoo.exists(rootDir, false);
        if (stat == null) {
            zoo.create(rootDir, null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
        baseDir = rootDir + ProducerConstants.ZOO_PERSISTENCE_HLOG_GROUP;
        
        stat = zoo.exists(baseDir, false);
        if (stat == null) {
            zoo.create(baseDir, null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
    }

    public void createEntry(HLogEntry entry) throws Exception {
        zoo.create(getEntryPath(entry), getEntryData(entry), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    }

    public void deleteEntry(HLogEntry entry) throws Exception {
        String path = getEntryPath(entry);
        Stat stat = zoo.exists(path, false);
        if (stat != null) {
            zoo.delete(path, stat.getVersion());
        }
    }

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

    public HLogEntry getHLogEntry(String groupName, String name) throws Exception {
        if (!HLog.validateHLogFilename(name)) {
            return null;
        }
        String path = getEntryPath(groupName, name);
        Stat stat = zoo.exists(path, false);
        if (stat != null) {
            HLogEntry entry = new HLogEntry(name);
            setEntryData(entry, zoo.getData(path, false, stat));
            return entry;
        }
        return null;
    }

    public List<String> listGroupName() throws KeeperException, InterruptedException {
        return zoo.getChildren(baseDir, false);
    }

    public void createGroup(HLogEntryGroup group, boolean createChild) throws Exception {
        zoo.create(getGroupPath(group.getGroupName()), getGroupData(group), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        if (createChild) {
            for (HLogEntry entry : group.getEntrys()) {
                createEntry(entry);
            }
        }
    }

    public void deleteGroup(HLogEntryGroup group) throws Exception {
        // Stat stat = getGroupStat(group);
        // if (stat != null) {
        // zoo.delete(getGroupPath(group.getGroupName()), stat.getVersion());
        // }
    }

    public HLogEntryGroup getGroupByName(String groupName, boolean getChild) throws Exception {
        String path = getGroupPath(groupName);
        Stat stat = zoo.exists(path, false);
        if (stat == null) return null;
        HLogEntryGroup group = new HLogEntryGroup(groupName);
        setGroupData(group, zoo.getData(path, false, stat));
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

    public void updateGroup(HLogEntryGroup group, boolean updateChild) throws Exception {
        String path = getGroupPath(group.getGroupName());
        Stat stat = zoo.exists(path, false);
        if (stat != null) {
            //
            //
            //
            zoo.setData(path, getGroupData(group), stat.getVersion());
            if (updateChild) {
                HLogEntry tmpEntry;
                for (HLogEntry entry : group.getEntrys()) {
                    tmpEntry = getHLogEntry(entry.getGroupName(), entry.getName());
                    if (tmpEntry == null) {
                        createEntry(entry);
                    }
                }
            }
        }
    }

    public void createOrUpdateGroup(HLogEntryGroup group, boolean updateChild) throws Exception {
        HLogEntryGroup tmpGroup = getGroupByName(group.getGroupName(), false);
        if (tmpGroup == null) {
            createGroup(group, updateChild);
        } else {
            updateGroup(group, updateChild);
        }
    }

    public boolean isLockGroup(String groupName) throws Exception {
        Stat stat = getLockGroupStat(groupName);
        if (stat != null) {
            return true;
        }
        return false;
    }

    public boolean lockGroup(String groupName) throws Exception {
        if (!isLockGroup(groupName)) {
            try {
                zoo.create(getGroupLockPath(groupName), null, Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
                // String lockPath = getGroupLockPath(groupName);
                // String seqPath = zoo.create(lockPath, null, Ids.OPEN_ACL_UNSAFE , CreateMode.EPHEMERAL);
                // if(getLockSeq(lockPath, seqPath) != 0){
                // zoo.delete(seqPath, 0);
                // return false;
                // }
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    public void unlockGroup(String groupName) throws Exception {
        Stat stat = getLockGroupStat(groupName);
        if (stat == null) return;
        try {
            zoo.delete(getGroupLockPath(groupName), stat.getVersion());
        } catch (Exception e) {
            return;
        }
    }

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
        return Bytes.toBytes(group.getLastOperatorTime());
    }

    /**
     * 请与 getGroupData 保持一致性
     * 
     * @param entry
     * @param data
     */
    protected void setGroupData(HLogEntryGroup entry, byte[] data) {
        if (entry != null && data != null) {
            entry.setLastOperatorTime(Bytes.toLong(data));
        }
    }

    protected String getGroupPath(String groupName) {
        return baseDir + "/" + groupName;
    }

    protected String getGroupLockPath(String groupName) {
        return baseDir + "/" + groupName + ProducerConstants.ZOO_PERSISTENCE_HLOG_GROUP_LOCK;
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

    protected long getLockSeq(String lockPath, String seqPath) {
        try {
            String tmpLockPath = lockPath + ManagementFactory.getRuntimeMXBean().getName();
            int idx = seqPath.indexOf(tmpLockPath);
            if (idx == 0) {
                return Long.parseLong(seqPath.substring(tmpLockPath.length() + 1, seqPath.length()));
            }
        } catch (Exception e) {
        }
        return -1;
    }
}
