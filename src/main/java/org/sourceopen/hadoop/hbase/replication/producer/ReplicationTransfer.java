package org.sourceopen.hadoop.hbase.replication.producer;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.regionserver.wal.HLog.Entry;
import org.apache.zookeeper.KeeperException;
import org.sourceopen.hadoop.hbase.replication.core.HBaseService;
import org.sourceopen.hadoop.hbase.replication.core.hlog.domain.HLogEntry;
import org.sourceopen.hadoop.hbase.replication.core.hlog.domain.HLogEntry.Type;
import org.sourceopen.hadoop.hbase.replication.core.hlog.domain.HLogGroup;
import org.sourceopen.hadoop.hbase.replication.core.hlog.domain.HLogPersistence;
import org.sourceopen.hadoop.hbase.replication.core.hlog.reader.HLogReader;
import org.sourceopen.hadoop.hbase.replication.protocol.MetaData;
import org.sourceopen.hadoop.hbase.replication.protocol.ProtocolAdapter;
import org.sourceopen.hadoop.hbase.replication.protocol.ProtocolBody;
import org.sourceopen.hadoop.hbase.replication.protocol.ProtocolHead;
import org.sourceopen.hadoop.hbase.replication.utility.HLogUtil;

/**
 * HReplicationProducer <BR>
 * 1. <BR>
 * 2. 负责将 group 中的数据搬运至 Protocol 中 <BR>
 * 类HBaseReplicationSink.java的实现描述：TODO 类实现描述
 * 
 * @author zalot.zhaoh Feb 29, 2012 2:27:45 PM
 */
public class ReplicationTransfer extends Thread {

    protected static final Log LOG                      = LogFactory.getLog(ReplicationTransfer.class);
    private long               minGroupOperatorInterval = ProducerConstants.HLOG_GROUP_INTERVAL;
    private long               maxReaderBuffer          = ProducerConstants.HLOG_READERBUFFER;
    private long               replicationSleepTime;

    // 外部对象
    protected ProtocolAdapter  adapter;
    protected HLogPersistence  hlogEntryPersistence;
    protected HBaseService     hlogService;

    public ReplicationTransfer(long maxReaderBuffer, long minGroupOperatorInterval, long replicationSleepTime)
                                                                                                              throws IOException,
                                                                                                              KeeperException,
                                                                                                              InterruptedException{
        this.maxReaderBuffer = maxReaderBuffer;
        this.minGroupOperatorInterval = minGroupOperatorInterval;
        this.replicationSleepTime = replicationSleepTime;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(replicationSleepTime);
                doTransfer();
            } catch (Exception e) {
                LOG.error("Producer running error ", e);
            }
        }
    }

    public void doTransfer() throws Exception {
        List<String> groups = hlogEntryPersistence.listGroupName();
        for (String groupName : groups) {
            if (hlogEntryPersistence.lockGroup(groupName)) {
                HLogGroup group = hlogEntryPersistence.getGroupByName(groupName, false);
                if (group != null) {
                    // 每个Group不能连续操作，需要间隔 (优化后)
                    if (group.getLastOperatorTime() + minGroupOperatorInterval < System.currentTimeMillis()) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug(HLogUtil.getBaseInfo(null) + " - doGroup " + group);
                        }
                        doSinkGroup(group);
                        group.setLastOperatorTime(System.currentTimeMillis());
                        hlogEntryPersistence.updateGroup(group, false);
                    }
                }
            }
            // FIXME by dsh 此处存在这线程中断然后无人释放锁的风险
            hlogEntryPersistence.unlockGroup(groupName);
        }
    }

    private void doSinkGroup(HLogGroup group) throws Exception {
        List<HLogEntry> entrys = hlogEntryPersistence.listEntry(group.getGroupName());
        Collections.sort(entrys);
        HLogReader reader = null;
        HLogEntry entry;

        ProtocolHead head = MetaData.getProtocolHead(hlogService.getConf());
        ProtocolBody body = null;

        for (int idx = 0; idx < entrys.size(); idx++) {
            entry = entrys.get(idx);
            if (entry.getType() == Type.END || entry.getType() == Type.UNKNOW) {
                continue;
            }
            reader = hlogService.getReader(entry);
            body = MetaData.getProtocolBody(head);
            int count = 0;
            while (true) {
                Entry ent = null;
                try {
                    ent = reader.next();
                } catch (OutOfMemoryError e) {
                    entry.setPos(entry.getLastVerifiedPos());
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
                // FIXME by dsh 是否完成多记录的去重与合并
                count = count + HLogUtil.put2Body(ent, body, UuidService.getMySelfUUID());
                if (count > maxReaderBuffer) {
                    setHead(head, group.getGroupName(), entry.getTimestamp(), entry.getPos(), reader.getPosition(),
                            count);
                    if (doSinkPart(head, body)) {
                        entry.setPos(reader.getPosition());
                        hlogEntryPersistence.updateEntry(entry);
                        body = MetaData.getProtocolBody(head);
                    } else {
                        reader.seek(entry.getPos());
                    }
                    count = 0;
                }
            }

            if (count > 0) {
                setHead(head, group.getGroupName(), entry.getTimestamp(), entry.getPos(), reader.getPosition(), count);
                if (!doSinkPart(head, body)) {
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
            reader.close();
        }
    }

    private void setHead(ProtocolHead head, String groupName, long timeStamp, long start, long end, long count) {
        head.setCount(count);
        head.setGroupName(groupName);
        head.setFileTimestamp(timeStamp);
        head.setStartOffset(start);
        head.setEndOffset(end);
    }

    private boolean doSinkPart(ProtocolHead head, ProtocolBody body) {
        if (doAdapter(head, body)) {
            return true;
        }
        return false;
    }

    private boolean doAdapter(ProtocolHead head, ProtocolBody body) {
        MetaData data = new MetaData(head, body);
        try {
            adapter.write(data);
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

    public HLogPersistence getHlogEntryPersistence() {
        return hlogEntryPersistence;
    }

    public void setHLogPersistence(HLogPersistence hlogEntryPersistence) {
        this.hlogEntryPersistence = hlogEntryPersistence;
    }

    public HBaseService getHlogService() {
        return hlogService;
    }

    public void setHBaseService(HBaseService hlogService) {
        this.hlogService = hlogService;
    }

    public static ReplicationTransfer newInstance(Configuration conf, ProtocolAdapter adapter, HLogPersistence dao,
                                                  HBaseService service) throws IOException, KeeperException,
                                                                       InterruptedException {
        long maxReaderBuffer = conf.getLong(ProducerConstants.CONFKEY_HLOG_READERBUFFER,
                                            ProducerConstants.HLOG_READERBUFFER);
        long minGroupOperatorInterval = conf.getLong(ProducerConstants.CONFKEY_HLOG_GROUP_INTERVAL,
                                                     ProducerConstants.HLOG_GROUP_INTERVAL);
        long replicationSleepTime = conf.getLong(ProducerConstants.CONFKEY_CROSSIDC_REPLICATION_SLEEPTIME,
                                                 ProducerConstants.CROSSIDC_REPLICATION_SLEEPTIME);
        ReplicationTransfer prod = new ReplicationTransfer(maxReaderBuffer, minGroupOperatorInterval,
                                                           replicationSleepTime);
        prod.setAdapter(adapter);
        prod.setHLogPersistence(dao);
        prod.setHBaseService(service);
        return prod;
    }

}
