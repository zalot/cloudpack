package com.alibaba.hbase.replication.protocol;

import com.alibaba.hbase.replication.protocol.protobuf.BodySerializingHandler;
import com.alibaba.hbase.replication.protocol.protobuf.SerBody;
import com.alibaba.hbase.replication.utility.HLogUtil;

/**
 * 为适应 第一版的 consumer 而封装的 Body1 Protocol
 * 
 * 类Body1.java的实现描述：TODO 类实现描述 
 * @author zalot.zhaoh Mar 22, 2012 1:13:39 PM
 */
public class ProtocolBodyV2 extends ProtocolBodyV1 {

    protected SerBody serBody = new SerBody();

    @Override
    public void setBodyData(byte[] data) throws Exception {
        this.serBody = BodySerializingHandler.deserialize(HLogUtil.ungzip(data));
    }

    @Override
    public byte[] getBodyData() throws Exception {
        return HLogUtil.gzip(BodySerializingHandler.serialize(serBody));
    }
}
