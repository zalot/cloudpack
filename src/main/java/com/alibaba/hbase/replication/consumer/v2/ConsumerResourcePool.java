package com.alibaba.hbase.replication.consumer.v2;

import java.util.List;

import com.alibaba.hbase.replication.protocol.ProtocolHead;

/**
 * v2 Consumer 资源池
 * 
 * 类ConsumerResourcePool.java的实现描述：TODO 类实现描述 
 * @author zalot.zhaoh Mar 22, 2012 11:28:37 AM
 */
public interface ConsumerResourcePool {

    public List<String> listGroup();

    public List<ProtocolHead> listHeadByGroupName(String groupName);

    public boolean lockGroup(String groupName);
}
