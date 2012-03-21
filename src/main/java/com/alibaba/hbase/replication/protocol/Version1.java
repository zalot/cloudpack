package com.alibaba.hbase.replication.protocol;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.lang.UnhandledException;

import com.alibaba.hbase.replication.protocol.protobuf.BodySerializingHandler;

/**
 * 类Version1.java的实现描述：TODO 类实现描述
 * 
 * @author zalot.zhaoh Mar 20, 2012 4:19:34 PM
 */
public class Version1 extends MetaData {

    public static final int VERSION = 1;

    public Version1() throws Exception{
        super();
    }

    @Override
    public Head getHead() {
        this._head.setHeadTimestamp(System.currentTimeMillis());
        this._head.setVersion(VERSION);
        return this._head;
    }

    @Override
    public Body getBody() {
        return this._body;
    }

    @Override
    public byte[] getBodyData() throws Exception {
        if (this._body != null) return gzip(BodySerializingHandler.serialize(this._body));
        return new byte[0];
    }

    @Override
    public void setBodyData(byte[] data) throws Exception {
        this._body = BodySerializingHandler.deserialize(ungzip(data));
    }

    protected byte[] gzip(byte[] data) throws Exception {
        byte[] rs;
        GZIPOutputStream gzipOut = null;
        ByteArrayOutputStream byteOut = null;
        try {
            byteOut = new ByteArrayOutputStream();
            gzipOut = new GZIPOutputStream(byteOut);
            gzipOut.write(data);
            gzipOut.finish();
            gzipOut.close();
            rs = byteOut.toByteArray();
            byteOut.close();
            return rs;
        } finally {
            if (gzipOut != null) {
                gzipOut.close();
            }
            if (byteOut != null) {
                byteOut.close();
            }
            gzipOut = null;
            byteOut = null;
        }
    }

    public static byte[] ungzip(byte[] data) throws Exception {
        byte[] b = null;
        ByteArrayInputStream byteIn = null;
        GZIPInputStream gzipIn = null;
        ByteArrayOutputStream byteOut = null;
        try {
            byteIn = new ByteArrayInputStream(data);
            gzipIn = new GZIPInputStream(byteIn);
            byte[] buf = new byte[1024];
            int num = -1;
            byteOut = new ByteArrayOutputStream();
            while ((num = gzipIn.read(buf, 0, buf.length)) != -1) {
                byteOut.write(buf, 0, num);
            }
            b = byteOut.toByteArray();
            byteOut.flush();
            byteOut.close();
            gzipIn.close();
            byteIn.close();
        } finally {
            if (gzipIn != null) {
                gzipIn.close();
            }
            if (byteIn != null) {
                byteIn.close();
            }
            if (byteOut != null) {
                byteOut.close();
            }
            gzipIn = null;
            byteOut = null;
            byteIn = null;
        }
        return b;
    }

    @Override
    public void setBody(Body body) throws Exception {
        this._body = body;
    }
    
    public static void main(String[] args){
        byte[] bytes = new byte[795726865];
    }
}
