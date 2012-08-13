package org.sourceopen.hadoop.hbase.replication.consumer.v2;

import java.util.List;

import org.sourceopen.hadoop.hbase.replication.protocol.ProtocolAdapter;
import org.sourceopen.hadoop.hbase.replication.protocol.ProtocolHead;
import org.sourceopen.hadoop.zookeeper.connect.RecoverableZooKeeper;

public class ConsumerZookeeperResourcePool implements ConsumerResourcePool {

    protected ProtocolAdapter      protocolAdapter;
    protected RecoverableZooKeeper zookeeper;

    public RecoverableZooKeeper getZookeeper() {
        return zookeeper;
    }

    public void setZookeeper(RecoverableZooKeeper zookeeper) {
        this.zookeeper = zookeeper;
    }

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
