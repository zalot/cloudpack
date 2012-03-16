package com.alibaba.hbase.replication.protocol;

import com.alibaba.hbase.replication.protocol.protobuf.BodySerializingHandler;
import com.google.protobuf.InvalidProtocolBufferException;

public class Version1 implements MetaData{
    public static final int VERSION = 1;
    protected Body body;
    protected Head head;
    
    public Version1(Head head, Body body){
        this.head = head;
        this.body = body;
    }
    
	@Override
	public Head getHead() {
	    head.setHeadTimestamp(System.currentTimeMillis());
	    head.setVersion(VERSION);
		return head;
	}

	@Override
	public Body getBody() {
		return body;
	}

    @Override
    public byte[] getBodyData() {
        return BodySerializingHandler.serialize(body);
    }

    @Override
    public void setBodyData(byte[] data) {
        try {
            body = BodySerializingHandler.deserialize(data);
        } catch (InvalidProtocolBufferException e) {
            body = null;
        }
    }
}
