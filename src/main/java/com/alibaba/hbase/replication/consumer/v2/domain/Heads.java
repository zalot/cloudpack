package com.alibaba.hbase.replication.consumer.v2.domain;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.hbase.replication.protocol.ProtocolHead;

/**
 * v2 数据结构 类Heads.java的实现描述：TODO 类实现描述
 * 
 * @author zalot.zhaoh Mar 22, 2012 11:28:51 AM
 */
public class Heads {

    protected Map<String, List<ProtocolHead>> heads = new ConcurrentHashMap<String, List<ProtocolHead>>();

    public Heads(){
        
    }

    public void put(ProtocolHead head) {
        head.getGroupName();
    }
}
