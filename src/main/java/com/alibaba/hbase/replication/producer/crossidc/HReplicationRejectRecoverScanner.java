package com.alibaba.hbase.replication.producer.crossidc;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.regionserver.wal.HLog.Entry;

import com.alibaba.hbase.replication.hlog.HLogService;
import com.alibaba.hbase.replication.hlog.domain.HLogEntry;
import com.alibaba.hbase.replication.hlog.reader.HLogReader;
import com.alibaba.hbase.replication.protocol.ProtocolBody;
import com.alibaba.hbase.replication.protocol.ProtocolHead;
import com.alibaba.hbase.replication.protocol.MetaData;
import com.alibaba.hbase.replication.protocol.ProtocolAdapter;
import com.alibaba.hbase.replication.utility.HLogUtil;
import com.alibaba.hbase.replication.utility.ProducerConstants;
import com.alibaba.hbase.replication.zookeeper.ZookeeperLock;
import com.alibaba.hbase.replication.zookeeper.ZookeeperSingleLockThread;

/**
 * Reject<BR>
 * 类HLogGroupZookeeperScanner.java的实现描述：TODO 类实现描述
 * 
 * @author zalot.zhaoh Mar 1, 2012 10:44:45 AM
 */
public class HReplicationRejectRecoverScanner extends ZookeeperSingleLockThread {

    protected static final Log LOG  = LogFactory.getLog(HReplicationRejectRecoverScanner.class);

    protected boolean          init = false;
    protected HLogService      hlogService;
    protected ProtocolAdapter  adapter;

    public void setAdapter(ProtocolAdapter adapter) {
        this.adapter = adapter;
    }

    public HLogService getHlogService() {
        return hlogService;
    }

    public void setHlogService(HLogService hlogService) {
        this.hlogService = hlogService;
    }

    public HReplicationRejectRecoverScanner(){
    }

    public HReplicationRejectRecoverScanner(ZookeeperLock lock){
        this.setLock(lock);
    }

    public HReplicationRejectRecoverScanner(Configuration conf){
        if (conf == null) return;
        ZookeeperLock lock = new ZookeeperLock();
        lock.setBasePath(conf.get(ProducerConstants.CONFKEY_ZOO_LOCK_ROOT, ProducerConstants.ZOO_LOCK_ROOT));
        lock.setLockPath(ProducerConstants.ZOO_LOCK_REJECT_SCAN);
        lock.setSleepTime(conf.getLong(ProducerConstants.CONFKEY_ZOO_REJECT_LOCK_FLUSHSLEEPTIME,
                                       ProducerConstants.ZOO_REJECT_LOCK_FLUSHSLEEPTIME));
        lock.setTryLockTime(conf.getLong(ProducerConstants.CONFKEY_ZOO_REJECT_LOCK_RETRYTIME,
                                         ProducerConstants.ZOO_REJECT_LOCK_RETRYTIME));
        this.setLock(lock);
    }

    @Override
    public void doRun() throws Exception {
        doRecover();
    }

    public void doRecover() throws Exception {
        for (ProtocolHead head : adapter.listRejectHead()) {
            doRecover(head);
        }
    }

    public void doRecover(ProtocolHead head) {
        head.setRetry(head.getRetry() + 1);
        HLogEntry entry = HLogUtil.getHLogEntryByHead(head);
        Entry ent = null;
        ProtocolBody body = MetaData.getProtocolBody(head);
        HLogReader reader = null;
        try {
            reader = hlogService.getReader(entry);
            if (reader != null) {
                while ((ent = reader.next()) != null) {
                    HLogUtil.put2Body(ent, body);
                    if (reader.getPosition() == head.getEndOffset()) {
                        break;
                    }
                }
            }
        } catch (Exception e) {
            LOG.error("recover error " + head, e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    LOG.error("reader close error ", e);
                } finally {
                    reader = null;
                }
            }
        }

        if (!doAdapter(head, body)) {
             LOG.warn("recover error " + head);
        }
    }

    private boolean doAdapter(ProtocolHead head, ProtocolBody body) {
        MetaData data = new MetaData(head, body);
        try {
            adapter.recover(data);
            return true;
        } catch (Exception e) {
            LOG.error("doAdapter ", e);
            return false;
        }
    }
}
