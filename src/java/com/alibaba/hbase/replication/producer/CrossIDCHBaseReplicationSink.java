package com.alibaba.hbase.replication.producer;

import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hbase.regionserver.wal.HLog.Entry;

import com.alibaba.hbase.replication.hlog.HLogOperator;
import com.alibaba.hbase.replication.hlog.HLogReader;
import com.alibaba.hbase.replication.hlog.domain.HLogEntry;
import com.alibaba.hbase.replication.hlog.domain.HLogEntry.Type;
import com.alibaba.hbase.replication.hlog.domain.HLogEntryGroup;
import com.alibaba.hbase.replication.persistence.HLogPersistence;
import com.alibaba.hbase.replication.protocol.Body;
import com.alibaba.hbase.replication.protocol.Body.Edit;
import com.alibaba.hbase.replication.protocol.Head;
import com.alibaba.hbase.replication.protocol.ProtocolAdapter;
import com.alibaba.hbase.replication.protocol.Version1;
import com.alibaba.hbase.replication.utility.AliHBaseConstants;
import com.alibaba.hbase.replication.utility.HLogUtil;

/**
 * HBaseReplication 搬运工作对象 <BR>
 * 1. 能操作 Operator 来取得 数据 <BR>
 * 2. 负责将 group 中的数据搬运至 Protocol 中 <BR>
 * 类HBaseReplicationSink.java的实现描述：TODO 类实现描述
 * 
 * @author zalot.zhaoh Feb 29, 2012 2:27:45 PM
 */
public class CrossIDCHBaseReplicationSink extends Thread {

    protected static final Log LOG             = LogFactory.getLog(HLogGroupZookeeperScanner.class);
    protected ProtocolAdapter  adapter;
    protected HLogPersistence  hlogDAO;
    protected HLogOperator     hlogOperator;
    private long               minGroupInterval = AliHBaseConstants.HLOG_GROUP_INTERVAL;
    private long               maxReaderBuffer = AliHBaseConstants.HLOG_READERBUFFER;

    public CrossIDCHBaseReplicationSink(HLogPersistence dao, HLogOperator operator, ProtocolAdapter ad){
        this.hlogDAO = dao;
        this.hlogOperator = operator;
        this.adapter = ad;
    }

    public ProtocolAdapter getAdapter() {
        return adapter;
    }

    public HLogPersistence getHlogDAO() {
        return hlogDAO;
    }

    public HLogOperator getHlogOperator() {
        return hlogOperator;
    }

    public void setAdapter(ProtocolAdapter adapter) {
        this.adapter = adapter;
    }

    public void setHlogDAO(HLogPersistence hlogDAO) {
        this.hlogDAO = hlogDAO;
    }

    public void setHlogOperator(HLogOperator hlogOperator) {
        this.hlogOperator = hlogOperator;
    }

    @Override
    public void run() {
        while (true) {
            List<String> groups;
            try {
                Thread.sleep(5000);
                groups = hlogDAO.listGroupName();
                for (String groupName : groups) {
                    if (hlogDAO.lockGroup(groupName)) {
                        HLogEntryGroup group = hlogDAO.getGroupByName(groupName, false);
                        if (group != null) {
                            // 每个Group不能连续操作，需要间隔 (优化后)
                            if (group.getLastOperatorTime() + minGroupInterval < System.currentTimeMillis()) {
                                doSinkGroup(group);
                                group.setLastOperatorTime(System.currentTimeMillis());
                                hlogDAO.updateGroup(group, false);
                            }
                        }
                    }
                    hlogDAO.unlockGroup(groupName);
                }
            } catch (Exception e) {
            }
        }
    }

    private void doSinkGroup(HLogEntryGroup group) throws Exception {
        List<HLogEntry> entrys = hlogDAO.listEntry(group.getGroupName());
        Collections.sort(entrys);
        HLogReader reader = null;
        Entry ent = null;
        Body body = new Body();
        HLogEntry entry;
        for (int idx = 0; idx < entrys.size(); idx++) {
            entry = entrys.get(idx);
            if (entry.getType() == Type.END || entry.getType() == Type.UNKNOW) {
                continue;
            }
            reader = hlogOperator.getReader(entry);
            while ((ent = reader.next()) != null) {
                HLogUtil.put2Body(ent, body);
                if (body.getEditMap().size() > maxReaderBuffer) {
                    if (doSinkPart(group.getGroupName(), entry.getTimestamp(), entry.getPos(), reader.getPosition(),
                                   body)) {
                        entry.setPos(reader.getPosition());
                        hlogDAO.updateEntry(entry);
                        body = new Body();
                    }
                }
            }

            // 如果指针移动了则更新
            if (entry.getPos() < reader.getPosition()) {
                entry.setPos(reader.getPosition());
                // 如果后面还有 HLogEntry 则说明这个 Reader 的数据都以经读完 (优化后)
                if (idx + 1 < entrys.size()) {
                    entry.setType(Type.END);
                }

                if (body.getEditMap().size() > 0) {
                    if (!doSinkPart(group.getGroupName(), entry.getTimestamp(), entry.getPos(), reader.getPosition(),
                                    body)) {
                        // 如果失败则返回,不再继续更新
                        return;
                    }
                }

                hlogDAO.updateEntry(entry);
            }
            body = new Body();
            reader.close();
        }
    }

    private boolean doSinkPart(String groupName, long timeStamp, long start, long end, Body body) {
        Head head = new Head();
        int count = 0;
        for (List<Edit> edits : body.getEditMap().values()) {
            count += edits.size();
        }
        head.setCount(count);
        head.setGroupName(groupName);
        head.setFileTimestamp(timeStamp);
        head.setStartOffset(start);
        head.setEndOffset(end);
        System.out.println(head);
        if (doAdapter(head, body)) {
            return true;
        }
        return false;
    }

    private boolean doAdapter(Head head, Body body) {
        Version1 version1 = new Version1(head, body);
        try {
            adapter.write(version1);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
