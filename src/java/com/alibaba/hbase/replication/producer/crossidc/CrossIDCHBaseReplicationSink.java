package com.alibaba.hbase.replication.producer.crossidc;

import java.util.Collections;
import java.util.List;

import org.apache.hadoop.hbase.regionserver.wal.HLog.Entry;

import com.alibaba.hbase.replication.domain.HLogEntry;
import com.alibaba.hbase.replication.hlog.HLogOperator;
import com.alibaba.hbase.replication.hlog.HLogReader;
import com.alibaba.hbase.replication.persistence.HLogPersistence;
import com.alibaba.hbase.replication.protocol.Body;
import com.alibaba.hbase.replication.protocol.Head;
import com.alibaba.hbase.replication.protocol.ProtocolAdapter;
import com.alibaba.hbase.replication.protocol.Version1;
import com.alibaba.hbase.replication.utility.HLogUtil;

/**
 * HBaseReplication 搬运工作对象 1. 能操作 Operator 来取得 数据 2. 负责将 group 中的数据搬运至 Protocol 中 类HBaseReplicationSink.java的实现描述：TODO
 * 类实现描述
 * 
 * @author zalot.zhaoh Feb 29, 2012 2:27:45 PM
 */
public class CrossIDCHBaseReplicationSink implements Runnable {
    protected ProtocolAdapter adapter;
    protected HLogPersistence hlogDAO;
    protected HLogOperator    hlogOperator;

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
        while(true){
            List<String> groups;
            try {
                Thread.sleep(500);
                groups = hlogDAO.listGroupName();
            } catch (Exception e1) {
                continue;
            }
            
            try {
                for(String groupName : groups){
                    if(hlogDAO.lockGroup(groupName)){
                        doSinkGroup(groupName);
                    }
                }
            } catch (Exception e) {
            }
        }
    }

    private void doSinkGroup(String groupName) throws Exception {
        List<HLogEntry> entrys = hlogDAO.listEntry(groupName);
        
        Collections.sort(entrys);
        HLogReader reader = null;
        Entry ent = null;
        Body body = new Body();
        for(HLogEntry entry : entrys){
            reader = hlogOperator.getReader(entry);
            while((ent = reader.next()) != null){
                HLogUtil.put2Body(ent, body);
                if(body.getEditMap().size() > 50000){
                    if(doSinkPart(groupName , entry.getTimestamp(), entry.getPos() , reader.getPosition(), body)){
                        entry.setPos(reader.getPosition());
                        hlogDAO.updateEntry(entry);
                    }
                }
            }
        }
//        if(doSinkPart(groupName , entry.getTimestamp(), entry.getPos() , reader.getPosition(), body)){
//            entry.setPos(reader.getPosition());
//            hlogDAO.updateEntry(entry);
//        }
    }
    
    private boolean doSinkPart(String groupName , long timeStamp , long start , long end , Body body){
        Head head = new Head();
        head.setGroupName(groupName);
        head.setFileTimestamp(timeStamp);
        head.setStartOffset(start);
        head.setEndOffset(end);
        if(doAdapter(head, body)){
            body = null;
            body = new Body();
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
            return false;
        }
    }
}
