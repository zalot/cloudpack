package com.alibaba.hbase.replication.test.protocol;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.Random;
import java.util.UUID;

import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Test;

import com.alibaba.hbase.replication.protocol.ProtocolBodyV1;
import com.alibaba.hbase.replication.protocol.ProtocolBodyV2;

public class TestProtocolBody {

    public static Random rnd = new Random();

    @Test
    public void testProtocolBodySize() throws Exception {
        ProtocolBodyV1 bodyv1 = new ProtocolBodyV1();
        ProtocolBodyV2 bodyv2 = new ProtocolBodyV2();
        String tableName = "null";
        int sizeCount = 100000;
        KeyValue[] kvs = new KeyValue[sizeCount + 1];
        byte[] family = "test".getBytes();
        byte[] qualifier = "test".getBytes();

        byte[] rowkey = null;
        byte[] value = null;
        int type = 6;

        for (int x = 0; x <= sizeCount; x++) {
            if (type == 1) {
                rowkey = UUID.randomUUID().toString().getBytes();
                value = UUID.randomUUID().toString().getBytes();
            }
            if (type == 2) {
                rowkey = Bytes.toBytes(UUID.randomUUID().toString());
                value = Bytes.toBytes(UUID.randomUUID().toString());
            } else if (type == 3) {
                rowkey = ("test-" + UUID.randomUUID().toString().substring(1, 10)).getBytes();
                value = UUID.randomUUID().toString().substring(1, 10).getBytes();
            } else if (type == 4) {
                rowkey = ("test-" + rnd.nextLong()).getBytes();
                value = Bytes.toBytes(rnd.nextLong());
            } else if (type == 5) {
                rowkey = "test2".getBytes();
                value = "test3".getBytes();
            } else if (type == 6) {
                TmpObject to = new TmpObject();
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                ObjectOutputStream outO = new ObjectOutputStream(bytes);
                outO.writeObject(to);
                value = bytes.toByteArray();
                bytes.close();
                outO.close();

                rowkey = ("test-" + UUID.randomUUID().toString().substring(1, 10)).getBytes();
            }
            kvs[x] = new KeyValue(rowkey, family, qualifier, value);
            bodyv1.putKeyValue(tableName, kvs[x]);
            bodyv2.putKeyValue(tableName, kvs[x]);
            if (x % 20000 == 0 && x > 0) {
                long start1 = System.currentTimeMillis();
                byte[] datav1 = bodyv1.getBodyData();
                long end1 = System.currentTimeMillis();

                long start2 = System.currentTimeMillis();
                byte[] datav2 = bodyv2.getBodyData();
                long end2 = System.currentTimeMillis();

                System.out.println("type=" + type + " kvsize = " + x + " v1 time " + (end1 - start1) + " len "
                                   + datav1.length);
                System.out.println("type=" + type + " kvsize = " + x + " v2 time " + (end2 - start2) + " len "
                                   + datav2.length);
            }
        }

    }
}
