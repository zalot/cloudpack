package org.sourceopen.hadoop.hbase.replication.consumer.v2;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.sourceopen.hadoop.hbase.replication.consumer.v2.domain.Heads;
import org.sourceopen.hadoop.hbase.replication.protocol.ProtocolAdapter;
import org.sourceopen.hadoop.hbase.replication.protocol.ProtocolHead;
import org.sourceopen.hadoop.hbase.replication.zookeeper.RecoverableZooKeeper;

public class ConsumerZookeeperResourcePool implements ConsumerResourcePool{

    protected ProtocolAdapter    protocolAdapter;
    protected RecoverableZooKeeper zookeeper;
    
    public RecoverableZooKeeper getZookeeper() {
        return zookeeper;
    }

    
    public void setZookeeper(RecoverableZooKeeper zookeeper) {
        this.zookeeper = zookeeper;
    }

    Map<String, Heads> heads = new ConcurrentHashMap<String, Heads>();

    public ProtocolAdapter getProtocolAdapter() {
        return protocolAdapter;
    }

    public void setProtocolAdapter(ProtocolAdapter protocolAdapter) {
        this.protocolAdapter = protocolAdapter;
    }

    
    @Override
    public List<String> listGroup() {
        return null;
    }

    @Override
    public List<ProtocolHead> listHeadByGroupName(String groupName) {
        return null;
    }

    @Override
    public boolean lockGroup(String groupName) {
        return false;
    }

}
