package com.alibaba.hbase.replication.protocol;

public class Version1 implements MetaData{
    protected Body body;
    protected Head head;
    
    public void setHead(){
        
    }
    public void setBody(){
        
    }
    
	@Override
	public Head getHead() {
		return null;
	}

	@Override
	public Body getBody() {
		return null;
	}
}
