package com.alibaba.hbase.replication.producer.crossidc;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.zookeeper.KeeperException;

import com.alibaba.hbase.replication.hlog.HLogOperator;
import com.alibaba.hbase.replication.hlog.ZookeeperHLogOperator;
import com.alibaba.hbase.replication.hlog.domain.HLogGroup;
import com.alibaba.hbase.replication.producer.HBaseReplicationSink;
import com.alibaba.hbase.replication.protocol.ProtocolAdapter;

/**
 * 跨节点同步同步管理类
 * 
 * @author zalot.zhao
 */
public class CorssIDCReplicationManager {

    protected static final Log LOG = LogFactory.getLog(CorssIDCReplicationManager.class);
    HLogOperator               hlogOperator;
    Configuration              conf;
    ThreadPoolExecutor         threadPool;
    ProtocolAdapter            adatper;

    public CorssIDCReplicationManager(Configuration conf) throws IOException, KeeperException, InterruptedException{
        hlogOperator = new ZookeeperHLogOperator(conf, null);
        this.conf = conf;
    }

    public void start() throws InterruptedException {
        while (true) {
            try{
                hlogOperator.flush();
                hlogOperator.open();
                Collection<HLogGroup> groups = hlogOperator.getHLogs().getGroups();
                for (HLogGroup group : groups){
                    threadPool.execute(new HBaseReplicationSink(group, hlogOperator, adatper));
                }
            }catch(Exception e){
                
            }
            // 等待同步
            Thread.sleep(60000);
            // 同步结束
            hlogOperator.close();
            // 等待所有结算
            Thread.sleep(5000);
            threadPool.shutdown();
        }
    }

    public static void main(String[] args) throws IOException, KeeperException, InterruptedException {
        Configuration conf = HBaseConfiguration.create();
        CorssIDCReplicationManager manager = new CorssIDCReplicationManager(conf);
        manager.start();
    }

}
