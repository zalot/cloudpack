package com.taobao.hive.udf.urlrecord;

public class Constants {
	public static final int FILE_RULE_BUSINESS_VALUE = 0; //业务
	public static final int FILE_RULE_RELATION_VALUE = 1; //关系
	public static final int FILE_RULE_URL_VALUE = 2; //URL规则
	public static final int FILE_RULE_URL_PROPERTY_VALUE = 3; //URL属性
	public static final int VALIDATE_TYPE_URL = 0;
	public static final int VALIDATE_TYPE_REFER = 1;
	
	public static final String FILE_RULE_BUSINESS = "./rule_type.txt"; //业务
	public static final String FILE_RULE_RELATION = "./rule_url_work.txt"; //关系
	public static final String FILE_RULE_URL = "./rule_url.txt"; //URL规则
	public static final String FILE_RULE_URL_PROPERTY = "./rule_url_property.txt"; //URL属性
	
//	public static final String FILE_RULE_BUSINESS = "D:\\rule_type.txt"; //业务
//	public static final String FILE_RULE_RELATION = "D:\\rule_url_work.txt"; //关系
//	public static final String FILE_RULE_URL = "D:\\rule_url.txt"; //URL规则
//	public static final String FILE_RULE_URL_PROPERTY = "D:\\rule_url_property.txt"; //URL属性
}
