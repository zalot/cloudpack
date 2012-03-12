package com.alibaba.hbase.replication.persistence;

import java.util.List;

import org.apache.hadoop.conf.Configurable;
import org.apache.hadoop.conf.Configuration;

import com.alibaba.hbase.replication.hlog.domain.HLogEntry;
import com.alibaba.hbase.replication.hlog.domain.HLogEntryGroup;

@Deprecated
public interface HLogPersistence extends Configurable {

    public void createEntry(HLogEntry entry) throws Exception;
    public void deleteEntry(HLogEntry entry) throws Exception;
    public void updateEntry(HLogEntry entry) throws Exception;
    public List<HLogEntry> listEntry(String group) throws Exception;
    public HLogEntry getHLogEntry(String groupName, String name) throws Exception;
    
    public void createGroup(HLogEntryGroup group, boolean createChild) throws Exception;
    public HLogEntryGroup getGroupByName(String groupName , boolean getChild) throws Exception;
    public void updateGroup(HLogEntryGroup entry, boolean updateChild) throws Exception;
    public void createOrUpdateGroup(HLogEntryGroup entry, boolean updateChild) throws Exception;
    public boolean isLockGroup(String groupName) throws Exception;
    public boolean lockGroup(String groupName) throws Exception;
    public void unlockGroup(String groupName) throws Exception;
    public List<String> listGroupName() throws Exception;
    
    public void init(Configuration conf) throws Exception;
}
