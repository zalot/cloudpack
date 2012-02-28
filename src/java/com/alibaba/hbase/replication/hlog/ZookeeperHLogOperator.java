package com.alibaba.hbase.replication.hlog;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.zookeeper.KeeperException;

import com.alibaba.hbase.replication.domain.HLogInfo;
import com.alibaba.hbase.replication.domain.HLogZNode;
import com.alibaba.hbase.replication.zookeeper.HLogZNodeOperator;

/**
 * 基础的 HLogOperator & Zookeeper 合并操作对象
 * 
 * @author zalot.zhaoh
 */
public class ZookeeperHLogOperator extends AbstractHLogOperator {

    static HLogZNodeOperator znodeOperator;

    public ZookeeperHLogOperator(Configuration conf, FileSystem fs) throws IOException, KeeperException, InterruptedException{
        super(conf,fs);
    }

    public HLogReader getReader(HLogInfo info) throws Exception{
        HLogReader reader = super.getReader(info);
        if (reader != null) {
            HLogZNode znode = znodeOperator.get(info.getPath());
            if (znode != null) {
                if (znode.getPos() > 0) {
                    reader.seek(znode.getPos());
                }
            } else {
                znode = new HLogZNode(reader.getHLogInfo(), 0);
                znodeOperator.create(znode);
            }
            return reader;
        }
        return null;
    }

    public void commit(HLogReader reader) throws Exception{
        HLogZNode znode = znodeOperator.get(reader.getHLogInfo().getPath());
        if(znode != null){
            znode.setPos(reader.getPosition());
            znodeOperator.update(znode);
        }else{
            znode = new HLogZNode(reader.getHLogInfo(), reader.getPosition());
            znodeOperator.create(znode);
        }
    }

}
