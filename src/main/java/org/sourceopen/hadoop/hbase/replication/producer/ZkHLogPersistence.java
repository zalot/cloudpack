package org.sourceopen.hadoop.hbase.replication.producer;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.regionserver.wal.HLog;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.NodeExistsException;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;
import org.sourceopen.hadoop.hbase.replication.core.hlog.domain.HLogEntry;
import org.sourceopen.hadoop.hbase.replication.core.hlog.domain.HLogEntry.Type;
import org.sourceopen.hadoop.hbase.replication.core.hlog.domain.HLogGroup;
import org.sourceopen.hadoop.hbase.replication.core.hlog.domain.HLogPersistence;
import org.sourceopen.hadoop.hbase.utils.HRepConfigUtil;
import org.sourceopen.hadoop.zookeeper.connect.AdvZooKeeper;
import org.sourceopen.hadoop.zookeeper.core.ZNode;

/**
 * HLogPersistence 持久化操作
 * 
 * @author zalot.zhaoh Mar 7, 2012 10:24:18 AM
 */
public class ZkHLogPersistence implements HLogPersistence {

    protected static final String DATA_SPLIT = "|";
    protected static final Log    LOG        = LogFactory.getLog(ZkHLogPersistence.class);
    protected AdvZooKeeper        zoo        = null;
    protected ZNode               pnode      = null;
    protected ThreadLocal<String> uuid       = new ThreadLocal<String>();                  ;

    // public ArrayList<ACL> perms;

    public String getName() {
        if (uuid.get() == null) {
            uuid.set(UUID.randomUUID().toString());
        }
        return uuid.get();
    }

    public ZkHLogPersistence(Configuration conf, AdvZooKeeper zoo) throws Exception{
        this.zoo = zoo;
        init(conf);
    }

    public void createEntry(HLogEntry entry) throws Exception {
        try {
            zoo.create(getEntryPath(entry), getEntryData(entry), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        } catch (NodeExistsException e) {
            LOG.warn("entry exist" + entry);
        }
    }

    public void deleteEntry(HLogEntry entry) throws Exception {
        String path = getEntryPath(entry);
        Stat stat = zoo.exists(path, false);
        if (stat != null) {
            zoo.delete(path, stat.getVersion());
        }
    }

    public void updateEntry(HLogEntry entry) throws Exception {
        if (entry.getType() == HLogEntry.Type.NOFOUND) {
            deleteEntry(entry);
            return;
        }
        String path = getEntryPath(entry);
        Stat stat = zoo.exists(path, false);
        if (stat != null) {
            zoo.setData(path, getEntryData(entry), stat.getVersion());
        }
    }

    @SuppressWarnings("unchecked")
    public List<HLogEntry> listEntry(String groupName) throws Exception {
        String path = getGroupPath(groupName);
        Stat stat = zoo.exists(path, false);
        if (stat == null) return Collections.EMPTY_LIST;
        List<HLogEntry> entrys = new ArrayList<HLogEntry>();
        List<String> ls = zoo.getChildren(path, false);
        HLogEntry entry = null;
        for (String name : ls) {
            entry = getEntry(groupName, name);
            if (entry != null) entrys.add(entry);
        }
        return entrys;
    }

    public HLogEntry getEntry(String groupName, String name) throws Exception {
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
        return zoo.getChildren(pnode.getPath(), false);
    }

    public void createGroup(HLogGroup group, boolean createChild) throws Exception {
        try {
            zoo.create(getGroupPath(group.getGroupName()), getGroupData(group), Ids.OPEN_ACL_UNSAFE,
                       CreateMode.PERSISTENT);
        } catch (NodeExistsException e) {
            LOG.warn("group exist" + group);
        }
        if (createChild) {
            for (HLogEntry entry : group.getEntrys()) {
                createEntry(entry);
            }
        }
    }

    public HLogGroup getGroupByName(String groupName, boolean getChild) throws Exception {
        String path = getGroupPath(groupName);
        Stat stat = zoo.exists(path, false);
        if (stat == null) return null;
        HLogGroup group = new HLogGroup(groupName);
        setGroupData(group, zoo.getData(path, false, stat));
        if (getChild) {
            List<String> ls = zoo.getChildren(path, false);
            HLogEntry entry = null;
            for (String name : ls) {
                entry = getEntry(groupName, name);
                if (entry != null) group.put(entry);
            }
        }
        return group;
    }

    public void updateGroup(HLogGroup group, boolean updateChild) throws Exception {
        String path = getGroupPath(group.getGroupName());
        Stat stat = zoo.exists(path, false);
        if (stat != null) {
            try {
                zoo.setData(path, getGroupData(group), stat.getVersion());
                // set time error ( no pb )
            } catch (Exception e) {
            }
            if (updateChild) {
                HLogEntry tmpEntry;
                for (HLogEntry entry : group.getEntrys()) {
                    tmpEntry = getEntry(entry.getGroupName(), entry.getName());
                    if (tmpEntry == null) {
                        createEntry(entry);
                    } else {
                        // 更新和修复数据
                        if (tmpEntry.getType() != HLogEntry.Type.END) {
                            if (tmpEntry.getType() != entry.getType()) {
                                tmpEntry.setType(entry.getType());
                                updateEntry(tmpEntry);
                            }
                        } else {
                            if (tmpEntry.getPos() <= 0) {
                                tmpEntry.setType(entry.getType());
                                updateEntry(tmpEntry);
                            }
                        }
                    }
                }
            }
        }
    }

    public void createOrUpdateGroup(HLogGroup group, boolean updateChild) throws Exception {
        HLogGroup tmpGroup = getGroupByName(group.getGroupName(), false);
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
        return lockGroup(groupName, false);
    }

    protected boolean lockGroup(String groupName, boolean is) throws Exception {
        if (!isLockGroup(groupName)) {
            try {
                zoo.create(getGroupLockPath(groupName), getGroupLockData(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    protected byte[] getGroupLockData() {
        return Bytes.toBytes(getName());
    }

    protected String setGroupLockData(byte[] data) {
        return Bytes.toString(data);
    }

    public void unlockGroup(String groupName) throws Exception {
        if (isMeLockGroup(groupName)) {
            Stat stat = getLockGroupStat(groupName);
            if (stat == null) return;
            try {
                String path = getGroupLockPath(groupName);
                zoo.delete(path, stat.getVersion());
            } catch (Exception e) {
                return;
            }
        }
    }

    private byte[] getEntryData(HLogEntry entry) {
        String data = entry.getPos() + DATA_SPLIT + entry.getLastVerifiedPos() + DATA_SPLIT
                      + entry.getType().getTypeValue() + DATA_SPLIT + entry.getLastReadtime();
        return Bytes.toBytes(data);
    }

    /**
     * 请与 getEntryData 保持一致
     * 
     * @param entry
     * @param data
     */
    private void setEntryData(HLogEntry entry, byte[] data) {
        if (data != null && entry != null) {
            String dataString = Bytes.toString(data);
            String[] idxs = StringUtils.split(dataString, DATA_SPLIT);
            setEntryDataVersion(idxs, entry);
        }
    }

    protected static void setEntryDataVersion(String[] idxs, HLogEntry entry) {
        // len 3
        if (idxs.length == 3) {
            entry.setPos(Long.parseLong(idxs[0]));
            entry.setType(Type.toType(Integer.valueOf(idxs[1])));
            entry.setLastReadtime(Long.parseLong(idxs[2]));
        }
        // len4
        if (idxs.length == 4) {
            entry.setPos(Long.parseLong(idxs[0]));
            entry.setLastVerifiedPos(Long.parseLong(idxs[1]));
            entry.setType(Type.toType(Integer.valueOf(idxs[2])));
            entry.setLastReadtime(Long.parseLong(idxs[3]));
        }
    }

    private String getEntryPath(String groupName, String name) {
        return pnode.getPath() + "/" + groupName + "/" + name;
    }

    private String getEntryPath(HLogEntry entry) {
        return getEntryPath(entry.getGroupName(), entry.getName());
    }

    //
    //
    //
    protected byte[] getGroupData(HLogGroup group) {
        return Bytes.toBytes(group.getLastOperatorTime());
    }

    /**
     * 请与 getGroupData 保持一致性
     * 
     * @param entry
     * @param data
     */
    protected void setGroupData(HLogGroup entry, byte[] data) {
        if (entry != null && data != null) {
            entry.setLastOperatorTime(Bytes.toLong(data));
        }
    }

    protected String getGroupPath(String groupName) {
        return pnode.getPath() + "/" + groupName;
    }

    protected String getGroupLockPath(String groupName) {
        return pnode.getPath() + "/" + groupName + "/" + ProducerConstants.ZOO_PERSISTENCE_HLOG_GROUP_LOCK;
    }

    protected Stat getGroupStat(HLogGroup group) throws Exception {
        String lockpath = getGroupPath(group.getGroupName());
        return zoo.exists(lockpath, false);
    }

    protected Stat getLockGroupStat(String groupName) throws Exception {
        HLogGroup group = getGroupByName(groupName, false);
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

    @Override
    public void init(Configuration conf) throws Exception {
        ZNode root = HRepConfigUtil.getRootZNode(conf);
        Stat stat = zoo.exists(root.getPath(), false);
        if (stat == null) {
            zoo.create(root.getPath(), null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
        pnode = new ZNode(root, ProducerConstants.ZOO_PERSISTENCE_HLOG_GROUP);

        stat = zoo.exists(pnode.getPath(), false);
        if (stat == null) {
            zoo.create(pnode.getPath(), null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
    }

    public boolean isMeLockGroup(String groupName) throws Exception {
        String path = getGroupLockPath(groupName);
        Stat stat = zoo.exists(path, false);
        if (stat != null) {
            String name = null;
            try {
                name = setGroupLockData(zoo.getData(path, false, stat));
            } catch (Exception e) {
                return false;
            }
            if (name != null) {
                if (name.equals(getName())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void deleteGroup(String groupName) throws Exception {
        String groupPath = getGroupPath(groupName);
        Stat stat = zoo.exists(groupPath, false);
        if (stat != null) {
            Stat cstat;
            String childPath;
            for (String child : zoo.getChildren(groupPath, false)) {
                childPath = groupPath + "/" + child;
                cstat = zoo.exists(childPath, false);
                if (cstat != null) zoo.delete(childPath, cstat.getVersion());
            }
            stat = zoo.exists(groupPath, false);
            zoo.delete(groupPath, stat.getVersion());
        }
    }

    public AdvZooKeeper getZoo() {
        return zoo;
    }

    public void setZoo(AdvZooKeeper zoo) {
        this.zoo = zoo;
    }
}
