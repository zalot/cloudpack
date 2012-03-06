package com.alibaba.hbase.replication.hlog.domain;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;


/**
 * 默认 HLogs 实现类， ( 后期可以优化 ) 类DefaultHLogs.java的实现描述：TODO 类实现描述
 * 
 * @author zalot.zhaoh Feb 28, 2012 4:27:35 PM
 */
public class HLogEntryGroups {
    // name , path
    protected Map<String, HLogEntryGroup> groups = new ConcurrentHashMap<String, HLogEntryGroup>();

    protected Configuration          conf   = null;

    public Path get(String name) {
        Path path = null;
        return path;
    }

    public void put(Path path) {
        if (path == null || path.getName().length() <= 0) return;
        HLogEntry entry = new HLogEntry(path);
        putGroup(entry);
    }

    protected void putGroup(HLogEntry entry) {
        HLogEntryGroup group = groups.get(entry.getGroupName());
        if (group == null) {
            group = new HLogEntryGroup(entry.getGroupName());
            groups.put(entry.getGroupName(), group);
        }
        group.put(entry);
    }

    public void put(List<Path> paths) {
        if (paths == null || paths.size() <= 0) return;
        for (Path path : paths) {
            put(path);
        }
    }

    public void clear() {
        groups.clear();
    }

    public int size() {
        return groups.size();
    }

    public Collection<HLogEntryGroup> getGroups() {
        return groups.values();
    }
}
