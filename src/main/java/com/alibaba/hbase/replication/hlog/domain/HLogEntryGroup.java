package com.alibaba.hbase.replication.hlog.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * HLogEntryGroup 代表了一个 EntryGroup
 * 
 * @author zalot.zhaoh
 */
public class HLogEntryGroup {

    protected String groupName;
    protected long   lastOperatorTime;
    protected List<HLogEntry> entrys = new ArrayList<HLogEntry>();

    public long getLastOperatorTime() {
        return lastOperatorTime;
    }

    public void setLastOperatorTime(long lastOperatorTime) {
        this.lastOperatorTime = lastOperatorTime;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }


    public HLogEntryGroup(String groupName){
        this.groupName = groupName;
    }

    public void put(HLogEntry entry) {
        if (groupName.equalsIgnoreCase(entry.getGroupName())) entrys.add(entry);
    }

    public List<HLogEntry> getEntrys() {
        Collections.sort(entrys);
        return entrys;
    }
    
    public boolean contains(HLogEntry entry){
        return entrys.contains(entry);
    }
}
