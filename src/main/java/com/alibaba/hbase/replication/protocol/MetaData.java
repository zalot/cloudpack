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
    protected static final Class<? extends Body> DEFAULT_BODYPROTOCOL_CLASS       = Body1.class;
    protected Head                               _head;
    protected Body                               _body;

    public Head getHead() {
        return _head;
    }

    public Body getBody() {
        return _body;
    }

    public void setBody(Body body) {
        this._body = body;
    }

    public void setHead(Head head) {
        this._head = head;
    }

    protected static Map<String, Class<? extends Body>> clazzes = new ConcurrentHashMap<String, Class<? extends Body>>();

    public static Class<? extends Body> getBodyClass(Head head) {
        Class<? extends Body> clazz = null;
        try {
            String versionClass = DEFAULT_BODYPROTOCOL_CLASS_SUFIX + head.getVersion();
            clazz = clazzes.get(versionClass);
            if (clazz == null) {
                clazz = (Class<? extends Body>) Class.forName(versionClass);
                clazzes.put(versionClass, clazz);
            }
        } catch (Exception e) {
            clazz = DEFAULT_BODYPROTOCOL_CLASS;
        }
        return clazz;
    }

    public static Body getDefaultBody() {
        return new Body1();
    }

    public static MetaData getMetaData(Head head, Body body) {
        MetaData minData = new MetaData();
        minData.setHead(head);
        minData.setBody(body);
        return minData;
    }

    public static MetaData getMetaData(Head head, byte[] bodyData) {
        MetaData minData = new MetaData();
        Body body;
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
