package org.sourceopen.hadoop.hbase.replication.producer.v2;

import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.sourceopen.hadoop.hbase.replication.core.hlog.domain.HLogEntry;
import org.sourceopen.hadoop.hbase.replication.core.hlog.domain.HLogGroup;
import org.sourceopen.hadoop.hbase.replication.core.hlog.domain.HLogPersistence;

/**
 * HLogPersistence 持久化操作
 * 
 * @author zalot.zhaoh Mar 7, 2012 10:24:18 AM
 */
public class ZNodeHLogPersistence implements HLogPersistence {

    @Override
    public void createEntry(HLogEntry entry) throws Exception {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void deleteEntry(HLogEntry entry) throws Exception {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateEntry(HLogEntry entry) throws Exception {
        // TODO Auto-generated method stub
        
    }

    @Override
    public List<HLogEntry> listEntry(String group) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public HLogEntry getEntry(String groupName, String name) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void createGroup(HLogGroup group, boolean createChild) throws Exception {
        // TODO Auto-generated method stub
        
    }

    @Override
    public HLogGroup getGroupByName(String groupName, boolean getChild) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void updateGroup(HLogGroup entry, boolean updateChild) throws Exception {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void createOrUpdateGroup(HLogGroup entry, boolean updateChild) throws Exception {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void deleteGroup(String groupName) throws Exception {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean isLockGroup(String groupName) throws Exception {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean lockGroup(String groupName) throws Exception {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void unlockGroup(String groupName) throws Exception {
        // TODO Auto-generated method stub
        
    }

    @Override
    public List<String> listGroupName() throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void init(Configuration conf) throws Exception {
        // TODO Auto-generated method stub
        
    }
}
