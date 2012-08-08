package org.sourceopen.hadoop.hbase.replication.core.hlog.domain;

import java.util.List;

import org.apache.hadoop.conf.Configuration;


/**
 * HLog 类HLogPersistence.java的实现描述：TODO 类实现描述
 * 
 * @author zalot.zhaoh Mar 12, 2012 4:09:06 PM
 */
public interface HLogPersistence {

    public void createEntry(HLogEntry entry) throws Exception;

    public void deleteEntry(HLogEntry entry) throws Exception;

    public void updateEntry(HLogEntry entry) throws Exception;

    public List<HLogEntry> listEntry(String group) throws Exception;

    public HLogEntry getEntry(String groupName, String name) throws Exception;

    public void createGroup(HLogGroup group, boolean createChild) throws Exception;

    public HLogGroup getGroupByName(String groupName, boolean getChild) throws Exception;

    public void updateGroup(HLogGroup entry, boolean updateChild) throws Exception;

    public void createOrUpdateGroup(HLogGroup entry, boolean updateChild) throws Exception;

    public void deleteGroup(String groupName) throws Exception;

    public boolean isLockGroup(String groupName) throws Exception;

    public boolean lockGroup(String groupName) throws Exception;

    public void unlockGroup(String groupName) throws Exception;

    public List<String> listGroupName() throws Exception;

    public void init(Configuration conf) throws Exception;
}
