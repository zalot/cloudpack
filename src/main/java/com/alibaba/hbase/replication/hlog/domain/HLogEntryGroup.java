package com.alibaba.hbase.replication.hlog.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * HLogEntryGroup 代表了一个 EntryGroup
 * 
 * @author zalot.zhaoh
 */
public class HLogEntryGroup {

    protected String groupName;
    protected long   lastOperatorTime;

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

    protected List<HLogEntry> entrys = new ArrayList<HLogEntry>();

    public HLogEntryGroup(String groupName){
        this.groupName = groupName;
    }

    public void put(HLogEntry entry) {
        if (groupName.equalsIgnoreCase(entry.getGroupName())) entrys.add(entry);
    }

    public List<HLogEntry> getEntrys() {
        return entrys;
    }
}
