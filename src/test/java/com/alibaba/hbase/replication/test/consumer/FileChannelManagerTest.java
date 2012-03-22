/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.hbase.replication.test.consumer;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableNotFoundException;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.zookeeper.RecoverableZooKeeper;
import org.apache.hadoop.hbase.zookeeper.ZKUtil;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.EasyMockUnitils;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.InjectInto;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBeanByType;

import com.alibaba.hbase.replication.consumer.FileChannelManager;
import com.alibaba.hbase.replication.protocol.DefaultHDFSFileAdapter;
import com.alibaba.hbase.replication.protocol.Head;
import com.alibaba.hbase.replication.protocol.exception.FileParsingException;
import com.alibaba.hbase.replication.protocol.exception.FileReadingException;
import com.alibaba.hbase.replication.protocol.protobuf.SerBody;
import com.alibaba.hbase.replication.utility.ConsumerConstants;
import com.alibaba.hbase.replication.zookeeper.NothingZookeeperWatch;

/**
 * 类FileChannelManagerTest.java的实现描述
 * 
 * @author dongsh 2012-3-5 上午10:58:49
 */
@RunWith(UnitilsJUnit4TestClassRunner.class)
@SpringApplicationContext("classpath*:META-INF/spring/context.xml")
public class FileChannelManagerTest {

    private static final long      DEFAULT_TIMESTAMP = 1330923080;
    private static final String    TABLE             = "FCM";
    @SpringBeanByType
    private FileChannelManager     fileChannelManager;
    @SpringBeanByType
    private Configuration          consumerConf;

    // careful with your asm version!
    @Mock
    @InjectInto(target = "fileChannelManager", property = "fileAdapter")
    private DefaultHDFSFileAdapter fileAdapter;

    Head                           mockHead          = new Head();

    @BeforeClass
    public static void vmSetUp() {
        // //
        // -Djavax.xml.parsers.DocumentBuilderFactory=com.sun.org.apache.xerces.internal.jaxp.documentbuilderfactoryimpl
        // System.setProperty("javax.xml.parsers.DocumentBuilderFactory",
        // "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
        // System.setProperty("javax.xml.parsers.SAXParserFactory",
        // "com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl");
    }

    @Before
    public void setUp() throws InterruptedException, KeeperException, IOException {
        mockHead.setCount(30000);
        mockHead.setEndOffset(30001);
        mockHead.setFileTimestamp(DEFAULT_TIMESTAMP);
        mockHead.setGroupName("FileChannelManagerTest-A");
        mockHead.setHeadTimestamp(DEFAULT_TIMESTAMP);
        mockHead.setStartOffset(1);
        mockHead.setVersion(1);
        // 清理待处理文件
        FileSystem fs = FileSystem.get(URI.create(consumerConf.get(ConsumerConstants.CONFKEY_PRODUCER_FS)),
                                       consumerConf);
        String filePath = consumerConf.get(ConsumerConstants.CONFKEY_TMPFILE_TARGETPATH);
        String oldPath = consumerConf.get(ConsumerConstants.CONFKEY_TMPFILE_OLDPATH);
        String rejectPath = consumerConf.get(ConsumerConstants.CONFKEY_TMPFILE_REJECTPATH);
        fs.delete(new Path(consumerConf.get(ConsumerConstants.CONFKEY_PRODUCER_FS), filePath), true);
        fs.delete(new Path(consumerConf.get(ConsumerConstants.CONFKEY_PRODUCER_FS), oldPath), true);
        fs.delete(new Path(consumerConf.get(ConsumerConstants.CONFKEY_PRODUCER_FS), rejectPath), true);
        fs.mkdirs(new Path(consumerConf.get(ConsumerConstants.CONFKEY_PRODUCER_FS), filePath));
        fs.mkdirs(new Path(consumerConf.get(ConsumerConstants.CONFKEY_PRODUCER_FS), oldPath));
        fs.mkdirs(new Path(consumerConf.get(ConsumerConstants.CONFKEY_PRODUCER_FS), rejectPath));
        fs.create(new Path(new Path(consumerConf.get(ConsumerConstants.CONFKEY_PRODUCER_FS), filePath),
                           DefaultHDFSFileAdapter.head2FileName(mockHead)), true);
        // 清理zk的偏移量
        RecoverableZooKeeper zoo = ZKUtil.connect(consumerConf, new NothingZookeeperWatch());
        Stat statZkRoot = zoo.exists(consumerConf.get(ConsumerConstants.CONFKEY_REP_ZNODE_ROOT), false);
        if (statZkRoot == null) {
            zoo.create(consumerConf.get(ConsumerConstants.CONFKEY_REP_ZNODE_ROOT), null, Ids.OPEN_ACL_UNSAFE,
                       CreateMode.PERSISTENT);
        } else {
            List<String> groupList = zoo.getChildren(consumerConf.get(ConsumerConstants.CONFKEY_REP_ZNODE_ROOT), false);
            for (String group : groupList) {
                String groupRoot = consumerConf.get(ConsumerConstants.CONFKEY_REP_ZNODE_ROOT)
                                   + ConsumerConstants.FILE_SEPERATOR + group;
                String cur = groupRoot + ConsumerConstants.FILE_SEPERATOR + ConsumerConstants.ZK_CURRENT;
                String queue = groupRoot + ConsumerConstants.FILE_SEPERATOR + ConsumerConstants.ZK_QUEUE;
                statZkRoot = zoo.exists(queue, false);
                if (statZkRoot != null) {
                    zoo.delete(queue, ConsumerConstants.ZK_ANY_VERSION);
                }
                statZkRoot = zoo.exists(cur, false);
                if (statZkRoot != null) {
                    zoo.delete(cur, ConsumerConstants.ZK_ANY_VERSION);
                }
                statZkRoot = zoo.exists(groupRoot, false);
                if (statZkRoot != null) {
                    zoo.delete(groupRoot, ConsumerConstants.ZK_ANY_VERSION);
                }
            }
        }
        // 准备相关表
        HBaseAdmin admin = new HBaseAdmin(consumerConf);
        try {
            HTableDescriptor htable = admin.getTableDescriptor(Bytes.toBytes(TABLE));
            if (htable != null) {
                admin.deleteTable(TABLE);
            }
        } catch (TableNotFoundException e) {
            // TODO some log
        }
        HTableDescriptor htable = new HTableDescriptor(TABLE);
        HColumnDescriptor cf1 = new HColumnDescriptor("cf");
        htable.addFamily(cf1);
        admin.createTable(htable);
    }

    @Test
    public void testSinglePut() throws Exception {
        SerBody expectedBody = new SerBody();
        SerBody.Edit e1 = new SerBody.Edit();
        e1.setFamily(Bytes.toBytes("cf"));
        e1.setQualifier(Bytes.toBytes("q"));
        e1.setRowKey(Bytes.toBytes("FCM_SP_1"));
        e1.setTimeStamp(DEFAULT_TIMESTAMP);
        e1.setType(SerBody.Type.Put);
        e1.setValue(Bytes.toBytes("FCM_SP_1_VALUE"));
        expectedBody.addEdit(TABLE, e1);
//        MetaData expMeta = new Version1(mockHead, expectedBody);
//        EasyMock.expect(fileAdapter.read(EasyMock.isA(Head.class), EasyMock.isA(FileSystem.class))).andReturn(expMeta);
        EasyMockUnitils.replay();
        fileChannelManager.start();
        // EasyMock.verify(fileAdapter);
    }
}
