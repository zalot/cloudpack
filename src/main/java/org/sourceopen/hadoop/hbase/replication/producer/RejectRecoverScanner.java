package org.sourceopen.hadoop.hbase.replication.producer;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hbase.regionserver.wal.HLog.Entry;
import org.sourceopen.hadoop.hbase.replication.core.HBaseService;
import org.sourceopen.hadoop.hbase.replication.core.hlog.domain.HLogEntry;
import org.sourceopen.hadoop.hbase.replication.core.hlog.reader.HLogReader;
import org.sourceopen.hadoop.hbase.replication.protocol.MetaData;
import org.sourceopen.hadoop.hbase.replication.protocol.ProtocolAdapter;
import org.sourceopen.hadoop.hbase.replication.protocol.ProtocolBody;
import org.sourceopen.hadoop.hbase.replication.protocol.ProtocolHead;
import org.sourceopen.hadoop.hbase.replication.utility.HLogUtil;
import org.sourceopen.hadoop.zookeeper.concurrent.ZDaemonThread;
import org.sourceopen.hadoop.zookeeper.connect.AdvZooKeeper;
import org.sourceopen.hadoop.zookeeper.core.ZNode;

/**
 * Reject<BR>
 * 类HLogGroupZookeeperScanner.java的实现描述：TODO 类实现描述
 * 
 * @author zalot.zhaoh Mar 1, 2012 10:44:45 AM
 */
public class RejectRecoverScanner extends ZDaemonThread {

    protected static final Log    LOG      = LogFactory.getLog(RejectRecoverScanner.class);
    protected final static String LOCKNAME = "rejectrecoverlock";

    public RejectRecoverScanner(AdvZooKeeper zk, ZNode znode, String lockName, long tryLockTime, long onceSleepTime){
        super(zk, znode, LOCKNAME, tryLockTime, onceSleepTime);
    }

    protected HBaseService    hlogService;
    protected ProtocolAdapter adapter;

    public void setAdapter(ProtocolAdapter adapter) {
        this.adapter = adapter;
    }

    public HBaseService getHlogService() {
        return hlogService;
    }

    public void setHBaseService(HBaseService hlogService) {
        this.hlogService = hlogService;
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
                    HLogUtil.put2Body(ent, body, UuidService.getMySelfUUID());
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

    @Override
    public void deamon() throws Exception {
        doRecover();
    }
}
