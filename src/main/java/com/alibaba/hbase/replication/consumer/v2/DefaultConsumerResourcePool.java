package com.alibaba.hbase.replication.consumer.v2;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.hbase.replication.consumer.v2.domain.Heads;
import com.alibaba.hbase.replication.protocol.ProtocolHead;
import com.alibaba.hbase.replication.protocol.ProtocolAdapter;

/**
 * v2 版 Consumer
 * 
 * 类DefaultConsumerResourcePool.java的实现描述：TODO 类实现描述 
 * @author zalot.zhaoh Mar 22, 2012 11:28:26 AM
 */
public class DefaultConsumerResourcePool implements ConsumerResourcePool {

    ProtocolAdapter    protocolAdapter;
    Map<String, Heads> heads = new ConcurrentHashMap<String, Heads>();

    public ProtocolAdapter getProtocolAdapter() {
        return protocolAdapter;
    }

    public void setProtocolAdapter(ProtocolAdapter protocolAdapter) {
        this.protocolAdapter = protocolAdapter;
    }

    public void flush(){
        try {
            protocolAdapter.listHead();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<String> listGroup() {
        return null;
    }

    @Override
    public List<ProtocolHead> listHead(String groupName) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean lockGroup(String groupName) {
        // TODO Auto-generated method stub
        return false;
    }

}
