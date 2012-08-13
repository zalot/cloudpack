package com.taobao.hive.udf.urlrecord;

import java.io.IOException;

import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.Text;

/*
 * 需求地址：http://confluence.taobao.ali.com:8080/pages/viewpage.action?pageId=192026310
 * CREATE TEMPORARY FUNCTION get_url_allbiz  AS 'com.taobao.hive.udf.urlrecord.UDFGetUrlBiz';
 * get_url_allbiz(time,url,ref); 
 * 	----传入url和ref 返回所有符合条件的业务id串，用逗号分隔：biz1,biz2,biz3，无符合的业务id返回空
 * get_url_allbiz(time,url,ref,prop); 
 * 	----传入url、ref 和参数名称返回所有符合条件的业务id和参数值串，业务id和参数值用冒号分隔，
 * 	不同业务用逗号分隔：biz1:val1,biz2:val2,biz3:val3，无符合的业务id返回空
 */

public class UDFGetUrlBiz extends UDF{
	
	public UDFGetUrlBiz(){}
	
	private Text result = new Text(); 
	private UrlRuleHandler handler = new UrlRuleHandler();
	
	public Text evaluate(String time,String url,String refer) throws IOException{
		handler.init();
		String bizIDs = "";
		for (String bizId: handler.getRuleBusiness().keySet()) {
			if (handler.validateBiz(time, url, refer, bizId,"pre=")) bizIDs += bizId + ",";
		}
		if (!"".equals(bizIDs)) bizIDs = bizIDs.substring(0,bizIDs.length()-1);
		result.set(bizIDs);
		return result;
	}
	
	public Text evaluate(String time,String url,String refer,String prop) throws IOException{
		String bizIDs = "";
		for (String bizId: handler.getRuleBusiness().keySet()) {
			String value = handler.getPropValue(time, url, refer, bizId, prop,"pre=");
			if (value != null) bizIDs += (bizId + ":" + value) + ";";
		}
		if (!"".equals(bizIDs)) bizIDs = bizIDs.substring(0,bizIDs.length()-1);
		result.set(bizIDs);
		return result;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			UDFGetUrlBiz biz = new UDFGetUrlBiz();
			String refer = "/1.gif?cache=4124541&pre=http%3A//s.taobao.com/search%3Fq%3D%25BB%25E1%25BC%25C6%25B0%25EC%25B9%25AB%25D3%25C3%25C6%25B7%26sort%3Dcredit-desc&scr=1024x768&category=item_50012708&userid=&tid=6cef52db816997800b6a46238327ff86&channel=112&at_isb=0&at_autype=5_33517315&ad_id= 5451729810408116229";
			String url = "http://item.taobao.com/item.htm?id=8455365";
			System.out.println(biz.evaluate("20110327000000",url,refer));
			System.out.println(biz.evaluate("20110327000000",url,refer,"121"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
