package com.alibaba.hbase.replication.protocol;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;

import com.alibaba.hbase.replication.utility.ProducerConstants;

/**
 * 协议元数据 类MetaData.java的实现描述：TODO 类实现描述
 * 
 * @author zalot.zhaoh Mar 19, 2012 11:51:21 AM
 */
public class MetaData {

    protected static final Log    LOG                              = LogFactory.getLog(MetaData.class);
    protected static final String DEFAULT_BODYPROTOCOL_CLASS_SUFIX = ProtocolBody.class.getCanonicalName() + "V";
    protected ProtocolHead        _head;
    protected ProtocolBody        _body;

    public ProtocolHead getHead() {
        return _head;
    }

    public ProtocolBody getBody() {
        return _body;
    }

    public void setBody(ProtocolBody body) {
        this._body = body;
    }

    public void setHead(ProtocolHead head) {
        this._head = head;
    }

    public MetaData(ProtocolHead head, ProtocolBody body){
        setHead(head);
        setBody(body);
    }

    public MetaData(){
    }

    protected static Map<String, Class<? extends ProtocolBody>> clazzes = new ConcurrentHashMap<String, Class<? extends ProtocolBody>>();

    public static Class<? extends ProtocolBody> getBodyClass(int version) {
        Class<? extends ProtocolBody> clazz = null;
        try {
            String versionClass = DEFAULT_BODYPROTOCOL_CLASS_SUFIX + version;
            clazz = clazzes.get(versionClass);
            if (clazz == null) {
                clazz = (Class<? extends ProtocolBody>) Class.forName(versionClass);
                clazzes.put(versionClass, clazz);
            }
        } catch (Exception e) {
        }
        return clazz;
    }

    public static ProtocolHead getProtocolHead(Configuration conf) {
        ProtocolHead head = new ProtocolHead();
        head.setVersion(conf.getInt(ProducerConstants.CONFKEY_PROTOCOL_VERSION, 2));
        return head;
    }

    public static ProtocolBody getProtocolBody(Configuration conf) {
        try {
            ProtocolBody body = getBodyClass(conf.getInt(ProducerConstants.CONFKEY_PROTOCOL_VERSION, 2)).newInstance();
            return body;
        } catch (Exception e) {
            return new ProtocolBodyV2();
        }
    }
    
    public static ProtocolBody getProtocolBody(Configuration conf, int version) {
        try {
            ProtocolBody body = getBodyClass(version).newInstance();
            return body;
        } catch (Exception e) {
            return getProtocolBody(conf);
        }
    }

    public static MetaData getMetaData(Configuration conf) {
        MetaData minData = new MetaData();
        minData.setHead(getProtocolHead(conf));
        minData.setBody(getProtocolBody(conf));
        return minData;
    }

    public static MetaData getMetaData(ProtocolHead head, byte[] bodyData) {
        ProtocolBody body;
        MetaData minData = new MetaData();
        try {
            body = getBodyClass(head.getVersion()).newInstance();
            body.setBodyData(bodyData);
            minData.setHead(head);
            minData.setBody(body);
            return minData;
        } catch (Exception e) {
            LOG.error("meta", e);
        }
        return null;
    }
}
