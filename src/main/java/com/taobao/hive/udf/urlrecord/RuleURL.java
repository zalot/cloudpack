package com.taobao.hive.udf.urlrecord;

import java.util.regex.Matcher;

public class RuleURL{
	private String id;
	private String ruleName;
	private String ruleValue;
	private String propertyId;
	private String startDate;
	private String endDate;
	private String status;
	private String path;
	private Matcher ruleMatcher;
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getRuleName() {
		return ruleName;
	}
	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}
	public String getRuleValue() {
		return ruleValue;
	}
	public void setRuleValue(String ruleValue) {
		this.ruleValue = ruleValue;
	}
	public String getPropertyId() {
		return propertyId;
	}
	public void setPropertyId(String propertyId) {
		this.propertyId = propertyId;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Matcher getRuleMatcher() {
		return ruleMatcher;
	}
	public void setRuleMatcher(Matcher ruleMatcher) {
		this.ruleMatcher = ruleMatcher;
	}
} 
