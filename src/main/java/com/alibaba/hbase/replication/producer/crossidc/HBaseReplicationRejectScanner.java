package com.alibaba.hbase.replication.producer.crossidc;

import java.io.IOException;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.regionserver.wal.HLog.Entry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.NodeExistsException;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;

import com.alibaba.hbase.replication.hlog.HLogService;
import com.alibaba.hbase.replication.hlog.domain.HLogEntry;
import com.alibaba.hbase.replication.hlog.reader.HLogReader;
import com.alibaba.hbase.replication.protocol.Body;
import com.alibaba.hbase.replication.protocol.FileAdapter;
import com.alibaba.hbase.replication.protocol.Head;
import com.alibaba.hbase.replication.protocol.ProtocolAdapter;
import com.alibaba.hbase.replication.protocol.Version1;
import com.alibaba.hbase.replication.utility.HLogUtil;
import com.alibaba.hbase.replication.utility.ProducerConstants;
import com.alibaba.hbase.replication.utility.ZKUtil;
import com.alibaba.hbase.replication.zookeeper.NothingZookeeperWatch;
import com.alibaba.hbase.replication.zookeeper.RecoverableZooKeeper;
import com.alibaba.hbase.replication.zookeeper.ZookeeperLock;
import com.alibaba.hbase.replication.zookeeper.ZookeeperLockThread;

/**
 * HLogGroup 扫描器<BR>
 * 类HLogGroupZookeeperScanner.java的实现描述：TODO 类实现描述
 * 
 * @author zalot.zhaoh Mar 1, 2012 10:44:45 AM
 */
public class HBaseReplicationRejectScanner extends ZookeeperLockThread {

    protected static final Log     LOG = LogFactory.getLog(HBaseReplicationRejectScanner.class);

    protected Path                 dfsRejectHLogPath;
    protected HLogService          hlogService;
    protected RecoverableZooKeeper zooKeeper;
    protected ProtocolAdapter      adapter;

    public HLogService getHlogService() {
        return hlogService;
    }

    public void setHlogService(HLogService hlogService) {
        this.hlogService = hlogService;
    }

    public RecoverableZooKeeper getZooKeeper() {
        return zooKeeper;
    }

    public void setZooKeeper(RecoverableZooKeeper zooKeeper) {
        this.zooKeeper = zooKeeper;
    }

    public HBaseReplicationRejectScanner(Configuration conf) throws KeeperException, InterruptedException, IOException{
        this(UUID.randomUUID().toString(), conf, null);
    }

    public HBaseReplicationRejectScanner(Configuration conf, RecoverableZooKeeper zoo) throws KeeperException,
                                                                                      InterruptedException, IOException{
        this(UUID.randomUUID().toString(), conf, zoo);
    }

    public HBaseReplicationRejectScanner(String name, Configuration conf, RecoverableZooKeeper zoo){
        // if (zoo == null) zoo = ZKUtil.connect(conf, new NothingZookeeperWatch());
        // this.zooKeeper = zoo;
        // this.name = name;
        // ZookeeperLock lock = new ZookeeperLock();
        // lock.setBasePath(hlogService.getConf().get(ProducerConstants.CONFKEY_ZOO_LOCK_ROOT,
        // ProducerConstants.ZOO_LOCK_ROOT));
        // lock.setLockPath(ProducerConstants.ZOO_LOCK_REJECT_SCAN);
        // lock.setSleepTime(hlogService.getConf().getLong(ProducerConstants.CONFKEY_ZOO_REJECT_LOCK_FLUSHSLEEPTIME,
        // ProducerConstants.ZOO_REJECT_LOCK_FLUSHSLEEPTIME));
        // lock.setTryLockTime(hlogService.getConf().getLong(ProducerConstants.CONFKEY_ZOO_REJECT_LOCK_RETRYTIME,
        // ProducerConstants.ZOO_REJECT_LOCK_RETRYTIME));
        this(new ZookeeperLock());
    }

    public HBaseReplicationRejectScanner(ZookeeperLock lock){
        super(lock);
    }

    @Override
    public void doRun() throws Exception {
        for (FileStatus fstat : hlogService.getFileSystem().listStatus(dfsRejectHLogPath)) {
            doPart(fstat);
        }
    }

    private void doPart(FileStatus fstat) {
        String fileName = fstat.getPath().getName();
        Head head = FileAdapter.validataFileName(fileName);

        HLogEntry entry = HLogUtil.getHLogEntryByHead(head);
        Entry ent = null;
        Body body = new Body();
        try {
            HLogReader reader = hlogService.getReader(entry);
            if (reader != null) {
                while ((ent = reader.next()) != null) {
                    HLogUtil.put2Body(ent, body);
                    if (reader.getPosition() == head.getEndOffset()) {
                        break;
                    }
                }
                if (doAdapter(head, body)) {

                }
                reader.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
