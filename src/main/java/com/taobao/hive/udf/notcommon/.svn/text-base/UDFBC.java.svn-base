package com.taobao.hive.udf.notcommon;

import org.apache.hadoop.hive.ql.exec.UDF;

public class UDFBC extends UDF {
	
	public UDFBC(){};
	
	public int evaluate(String refer, String url) {
		
		if (url == null || url.equals(""))
            return DOMAIN_TYPE_OTHER;
		
		if (url.matches("(.*[\\W]+)?(taobao|alipay|wozhongle|tmall)\\.(com|com[\\W]+.*)")){
		   if(url.indexOf("http://mall.taobao.com") == 0 || url.indexOf(".mall.taobao.com") >= 0 || url.indexOf(".tmall.com") >= 0)
		     return DOMAIN_TYPE_B2C;
		   if (refer == null || refer.equals(""))
	             return DOMAIN_TYPE_C2C;
		   if(refer.indexOf("at_isb=") >= 0)
		   {
		     int start = refer.indexOf("at_isb=");
		     start += 7;
		     int end = refer.indexOf("&",start);
		     if(end == -1)
			end = refer.length();
		     String tmp = refer.substring(start,end);
		     if(tmp.equals("1") == true)
			return DOMAIN_TYPE_B2C;
		     else
			return DOMAIN_TYPE_C2C;
		   }
		   else//
		     return DOMAIN_TYPE_C2C;
		}
		return DOMAIN_TYPE_OTHER;
	}
	
	public static int DOMAIN_TYPE_C2C = 0;
	public static int DOMAIN_TYPE_B2C = 1;
	public static int DOMAIN_TYPE_OTHER = -1;
}
