package com.alibaba.hbase.replication.hlog.domain;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.regionserver.wal.HLog;

import com.alibaba.hbase.replication.utility.ProducerConstants;

public class HLogEntry implements Comparable<HLogEntry> {

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

    // name is file name
    protected String name;

    // groupName + timestamp = name
    protected String groupName;

    // groupName + timestamp = name
    protected long   timestamp;

    protected Type   type = Type.UNKNOW;

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

    public HLogEntry(Path path){
        this(path.getName());
        this.type = HLogEntry.Type.toType(path);
    }
    
    public HLogEntry(String name){
        if (HLog.validateHLogFilename(name)) {
            int idx = name.lastIndexOf(".");
            this.name = name;
            this.groupName = name.substring(0, idx);
            this.timestamp = Long.parseLong(name.substring(idx + 1, name.length()));
        }else{
            throw new RuntimeException("[ERROR] HLog file name is [" + name + "]");
        }
    }

    public String getName() {
        return name;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setName(String name) {
        this.name = name;
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

    // @Override
    // public String getZkPath() {
    // return path.getName();
    // }
    //
    // @Override
    // public byte[] getZkData() {
    // String data = type.getTypeValue() + SEPARATOR + pos;
    // return data.getBytes();
    // }
    //
    // @Override
    // public void setZkPath(String path) {
    // // unsupported
    // }
    //
    // @Override
    // public void setZKData(byte[] data) {
    // if (data != null) {
    // String[] sdata = new String(data).split(SEPARATOR);
    // this.type = HLogType.toType(Integer.valueOf(sdata[0]));
    // this.pos = Long.valueOf(sdata[1]);
    // }
    // }

}
