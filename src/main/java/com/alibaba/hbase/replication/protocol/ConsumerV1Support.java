package com.alibaba.hbase.replication.protocol;

import java.util.List;
import java.util.Map;

import com.alibaba.hbase.replication.protocol.protobuf.SerBody.Edit;

/**
 * 只是为了兼容第一版本的 Consumer <BR>
 * 类ConsumerV1Support.java的实现描述：TODO 类实现描述
 * 
 * @author zalot.zhaoh Apr 1, 2012 11:10:55 AM
 */
public abstract class ConsumerV1Support implements ProtocolBody{

    public abstract Map<String, List<Edit>> getEditMap();
}
