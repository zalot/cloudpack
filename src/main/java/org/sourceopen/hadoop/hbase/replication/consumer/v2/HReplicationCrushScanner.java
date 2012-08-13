package org.sourceopen.hadoop.hbase.replication.consumer.v2;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.sourceopen.hadoop.hbase.replication.protocol.ProtocolAdapter;
import org.sourceopen.hadoop.hbase.replication.utility.ProducerConstants;
import org.sourceopen.hadoop.zookeeper.concurrent.ZThread;

/**
 * Protocol 粉碎旧文件 <BR>
 * 类HReplicationCrushScanner的实现描述：TODO 类实现描述
 * 
 * @author zalot.zhaoh Mar 1, 2012 10:44:45 AM
 */
public class HReplicationCrushScanner extends ZThread {

    protected static final Log LOG  = LogFactory.getLog(HReplicationCrushScanner.class);

    protected boolean          init = false;
    protected ProtocolAdapter  adapter;

    public void setAdapter(ProtocolAdapter adapter) {
        this.adapter = adapter;
    }

    public HReplicationCrushScanner(){
    }

    public HReplicationCrushScanner(ZNodeLock lock){
        this.setLock(lock);
    }

    public HReplicationCrushScanner(Configuration conf){
        if (conf == null) return;
        ZNodeLock lock = new ZNodeLock();
        lock.setBasePath(conf.get(ProducerConstants.CONFKEY_ZOO_LOCK_ROOT, ProducerConstants.ZOO_LOCK_ROOT));
        lock.setLockPath(ProducerConstants.ZOO_LOCK_CRUSH_SCAN);
        lock.setSleepTime(conf.getLong(ProducerConstants.CONFKEY_ZOO_REJECT_LOCK_FLUSHSLEEPTIME,
                                       ProducerConstants.ZOO_REJECT_LOCK_FLUSHSLEEPTIME) * 2);
        lock.setTryLockTime(conf.getLong(ProducerConstants.CONFKEY_ZOO_REJECT_LOCK_RETRYTIME,
                                         ProducerConstants.ZOO_REJECT_LOCK_RETRYTIME) * 2);
        this.setLock(lock);
    }

    @Override
    public void doRun() throws Exception {
        doCrush();
    }

    public void doCrush() throws Exception {
        adapter.crush();
    }
}
