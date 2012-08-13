package org.sourceopen.hadoop.hbase.replication.consumer.v2;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.sourceopen.hadoop.hbase.replication.consumer.ConsumerConstants;
import org.sourceopen.hadoop.hbase.replication.protocol.ProtocolAdapter;
import org.sourceopen.hadoop.zookeeper.concurrent.ZDaemonThread;
import org.sourceopen.hadoop.zookeeper.connect.AdvZooKeeper;
import org.sourceopen.hadoop.zookeeper.core.ZNode;
import org.sourceopen.hadoop.zookeeper.core.ZNodeFactory;

/**
 * Protocol 粉碎旧文件 <BR>
 * 类HReplicationCrushScanner的实现描述：TODO 类实现描述
 * 
 * @author zalot.zhaoh Mar 1, 2012 10:44:45 AM
 */
public class CrushScanner extends ZDaemonThread {

    protected static final Log    LOG      = LogFactory.getLog(CrushScanner.class);
    protected static final String LOCKNAME = "crushscanner";
    protected ProtocolAdapter     adapter;
    protected boolean             init     = false;

    public CrushScanner(AdvZooKeeper zk, ZNode znode, long tryLockTime, long onceSleepTime){
        super(zk, znode, LOCKNAME, tryLockTime, onceSleepTime);
    }

    public void setAdapter(ProtocolAdapter adapter) {
        this.adapter = adapter;
    }

    public void doCrush() throws Exception {
        adapter.crush();
    }

    @Override
    public void deamon() throws Exception {
        doCrush();
    }

    public static CrushScanner newInstance(Configuration conf, AdvZooKeeper zk) throws Exception {
        ProtocolAdapter adapter = ProtocolAdapter.getAdapter(conf);
        String name = conf.get(ConsumerConstants.CONFKEY_ROOT_ZOO, ConsumerConstants.ROOT_ZOO);
        ZNode root = ZNodeFactory.createZNode(zk, name, true);
        long tryLockTime = 5000L;
        long onceSleepTime = 10000L;

        CrushScanner crush = new CrushScanner(zk, root, tryLockTime, onceSleepTime);
        crush.setAdapter(adapter);
        return crush;
    }
}
