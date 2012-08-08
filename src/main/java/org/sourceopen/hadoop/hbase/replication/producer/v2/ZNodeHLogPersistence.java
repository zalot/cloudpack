package org.sourceopen.hadoop.hbase.replication.producer.v2;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.regionserver.wal.HLog;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;
import org.sourceopen.hadoop.hbase.replication.core.hlog.domain.HLogEntry;
import org.sourceopen.hadoop.hbase.replication.core.hlog.domain.HLogEntry.Type;
import org.sourceopen.hadoop.hbase.replication.core.hlog.domain.HLogGroup;
import org.sourceopen.hadoop.hbase.replication.core.hlog.domain.HLogPersistence;
import org.sourceopen.hadoop.hbase.replication.utility.ProducerConstants;
import org.sourceopen.hadoop.zookeeper.connect.AdvZooKeeper;
import org.sourceopen.hadoop.zookeeper.connect.RecoverableZooKeeper;
import org.sourceopen.hadoop.zookeeper.core.ZNode;
import org.sourceopen.hadoop.zookeeper.core.ZNodeFactory;

/**
 * HLogPersistence 持久化操作
 * 
 * @author zalot.zhaoh Mar 7, 2012 10:24:18 AM
 */
public class ZNodeHLogPersistence implements HLogPersistence {

    protected static final String SPLIT = "|";
    protected static final Log    LOG   = LogFactory.getLog(ZNodeHLogPersistence.class);
    protected AdvZooKeeper        zookeepr;
    protected ZNode               groot;

    // public ArrayList<ACL> perms;

    public void setZookeeper(RecoverableZooKeeper zoo) {
        this.zookeepr = zoo;
    }

    public ZNodeHLogPersistence(Configuration conf, AdvZooKeeper zoo) throws Exception{
        this.zookeepr = zoo;
        init(conf);
    }

    public void createEntry(HLogEntry entry) throws Exception {
        ZNode g = groot.getChild(entry.getGroupName());
        if (g != null) {
            g.addChild(entry.getName());
            g.setData(getEntryData(entry));
        }
    }

    public void deleteEntry(HLogEntry entry) throws Exception {
        ZNode g = groot.getChild(entry.getGroupName());
        if (g != null) {
            g.delChild(entry.getName());
        }
    }

    public void updateEntry(HLogEntry entry) throws Exception {
        if (entry.getType() == HLogEntry.Type.NOFOUND) {
            deleteEntry(entry);
            return;
        }
        String path = getEntryPath(entry);
        Stat stat = zookeepr.exists(path, false);
        if (stat != null) {
            zookeepr.setData(path, getEntryData(entry), stat.getVersion());
        }
    }

    @SuppressWarnings("unchecked")
    public List<HLogEntry> listEntry(String groupName) throws Exception {
        List<ZNode> path = groot.getChild(groupName).getChilds();
        Stat stat = zookeepr.exists(path, false);
        if (stat == null) return Collections.EMPTY_LIST;
        List<HLogEntry> entrys = new ArrayList<HLogEntry>();
        List<String> ls = zookeepr.getChildren(path, false);
        HLogEntry entry = null;
        for (String name : ls) {
            entry = getEntry(groupName, name);
            if (entry != null) entrys.add(entry);
        }
        return entrys;
    }

    public HLogEntry pkZNode2HLogEntry(ZNode zn) throws Exception {
        HLogEntry entry = new HLogEntry(zn.getName());
        setEntryData(entry, zn.getData());
        return entry;
    }

    public HLogEntry getEntry(String groupName, String name) throws Exception {
        if (!HLog.validateHLogFilename(name)) {
            return null;
        }
        ZNode g = groot.getChild(groupName);
        if (g != null) {
            ZNode e = g.getChild(name);
            return pkZNode2HLogEntry(e);
        }
        return null;
    }

    public List<String> listGroupName() throws KeeperException, InterruptedException {
        List<String> childs = new ArrayList<String>();
        for (ZNode g : groot.getChilds()) {
            childs.add(g.getName());
        }
        return childs;
    }

    public void createGroup(HLogGroup group, boolean createChild) throws Exception {
        ZNode g = groot.addChild(group.getGroupName());
        g.setData(getGroupData(group));
        if (createChild) {
            for (HLogEntry entry : group.getEntrys()) {
                createEntry(entry);
            }
        }
    }

    public HLogGroup getGroupByName(String groupName, boolean getChild) throws Exception {
        String path = createGroup(groupName);
        Stat stat = zookeepr.exists(path, false);
        if (stat == null) return null;
        HLogGroup group = new HLogGroup(groupName);
        setGroupData(group, zookeepr.getData(path, false, stat));
        if (getChild) {
            List<String> ls = zookeepr.getChildren(path, false);
            HLogEntry entry = null;
            for (String name : ls) {
                entry = getEntry(groupName, name);
                if (entry != null) group.put(entry);
            }
        }
        return group;
    }

    public void updateGroup(HLogGroup group, boolean updateChild) throws Exception {
        String path = createGroup(group.getGroupName());
        Stat stat = zookeepr.exists(path, false);
        if (stat != null) {
            try {
                zookeepr.setData(path, getGroupData(group), stat.getVersion());
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
                zookeepr.create(getGroupLockPath(groupName), getGroupLockData(), Ids.OPEN_ACL_UNSAFE,
                                CreateMode.EPHEMERAL);
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
        Stat stat = getLockGroupStat(groupName);
        if (stat == null) return;
        try {
            String path = getGroupLockPath(groupName);
            zookeepr.delete(path, stat.getVersion());
        } catch (Exception e) {
            return;
        }
    }

    private byte[] getEntryData(HLogEntry entry) {
        String data = entry.getPos() + SPLIT + entry.getLastVerifiedPos() + SPLIT + entry.getType().getTypeValue()
                      + SPLIT + entry.getLastReadtime();
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
            String[] idxs = StringUtils.split(dataString, SPLIT);
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

    protected ZNode createGroup(String groupName) {
        return groot.addChild(groupName);
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
        String rootName = conf.get(ProducerConstants.CONFKEY_ROOT_ZOO, ProducerConstants.ROOT_ZOO);
        ZNodeFactory.createZNode(zookeepr, rootName);
        groot = groot.addChild(ProducerConstants.ZOO_PERSISTENCE_HLOG_GROUP);
    }

    @Override
    public void deleteGroup(String groupName) throws Exception {
        String groupPath = createGroup(groupName);
        Stat stat = zookeepr.exists(groupPath, false);
        if (stat != null) {
            Stat cstat;
            String childPath;
            for (String child : zookeepr.getChildren(groupPath, false)) {
                childPath = groupPath + "/" + child;
                cstat = zookeepr.exists(childPath, false);
                if (cstat != null) zookeepr.delete(childPath, cstat.getVersion());
            }
            stat = zookeepr.exists(groupPath, false);
            zookeepr.delete(groupPath, stat.getVersion());
        }
    }
}
