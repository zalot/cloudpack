package com.taobao.hive.udf.urlrecord;

import java.util.regex.Pattern;

public class URLRecord {
	private String bizId;
	private Pattern urlPattern;
	private Pattern referPattern;
	private String property;
	
	public String getBizId() {
		return bizId;
	}
	public void setBizId(String bizId) {
		this.bizId = bizId;
	}
	public Pattern getUrlPattern() {
		return urlPattern;
	}
	public void setUrlPattern(Pattern urlPattern) {
		this.urlPattern = urlPattern;
	}
	public Pattern getReferPattern() {
		return referPattern;
	}
	public void setReferPattern(Pattern referPattern) {
		this.referPattern = referPattern;
	}
	public String getProperty() {
		return property;
	}
	public void setProperty(String propertyId) {
		this.property = propertyId;
	}
}
