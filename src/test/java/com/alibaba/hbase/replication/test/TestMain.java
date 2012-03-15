package com.alibaba.hbase.replication.test;

import org.apache.hadoop.conf.Configuration;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.InjectInto;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBeanByName;
import org.unitils.spring.annotation.SpringBeanByType;

import com.alibaba.hbase.replication.consumer.FileChannelManager;
import com.alibaba.hbase.replication.hlog.HLogEntryZookeeperPersistence;
import com.alibaba.hbase.replication.hlog.HLogService;
import com.alibaba.hbase.replication.producer.HLogGroupZookeeperScanner;
import com.alibaba.hbase.replication.producer.crossidc.HReplicationProducer;
import com.alibaba.hbase.replication.protocol.DefaultHDFSFileAdapter;
import com.alibaba.hbase.replication.utility.ZKUtil;
import com.alibaba.hbase.replication.zookeeper.NothingZookeeperWatch;
import com.alibaba.hbase.replication.zookeeper.RecoverableZooKeeper;

@RunWith(UnitilsJUnit4TestClassRunner.class)
@SpringApplicationContext("classpath*:META-INF/spring/context.xml")
public class TestMain extends BaseReplicationTest{
    

    @Mock
    @InjectInto(target = "fileChannelManager", property = "fileAdapter")
    private DefaultHDFSFileAdapter         fileAdapter;
    @SpringBeanByType
    private FileChannelManager  fileChannelManager;
    @SpringBeanByName
    private Configuration       consumerConf;
    
    
    @BeforeClass
    public static void init() throws Exception{
        init1();
        init2();
    }
    
    public void testCrossIDCReplication() throws Exception{
        fileAdapter.init(conf1);
        HLogService service = new HLogService(conf1);
        RecoverableZooKeeper zk = ZKUtil.connect(conf1, new NothingZookeeperWatch());
        
        HLogEntryZookeeperPersistence dao = new HLogEntryZookeeperPersistence(conf1 ,zk);
        
        HLogGroupZookeeperScanner scan = new HLogGroupZookeeperScanner(conf1);
        scan.setHlogEntryPersistence(dao);
        scan.setHlogService(service);
        scan.setZooKeeper(zk);
        
        Thread threadscan = new Thread(scan);
        threadscan.start();
        
        HReplicationProducer pro = new HReplicationProducer(conf1);
        pro.setAdapter(fileAdapter);
        pro.setHlogEntryPersistence(dao);
        pro.setHlogService(service);
        
        Thread threadpro = new Thread(pro);
        threadpro.start();
    }
}
