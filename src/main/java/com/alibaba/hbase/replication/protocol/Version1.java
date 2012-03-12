package com.alibaba.hbase.replication.protocol;

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
}
