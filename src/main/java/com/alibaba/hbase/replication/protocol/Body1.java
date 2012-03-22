package com.alibaba.hbase.replication.protocol;

import java.util.List;
import java.util.Map;

import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.KeyValue.Type;

import com.alibaba.hbase.replication.protocol.protobuf.BodySerializingHandler;
import com.alibaba.hbase.replication.protocol.protobuf.SerBody;
import com.alibaba.hbase.replication.protocol.protobuf.SerBody.Edit;
import com.alibaba.hbase.replication.utility.HLogUtil;

/**
 * 为适应 第一版的 consumer 而封装的 Body1 Protocol
 * 
 * 类Body1.java的实现描述：TODO 类实现描述 
 * @author zalot.zhaoh Mar 22, 2012 1:13:39 PM
 */
public class Body1 extends Body {

    protected SerBody serBody = new SerBody();

    @Override
    public void setBodyData(byte[] data) throws Exception {
        this.serBody = BodySerializingHandler.deserialize(HLogUtil.ungzip(data));
    }

    @Override
    public byte[] getBodyData() throws Exception {
        return HLogUtil.gzip(BodySerializingHandler.serialize(serBody));
    }

    @Override
    public void putKeyValue(String tableName, KeyValue kv) {
        Edit edit = new Edit();
        switch (Type.codeToType(kv.getType())) {
            case DeleteFamily:
                edit.setType(SerBody.Type.DeleteFamily);
                break;
            case DeleteColumn:
                edit.setType(SerBody.Type.DeleteColumn);
                break;
            case Delete:
                edit.setType(SerBody.Type.Delete);
                break;
            case Put:
                edit.setType(SerBody.Type.Put);
                break;
        }
        edit.setFamily(kv.getFamily());
        edit.setQualifier(kv.getQualifier());
        edit.setRowKey(kv.getRow());
        edit.setValue(kv.getValue());
        edit.setTimeStamp(kv.getTimestamp());
        serBody.addEdit(tableName, edit);
    }

    public Map<String, List<Edit>> getEditMap() {
        return serBody.getEditMap();
    }
}
