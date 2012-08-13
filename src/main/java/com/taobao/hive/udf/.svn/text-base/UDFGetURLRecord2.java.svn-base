package com.taobao.hive.udf;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.Text;

import com.taobao.hive.udf.urlrecord.UrlRuleHandler;

public class UDFGetURLRecord2 extends UDF{
	
	static final Log LOG = LogFactory.getLog(UDFGetURLRecord2.class.getName());
	
	private Text result = new Text(); 
	private UrlRuleHandler handler = new UrlRuleHandler();
	private static final String DEFAULT_PREFIX = "pre=";
	private static final String DEFAULT_SPLITOR = "-";
	
	public Text evaluate(String time,String url,String refer,String type_ids) throws IOException{
		return evaluate(time,url,refer,type_ids,DEFAULT_PREFIX);
	}
	
	public Text evaluate(String time,String url,String refer,String type_ids,String property_id,String flag) throws IOException{
		return evaluate(time,url,refer,type_ids,property_id,flag,DEFAULT_PREFIX);
	}
	
	public Text evaluate(String time,String url,String refer,String type_ids,String prefix) throws IOException{
		if(time==null||url==null||refer==null||type_ids==null||prefix==null){
			result.set("false");
		    return result;
		}
		handler.init();
		String[] ruleIds = type_ids.split(DEFAULT_SPLITOR);
		for (int i=0; i<ruleIds.length; i++) {
			if (handler.validateBiz(time, url, refer, ruleIds[i],prefix)) {
				result.set("true");
				return result;
			}
			
		}
		result.set("false");
	    return result;
	}
	
	public Text evaluate(String time,String url,String refer,String type_ids,String property_id,String flag,String prefix) throws IOException{
		if(time==null||url==null||refer==null||type_ids==null||prefix==null || property_id==null){
		    return null;
		}
		handler.init();
		String[] ruleIds = type_ids.split(DEFAULT_SPLITOR);
		for (int i=0; i<ruleIds.length; i++) {
			String value = handler.getPropValue(time, url, refer, ruleIds[i],property_id,flag,prefix);
			if (value != null) {
				result.set(value);
				return result;
			}
			
		}
	    return null;
	}
}
