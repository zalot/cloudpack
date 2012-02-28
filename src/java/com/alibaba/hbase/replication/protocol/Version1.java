package com.alibaba.hbase.replication.protocol;

public class Version1 implements MetaData{
    protected Body body;
    protected Head head;
    
    public Version1(Head head, Body body){
        this.head = head;
        this.body = body;
    }
    
	@Override
	public Head getHead() {
		return head;
	}

	@Override
	public Body getBody() {
		return body;
	}
}
