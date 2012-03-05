package com.alibaba.hbase.replication.zookeeper;

import org.apache.hadoop.hbase.zookeeper.RecoverableZooKeeper;

/**
 * HLogZnode 操作
 * 
 * @author zalot.zhaoh
 */
public class ZooKeeperOperator {
    
    protected static RecoverableZooKeeper zoo = null;

    public static RecoverableZooKeeper getInstance() {
        if(zoo == null){
            synchronized(zoo){
                
            }
        }
        return null;
    }

    public static RecoverableZooKeeper newInstance() {
        return null;
    }
}
