package com.alibaba.hbase.replication.hlog.domain;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.regionserver.wal.HLog;

import com.alibaba.hbase.replication.utility.ProducerConstants;

/**
 * 支持排序的 HlogEntry
 * 
 * 类HLogEntry.java的实现描述：TODO 类实现描述 
 * @author zalot.zhaoh Mar 19, 2012 10:52:37 AM
 */
public class HLogEntry implements Comparable<HLogEntry> {

    @Override
    public String toString() {
        return "HLogEntry [groupName=" + groupName + ", timestamp=" + timestamp + ", type=" + type
               + ", pos=" + pos + "]";
    }

    /**
     * 日志类型
     * 
     * @author zalot.zhaoh
     */
    public static enum Type {
        LIFE(1), OLD(2), END(3), UNKNOW(255);

        int typeValue;

        Type(int typeValue){
            this.typeValue = typeValue;
        }

        public int getTypeValue() {
            return this.typeValue;
        }

        public static Type toType(int typeValue) {
            for (Type cp : Type.values()) {
                if (cp.getTypeValue() == typeValue) return cp;
            }
            return LIFE;
        }

        public static Type toType(Path path) {
            if (path != null && HLog.validateHLogFilename(path.getName())) {
                String url = path.toUri().getRawPath();
                if (url.indexOf(ProducerConstants.PATH_BASE_HLOG) > 0) {
                    return Type.LIFE;
                } else if (url.indexOf(ProducerConstants.PATH_BASE_OLDHLOG) > 0) {
                    return Type.OLD;
                }
            }
            return Type.UNKNOW;
        }
    }

    // groupName + timestamp = name
    protected String groupName;

    // groupName + timestamp = name
    protected long   timestamp;

    protected long   lastReadtime = 0;

    public long getLastReadtime() {
        return lastReadtime;
    }

    public void setLastReadtime(long lastReadtime) {
        this.lastReadtime = lastReadtime;
    }

    protected Type type = Type.UNKNOW;

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    // pos
    private long pos = 0;

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public long getPos() {
        return pos;
    }

    public void setPos(long pos) {
        this.pos = pos;
    }

    public HLogEntry(){
    }
    
    public HLogEntry(Path path){
        this(path.getName());
        this.type = HLogEntry.Type.toType(path);
    }

    public HLogEntry(String name){
        if (HLog.validateHLogFilename(name)) {
            int idx = name.lastIndexOf(".");
            this.groupName = name.substring(0, idx);
            this.timestamp = Long.parseLong(name.substring(idx + 1, name.length()));
        } else {
            throw new RuntimeException("[ERROR] HLog file name is [" + getName() + "]");
        }
    }

    public String getName() {
        return groupName + "." + timestamp;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public int compareTo(HLogEntry o) {
        if (this.getTimestamp() > o.getTimestamp()) return 1;
        if (o.getTimestamp() == this.getTimestamp()) return 0;
        return -1;
    }
}
