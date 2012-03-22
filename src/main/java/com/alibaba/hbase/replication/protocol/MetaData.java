package com.alibaba.hbase.replication.protocol;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 协议元数据 类MetaData.java的实现描述：TODO 类实现描述
 * 
 * @author zalot.zhaoh Mar 19, 2012 11:51:21 AM
 */
public class MetaData {

    protected static final Log                   LOG                              = LogFactory.getLog(MetaData.class);
    protected static final String                DEFAULT_BODYPROTOCOL_CLASS_SUFIX = "com.alibaba.hbase.replication.protocol.Body";
    protected static final Class<? extends ProtocolBody> DEFAULT_BODYPROTOCOL_CLASS       = Body1.class;
    protected ProtocolHead                               _head;
    protected ProtocolBody                               _body;

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

    protected static Map<String, Class<? extends ProtocolBody>> clazzes = new ConcurrentHashMap<String, Class<? extends ProtocolBody>>();

    public static Class<? extends ProtocolBody> getBodyClass(ProtocolHead head) {
        Class<? extends ProtocolBody> clazz = null;
        try {
            String versionClass = DEFAULT_BODYPROTOCOL_CLASS_SUFIX + head.getVersion();
            clazz = clazzes.get(versionClass);
            if (clazz == null) {
                clazz = (Class<? extends ProtocolBody>) Class.forName(versionClass);
                clazzes.put(versionClass, clazz);
            }
        } catch (Exception e) {
            clazz = DEFAULT_BODYPROTOCOL_CLASS;
        }
        return clazz;
    }

    public static ProtocolHead getDefaultHead() {
        ProtocolHead head = new ProtocolHead();
        head.setVersion(1); // 可以将版本号设在外面
        return head;
    }
    
    public static ProtocolBody getDefaultBody() {
        return new Body1();
    }

    public static MetaData getMetaData(ProtocolHead head, ProtocolBody body) {
        MetaData minData = new MetaData();
        minData.setHead(head);
        minData.setBody(body);
        return minData;
    }

    public static MetaData getMetaData(ProtocolHead head, byte[] bodyData) {
        MetaData minData = new MetaData();
        ProtocolBody body;
        try {
            body = getBodyClass(head).newInstance();
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
