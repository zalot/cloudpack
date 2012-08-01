package org.sourceopen.hadoop.hbase.replication.protocol;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;

import org.sourceopen.hadoop.hbase.replication.utility.ProducerConstants;

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

    @SuppressWarnings("unchecked")
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
        head.setVersion(getDefaultProtocolVersion(conf));
        return head;
    }

    public static ProtocolBody getProtocolBody(ProtocolHead head) {
        try {
            ProtocolBody body = getBodyClass(head.getVersion()).newInstance();
            return body;
        } catch (Exception e) {
            return new ProtocolBodyV2();
        }
    }
    
    public static int getDefaultProtocolVersion(Configuration conf){
        return conf.getInt(ProducerConstants.CONFKEY_PROTOCOL_VERSION, 2);
    }
    

    public static MetaData getMetaData(Configuration conf) {
        MetaData minData = new MetaData();
        ProtocolHead head = getProtocolHead(conf);
        minData.setHead(head);
        minData.setBody(getProtocolBody(head));
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
