package com.alibaba.hbase.replication.zookeeper;

import org.apache.hadoop.conf.Configuration;
import org.apache.zookeeper.KeeperException;

import com.alibaba.hbase.replication.domain.HLogGroupZNode;

public class DefaultHLogGroupZNodeOperator implements HLogGroupZNodeOperator{

    @Override
    public void create(HLogGroupZNode znode) throws KeeperException, InterruptedException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void delete(HLogGroupZNode znode) throws KeeperException, InterruptedException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public HLogGroupZNode get(String name) throws KeeperException, InterruptedException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean lockGroup(HLogGroupZNode znode) throws KeeperException, InterruptedException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean unlockGroup(HLogGroupZNode znode) throws KeeperException, InterruptedException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void init(Configuration conf) throws KeeperException, InterruptedException {
        // TODO Auto-generated method stub
        
    }
}
