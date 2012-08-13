package org.sourceopen.hadoop.hbase.replication.protocol;

import org.sourceopen.common.utils.ZipUtil;
import org.sourceopen.hadoop.hbase.replication.protocol.protobuf.BodySerializingHandler;

/**
 * 为适应 第一版的 consumer 而封装的 Body1 Protocol 类Body1.java的实现描述：TODO 类实现描述
 * 
 * @author zalot.zhaoh Mar 22, 2012 1:13:39 PM
 */
public class ProtocolBodyV2 extends ProtocolBodyV1 {

    @Override
    public void setBodyData(byte[] data) throws Exception {
        this.serBody = BodySerializingHandler.deserialize(ZipUtil.ungzip(data));
    }

    @Override
    public byte[] getBodyData() throws Exception {
        return ZipUtil.gzip(BodySerializingHandler.serialize(serBody));
    }
}
