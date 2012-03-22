package com.alibaba.hbase.replication.protocol;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;

import com.alibaba.hbase.replication.utility.ProducerConstants;

/**
 * 协议适配器 类ProtocolAdapter.java的实现描述：TODO 类实现描述
 * 
 * @author zalot.zhaoh Feb 28, 2012 2:26:38 PM
 */
public abstract class ProtocolAdapter {

    protected static final Log LOG = LogFactory.getLog(ProtocolAdapter.class);

    public ProtocolAdapter(){
    };

    public abstract void write(MetaData data) throws Exception;

    public abstract MetaData read(ProtocolHead head) throws Exception;

    public abstract void clean(ProtocolHead head) throws Exception;

    public abstract void crush() throws Exception;

    public abstract List<ProtocolHead> listHead() throws Exception;

    public abstract void reject(ProtocolHead head) throws Exception ;

    public abstract void recover(MetaData data) throws Exception;

    public abstract void init(Configuration conf);

    public abstract List<ProtocolHead> listRejectHead() throws Exception ;

    protected static ProtocolAdapter _adapter;

    @SuppressWarnings("unchecked")
    public static ProtocolAdapter getAdapter(Configuration conf) {
        if (_adapter != null) return _adapter;
        String clazzStr = conf.get(ProducerConstants.CONFKEY_PROTOCOL_CLASS);
        try {
            Class<ProtocolAdapter> clazz = (Class<ProtocolAdapter>) Class.forName(clazzStr);
            ProtocolAdapter adapter = clazz.newInstance();
            adapter.init(conf);
            _adapter = adapter;
        } catch (Exception e) {
            LOG.error("getAdapter error", e);
        }
        return _adapter;
    }
}
