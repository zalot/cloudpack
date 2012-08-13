package com.taobao.hive.udf.urlrecord;

import java.util.ArrayList;
import java.util.List;

public class RuleBusiness {
	private String id;
	private String businessName;
	private String parentId;
	private String status;
	private List<String> relationIds = new ArrayList<String>();
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getBusinessName() {
		return businessName;
	}
	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public List<String> getRelationIds() {
		return relationIds;
	}
	public void setRelationIds(List<String> relationIds) {
		this.relationIds = relationIds;
	}
}
