package com.alibaba.hbase.replication.producer;

import java.io.IOException;

import org.apache.hadoop.hbase.regionserver.wal.HLog.Entry;

import com.alibaba.hbase.replication.hlog.HLogOperator;
import com.alibaba.hbase.replication.hlog.HLogReader;
import com.alibaba.hbase.replication.hlog.domain.HLogGroup;
import com.alibaba.hbase.replication.hlog.domain.HLogInfo;
import com.alibaba.hbase.replication.protocol.Body;
import com.alibaba.hbase.replication.protocol.Head;
import com.alibaba.hbase.replication.protocol.ProtocolAdapter;
import com.alibaba.hbase.replication.protocol.Version1;
import com.alibaba.hbase.replication.utility.HLogUtil;

public class HBaseReplicationSink implements Runnable {

    public static final int   DEFAULT_MAX_SIZE = 10000;

    protected HLogGroup       group            = null;
    protected HLogOperator    operator         = null;
    protected ProtocolAdapter protocol;
    protected int             maxSize          = DEFAULT_MAX_SIZE;

    public HBaseReplicationSink(HLogGroup group, HLogOperator operator, ProtocolAdapter adapter){
        this(group, operator, adapter, DEFAULT_MAX_SIZE);
    }

    public HBaseReplicationSink(HLogGroup group, HLogOperator operator, ProtocolAdapter adapter, int size){
        this.group = group;
        group.sort();
        this.operator = operator;
        this.maxSize = size;
    }

    @Override
    public void run() {
        HLogReader reader;
        Body body = new Body(null);
        for (HLogInfo info : group.getHlogInfos()) {
            try {
                reader = operator.getReader(info);
                doReader(reader, body);
                Head head = new Head();
                head.setGroupName(group.getGroupName());
                head.setFileTimestamp(reader.getHLogInfo().getTimestamp());
                if (sink(head, body)) {
                    operator.commit(reader);
                }
            } catch (Exception e) {
                return;
            }
        }
    }

    private void doReader(HLogReader reader, Body body) throws IOException {
        try {
            Entry entry = null;
            while ((entry = reader.next()) != null) {
                HLogUtil.put2Body(entry, body);
                if(body.getEditMap().size() > maxSize){
                    return;
                }
            }
        } finally {
            reader.close();
        }
    }

    protected boolean sink(Head head, Body body) {
        Version1 metadata = new Version1(head, body);
        try {
            protocol.write(metadata);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            // TODO
        }
        return false;
    }
}
