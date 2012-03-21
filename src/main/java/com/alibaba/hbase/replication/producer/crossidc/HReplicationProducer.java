package com.alibaba.hbase.replication.producer.crossidc;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.regionserver.wal.HLog.Entry;
import org.apache.zookeeper.KeeperException;

import com.alibaba.hbase.replication.hlog.HLogEntryPersistence;
import com.alibaba.hbase.replication.hlog.HLogService;
import com.alibaba.hbase.replication.hlog.domain.HLogEntry;
import com.alibaba.hbase.replication.hlog.domain.HLogEntry.Type;
import com.alibaba.hbase.replication.hlog.domain.HLogEntryGroup;
import com.alibaba.hbase.replication.hlog.reader.HLogReader;
import com.alibaba.hbase.replication.protocol.Body;
import com.alibaba.hbase.replication.protocol.Head;
import com.alibaba.hbase.replication.protocol.MetaData;
import com.alibaba.hbase.replication.protocol.ProtocolAdapter;
import com.alibaba.hbase.replication.utility.HLogUtil;
import com.alibaba.hbase.replication.utility.ProducerConstants;

/**
 * HReplicationProducer <BR>
 * 1. <BR>
 * 2. 负责将 group 中的数据搬运至 Protocol 中 <BR>
 * 类HBaseReplicationSink.java的实现描述：TODO 类实现描述
 * 
 * @author zalot.zhaoh Feb 29, 2012 2:27:45 PM
 */
public class HReplicationProducer implements Runnable {

    protected static final Log     LOG                      = LogFactory.getLog(HReplicationProducer.class);
    private long                   minGroupOperatorInterval = ProducerConstants.HLOG_GROUP_INTERVAL;
    private long                   maxReaderBuffer          = ProducerConstants.HLOG_READERBUFFER;
    private long                   replicationSleepTime;

    // 外部对象
    protected ProtocolAdapter      adapter;
    protected HLogEntryPersistence hlogEntryPersistence;
    protected HLogService          hlogService;

    public HReplicationProducer(Configuration conf) throws IOException, KeeperException, InterruptedException{
        maxReaderBuffer = conf.getLong(ProducerConstants.CONFKEY_HLOG_READERBUFFER, ProducerConstants.HLOG_READERBUFFER);
        minGroupOperatorInterval = conf.getLong(ProducerConstants.CONFKEY_HLOG_GROUP_INTERVAL,
                                                ProducerConstants.HLOG_GROUP_INTERVAL);
        replicationSleepTime = conf.getLong(ProducerConstants.CONFKEY_CROSSIDC_REPLICATION_SLEEPTIME,
                                            ProducerConstants.CROSSIDC_REPLICATION_SLEEPTIME);
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(replicationSleepTime);
                doProducer();
            } catch (Exception e) {
                LOG.error("Producer running error ", e);
            }
        }
    }

    public void doProducer() throws Exception {
        List<String> groups = hlogEntryPersistence.listGroupName();
        for (String groupName : groups) {
            if (hlogEntryPersistence.lockGroup(groupName)) {
                HLogEntryGroup group = hlogEntryPersistence.getGroupByName(groupName, false);
                if (group != null) {
                    // 每个Group不能连续操作，需要间隔 (优化后)
                    if (group.getLastOperatorTime() + minGroupOperatorInterval < System.currentTimeMillis()) {
                        doSinkGroup(group);
                        group.setLastOperatorTime(System.currentTimeMillis());
                        hlogEntryPersistence.updateGroup(group, false);
                    }
                }
            }
            if (hlogEntryPersistence.isMeLockGroup(groupName)) {
                hlogEntryPersistence.unlockGroup(groupName);
            }
        }
    }

    private void doSinkGroup(HLogEntryGroup group) throws Exception {
        List<HLogEntry> entrys = hlogEntryPersistence.listEntry(group.getGroupName());
        Collections.sort(entrys);
        HLogReader reader = null;
        Body body = new Body();
        HLogEntry entry;
        for (int idx = 0; idx < entrys.size(); idx++) {
            entry = entrys.get(idx);
            if (entry.getType() == Type.END || entry.getType() == Type.UNKNOW) {
                continue;
            }
            reader = hlogService.getReader(entry);
            int count = 0;
            while (true) {
                Entry ent = null;
                try {
                    ent = reader.next();
                } catch (OutOfMemoryError e) {
                    entry.setPos(reader.getPosition());
                    hlogEntryPersistence.updateEntry(entry);
                    return;
                } catch (Exception e) {
                    entry.setPos(entry.getLastVerifiedPos());
                    hlogEntryPersistence.updateEntry(entry);
                    return;
                }
                if (ent == null) {
                    break;
                } else {
                    entry.setLastVerifiedPos(reader.getPosition());
                }
                count = count + HLogUtil.put2Body(ent, body);
                if (count > maxReaderBuffer) {
                    if (doSinkPart(group.getGroupName(), entry.getTimestamp(), entry.getPos(), reader.getPosition(),
                                   body, count)) {
                        entry.setPos(reader.getPosition());
                        hlogEntryPersistence.updateEntry(entry);
                        body = new Body();
                    } else {
                        reader.seek(entry.getPos());
                    }
                    count = 0;
                }
            }

            if (count > 0) {
                if (!doSinkPart(group.getGroupName(), entry.getTimestamp(), entry.getPos(), reader.getPosition(), body,
                                count)) {
                    // 如果失败则返回,不再继续更新
                    return;
                }
            }

            // 如果指针移动了则更新
            if (entry.getPos() < reader.getPosition()) {
                entry.setPos(reader.getPosition());
            }

            // 如果后面还有 HLogEntry 则说明这个 Reader 的数据都以经读完 (优化后)
            if (reader.getPosition() > 0 && idx + 1 < entrys.size()) {
                entry.setType(Type.END);
            }

            hlogEntryPersistence.updateEntry(entry);
            body = new Body();
            reader.close();
        }
    }

    private boolean doSinkPart(String groupName, long timeStamp, long start, long end, Body body, long count) {
        Head head = new Head();
        head.setCount(count);
        head.setGroupName(groupName);
        head.setFileTimestamp(timeStamp);
        head.setStartOffset(start);
        head.setEndOffset(end);
        head.setVersion(1);
        if (doAdapter(head, body)) {
            return true;
        }
        return false;
    }

    private boolean doAdapter(Head head, Body body) {
        MetaData data = MetaData.getMetaData(head, body);
        try {
            adapter.write(data);
            if (LOG.isInfoEnabled()) LOG.info("doAdapter - > " + head);
            return true;
        } catch (Exception e) {
            LOG.error("doAdapter error " + head, e);
            return false;
        }
    }

    public ProtocolAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(ProtocolAdapter adapter) {
        this.adapter = adapter;
    }

    public HLogEntryPersistence getHlogEntryPersistence() {
        return hlogEntryPersistence;
    }

    public void setHlogEntryPersistence(HLogEntryPersistence hlogEntryPersistence) {
        this.hlogEntryPersistence = hlogEntryPersistence;
    }

    public HLogService getHlogService() {
        return hlogService;
    }

    public void setHlogService(HLogService hlogService) {
        this.hlogService = hlogService;
    }

    public static HReplicationProducer newInstance(Configuration conf, ProtocolAdapter adapter,
                                                   HLogEntryPersistence dao, HLogService service) throws IOException,
                                                                                                 KeeperException,
                                                                                                 InterruptedException {
        HReplicationProducer prod = new HReplicationProducer(conf);
        prod.setAdapter(adapter);
        prod.setHlogEntryPersistence(dao);
        prod.setHlogService(service);
        return prod;
    }
}
