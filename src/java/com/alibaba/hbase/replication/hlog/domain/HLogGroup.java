package com.alibaba.hbase.replication.hlog.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * HLog 组，给HLogs 使用
 * @author zalot.zhaoh
 * 
 */
public class HLogGroup {
	String groupName;
	
    public String getGroupName() {
        return groupName;
    }

    int index = 0;
	boolean isOver = false;
	protected List<HLogInfo> hlogInfos = new ArrayList<HLogInfo>();
	
	public HLogGroup(String name) {
		this.groupName = name;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HLogGroup other = (HLogGroup) obj;
		if (groupName == null) {
			if (other.groupName != null)
				return false;
		} else if (!groupName.equals(other.groupName))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((groupName == null) ? 0 : groupName.hashCode());
		return result;
	}

	public boolean isOver() {
		return isOver;
	}

	public void sort() {
		Collections.sort(hlogInfos);
	}
	
	public List<HLogInfo> getHlogInfos(){
		return hlogInfos;
	}
	
	public HLogInfo next(){
		return hlogInfos.get(index++);
	}
	
	public void put(HLogInfo info){
		hlogInfos.add(info);
	}
	
	public boolean hasMoreGroup(){
	    if(index + 1 < hlogInfos.size()){
	        return true;
	    }
	    return false;
	}
}
