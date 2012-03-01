package com.alibaba.hbase.replication.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * HLog 组，给HLogs 使用
 * 
 * @author zalot.zhaoh
 */
public class HLogEntryGroup {

    protected String groupName;

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    protected List<HLogEntry> entrys = new ArrayList<HLogEntry>();

    public HLogEntryGroup(String groupName){
        this.groupName = groupName;
    }

    public void put(HLogEntry entry) {
        if(groupName.equalsIgnoreCase(entry.getGroupName()))
            entrys.add(entry);
    }
    
    public List<HLogEntry> getEntrys(){
        return entrys;
    }
}
