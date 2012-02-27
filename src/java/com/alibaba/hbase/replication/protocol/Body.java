package com.alibaba.hbase.replication.protocol;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * 类Body.java的实现描述：中间文件的文件体对象
 * @author dongsh 2012-2-27 下午03:47:32
 */
public class Body {

    private final List<Edit> editList = new ArrayList<Body.Edit>();

    public List<Edit> getEditList() {
        return editList;
    }

    public static class Edit {

        private Type   type;
        private String clusterId;
        private String tableName;
        private String rowKey;
        private String family;
        private String qualifier;
        private byte[] value;

        public Type getType() {
            return type;
        }

        public void setType(Type type) {
            this.type = type;
        }

        public String getClusterId() {
            return clusterId;
        }

        public void setClusterId(String clusterId) {
            this.clusterId = clusterId;
        }

        public String getRowKey() {
            return rowKey;
        }

        public void setRowKey(String rowKey) {
            this.rowKey = rowKey;
        }

        public String getFamily() {
            return family;
        }

        public void setFamily(String family) {
            this.family = family;
        }

        public String getQualifier() {
            return qualifier;
        }

        public void setQualifier(String qualifier) {
            this.qualifier = qualifier;
        }

        public byte[] getValue() {
            return value;
        }

        public void setValue(byte[] value) {
            this.value = value;
        }

        public String getTableName() {
            return tableName;
        }

        public void setTableName(String tableName) {
            this.tableName = tableName;
        }

    }

    public static enum Type {
        Put(0), Delete(1), DeleteColumn(2), DeleteFamily(3);

        int code;

        private Type(int code){
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }

}
