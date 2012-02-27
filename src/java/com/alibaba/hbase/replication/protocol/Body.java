package com.alibaba.hbase.replication.protocol;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;

/**
 * 类Body.java的实现描述：中间文件的文件体对象
 * 
 * @author dongsh 2012-2-27 下午03:47:32
 */
public class Body {

    /**
     * UUID
     */
    private String                        clusterId;

    /**
     * <tableName,editList>
     */
    private final Map<String, List<Edit>> editMap = new HashMap<String, List<Edit>>();

    public Map<String, List<Edit>> getEditMap() {
        return editMap;
    }

    /**
     * @param clusterId not blank
     */
    public Body(String clusterId){
        if (StringUtils.isBlank(clusterId)) {
            throw new RuntimeException("Please init Body with a clusterId of the producer.");
        }
        this.clusterId = clusterId;
    }

    public String getClusterId() {
        return clusterId;
    }

    /**
     * thread-safe
     * 
     * @param tableName not blank
     * @param edit not null
     */
    public void addEdit(String tableName, Edit edit) {
        if (StringUtils.isBlank(tableName)) {
            throw new RuntimeException("add Edit whit blank tableName. edit :" + edit);
        }
        if (edit != null) {
            List<Edit> editList = editMap.get(tableName);
            if (editList == null) {
                synchronized (editMap) {
                    if (editList == null) {
                        // XXX CopyOnWriteArrayist？
                        editList = new Vector<Body.Edit>();
                        editMap.put(tableName, editList);
                    }
                }
                editList = editMap.get(tableName);
            }
            editList.add(edit);
        }
    }

    public static class Edit {

        private Type   type;
        private byte[] rowKey;
        private byte[] family;
        private byte[] qualifier;
        private byte[] value;
        private long   timeStamp;

        public Type getType() {
            return type;
        }

        public void setType(Type type) {
            this.type = type;
        }

        public byte[] getRowKey() {
            return rowKey;
        }

        public void setRowKey(byte[] rowKey) {
            this.rowKey = rowKey;
        }

        public byte[] getFamily() {
            return family;
        }

        public void setFamily(byte[] family) {
            this.family = family;
        }

        public byte[] getQualifier() {
            return qualifier;
        }

        public void setQualifier(byte[] qualifier) {
            this.qualifier = qualifier;
        }

        public byte[] getValue() {
            return value;
        }

        public void setValue(byte[] value) {
            this.value = value;
        }

        public long getTimeStamp() {
            return timeStamp;
        }

        public void setTimeStamp(long timeStamp) {
            this.timeStamp = timeStamp;
        }

    }

    public static enum Type {
        Put(1), Delete(2), DeleteColumn(3), DeleteFamily(4);

        /**
         * 0不使用，因爲這是protobuffer的int默認值
         */
        int code;

        private Type(int code){
            this.code = code;
        }

        public int getCode() {
            return code;
        }

        public static Type valueOfCode(int code) {
            for (Type elem : values()) {
                if (elem.getCode() == code) {
                    return elem;
                }
            }
            return null;
        }
    }

}
