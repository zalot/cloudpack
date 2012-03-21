package com.alibaba.hbase.replication.protocol;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 协议元数据 类MetaData.java的实现描述：TODO 类实现描述
 * 
 * @author zalot.zhaoh Mar 19, 2012 11:51:21 AM
 */
public abstract class MetaData {

    protected static final String DEFAULT_CLASS_SUFIX = "com.alibaba.hbase.replication.protocol.Version";
    protected static final Class  DEFAULT_CLASS       = com.alibaba.hbase.replication.protocol.Version1.class;
    protected Head                _head;
    protected Body                _body;

    public abstract Head getHead();

    public abstract Body getBody();

    public abstract byte[] getBodyData() throws Exception;

    public abstract void setBodyData(byte[] data) throws Exception;

    public abstract void setBody(Body body) throws Exception;

    public MetaData(){
    }

    public void setHead(Head head) {
        this._head = head;
    }

    protected static Map<String, Class<? extends MetaData>> clazzes = new ConcurrentHashMap<String, Class<? extends MetaData>>();

    public static Class<? extends MetaData> getClass(Head head) {
        Class<? extends MetaData> clazz = null;
        try {
            String versionClass = DEFAULT_CLASS_SUFIX + head.getVersion();
            clazz = clazzes.get(versionClass);
            if (clazz == null) {
                clazz = (Class<? extends MetaData>) Class.forName(versionClass);
                clazzes.put(versionClass, clazz);
            }
        } catch (Exception e) {
            clazz = DEFAULT_CLASS;
        }
        return clazz;
    }

    public static MetaData getMetaData(Head head, Body body) {
        MetaData minData;
        try {
            minData = getClass(head).newInstance();
            minData.setHead(head);
            minData.setBody(body);
            return minData;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static MetaData getMetaData(Head head, byte[] bodyData) {
        MetaData minData;
        try {
            minData = getClass(head).newInstance();
            minData.setHead(head);
            minData.setBodyData(bodyData);
            return minData;
        } catch (Exception e) {
        }

        return null;
    }
}
