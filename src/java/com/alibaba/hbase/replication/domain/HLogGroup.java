package com.alibaba.hbase.replication.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.hadoop.fs.Path;

/**
 * HLog 组
 * @author zalot.zhaoh
 * 
 */
public class HLogGroup{
	String groupName;
	boolean isOver = false;
	protected List<Path> readers = new ArrayList<Path>();
	
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
		Collections.sort(readers);
	}
}
