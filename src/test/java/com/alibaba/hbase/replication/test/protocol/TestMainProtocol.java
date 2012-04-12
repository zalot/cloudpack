package com.alibaba.hbase.replication.test.protocol;

import java.util.UUID;

import org.apache.hadoop.hbase.KeyValue;
import org.junit.BeforeClass;
import org.junit.Test;

import com.alibaba.hbase.replication.protocol.HDFSFileAdapter;
import com.alibaba.hbase.replication.protocol.MetaData;
import com.alibaba.hbase.replication.protocol.ProtocolBody;
import com.alibaba.hbase.replication.protocol.ProtocolBodyV2;
import com.alibaba.hbase.replication.protocol.ProtocolHead;
import com.alibaba.hbase.replication.server.ReplicationConf;
import com.alibaba.hbase.replication.test.TestBase;
import com.alibaba.hbase.replication.test.util.TestConfigurationUtil;

public class TestMainProtocol extends TestBase {

    protected static ReplicationConf confProducer = new ReplicationConf();

    @BeforeClass
    public static void init() throws Exception {
        init1();
        TestConfigurationUtil.setProducer(util1.getConfiguration(), util2.getConfiguration(), confProducer);
    }

    @Test
    public void testDFSAdapterforBodyVersion(ProtocolBody body, int size) {
        HDFSFileAdapter adapter = new HDFSFileAdapter();
        adapter.init(confProducer);

        ProtocolHead head = MetaData.getProtocolHead(confProducer);
        body = new ProtocolBodyV2();
        byte[] family = "test".getBytes();
        byte[] qualifier = "info".getBytes();
        for (int x = 0; x < size; x++) {
            byte[] row = UUID.randomUUID().toString().getBytes();
            byte[] value = UUID.randomUUID().toString().getBytes();
            body.putKeyValue("test", new KeyValue(row, family, qualifier, value));
        }
        // metadata.setBody(body);
        // adapter.write(data)
    }
}
