package com.alibaba.hbase.replication.hlog;

import java.util.List;

import org.apache.hadoop.conf.Configuration;

import com.alibaba.hbase.replication.hlog.domain.HLogEntry;
import com.alibaba.hbase.replication.hlog.domain.HLogEntryGroup;

/**
 * HLog
 * 
 * 类HLogPersistence.java的实现描述：TODO 类实现描述 
 * @author zalot.zhaoh Mar 12, 2012 4:09:06 PM
 */
public interface HLogEntryPoolPersistence{
    public void createEntry(HLogEntry entry) throws Exception;
    public void deleteEntry(HLogEntry entry) throws Exception;
    public void updateEntry(HLogEntry entry) throws Exception;
    public List<HLogEntry> listEntry(String group) throws Exception;
    public HLogEntry getHLogEntry(String groupName, String name) throws Exception;
    
    public void createGroup(HLogEntryGroup group, boolean createChild) throws Exception;
    public HLogEntryGroup getGroupByName(String groupName , boolean getChild) throws Exception;
    public void updateGroup(HLogEntryGroup entry, boolean updateChild) throws Exception;
    public void createOrUpdateGroup(HLogEntryGroup entry, boolean updateChild) throws Exception;
    public void deleteGroup(String groupName) throws Exception;
    public boolean isLockGroup(String groupName) throws Exception;
    public boolean isMeLockGroup(String groupName) throws Exception;
    public boolean lockGroup(String groupName) throws Exception;
    public void unlockGroup(String groupName) throws Exception;
    public List<String> listGroupName() throws Exception;
    
    public void init(Configuration conf) throws Exception;
}
