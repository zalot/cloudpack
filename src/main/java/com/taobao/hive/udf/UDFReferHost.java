package com.taobao.hive.udf;

import java.util.regex.Pattern;
import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.Text;


/**
 * 从url中解析出指定的referhost
 * 用法:
 * CREATE TEMPORARY FUNCTION getReferHost  AS 'com.taobao.hive.udf.UDFReferHost';
 * getReferHost(url)   得到对应的referhost
 * 
 * @author youliang
 *
 */
public class UDFReferHost extends UDF{

	  Text result = new Text();
	  static RegexMap<String> mapTest = new RegexMap<String>();
	  public UDFReferHost() {
		  //referhost维表
		  String[] zhengze ={ 
				  "\\.alibaba\\.com",
				  "\\.alimama\\.com",
				  "\\.alipay\\.com",
				  "http://archive\\.taobao\\.com",
				  "http://auction1\\.taobao\\.com",
				  "\\.baidu\\.com",
				  "http://buy\\.taobao\\.com",    
				  "http://click\\.p4p\\.cn\\.yahoo\\.com",  
				  "http://dating\\.taobao\\.com",
				  "http://edyna\\..*\\.taobao\\.com",
				  "http://favorite\\.taobao\\.com",
				  "http://forum\\.taobao\\.com",     
				  "\\.google\\.cn", 
				  "\\.google\\.com",
				  "http://itaobao\\.taobao\\.com",  
				  "http://item\\.taobao\\.com",
				  "http://list\\.taobao\\.com",  
				  "http://member1\\.taobao\\.com",
				  "http://my\\.taobao\\.com",   
				  "\\.qq\\.com",   
				  "http://rate\\.taobao\\.com",     
				  "http://search1\\.taobao\\.com",    
				  "http://search2\\.cn\\.yahoo\\.com",    
				  "http://search8\\.taobao\\.com",       
				  "http://search\\.cn\\.yahoo\\.com",     
				  "http://sell\\.taobao\\.com",          
				  "http://share\\.taobao\\.com",         
				  "http://shop[0-9]+\\.taobao\\.com",    
				  "\\.sina\\.com\\.cn",    
				  "\\.soso\\.com",        
				  "http://space\\.taobao\\.com",         
				  "http://store\\.taobao\\.com",         
				  "http://tb\\.p4p\\.cn\\.yahoo\\.com",    
				  "http://trade\\.taobao\\.com",         
				  "http://upload\\.taobao\\.com",        
				  "http://wuliu\\.taobao\\.com",         
				  "http://www\\.obuy\\.cn",      
				  "http://www\\.taobao\\.com",           
				  "\\.yahoo\\.com",       
				  "\\.youdao\\.com",      
				  "http://list\\.mall\\.taobao\\.com",    
				  "http://www\\.mall\\.taobao\\.com",     
				  "http://mservice\\.taobao\\.com",      
				  "http://mall\\.taobao\\.com",          
				  "http://click\\.simba\\.taobao\\.com",          
				  "http://bangpai\\.taobao\\.com",     
				  "http://search\\.taobao\\.com",
				  "http://jianghu\\.taobao\\.com",
				  "http://i\\.taobao\\.com",
				  "http://huoban\\.taobao\\.com",
				  "http://info\\.taobao\\.com",
				  "http://bbs\\.taobao\\.com",
				  "http://caipiao\\.taobao\\.com",
				  "http://poster\\.taobao\\.com",
				  "http://huabao\\.taobao\\.com",
				  "http://service\\.taobao\\.com",
				  "http://ishop\\.taobao\\.com",
				  "http://madou\\.taobao\\.com",
				  "http://list1\\.taobao\\.com",
				  "http://s\\.taobao\\.com"
		  };     
		   
		  for(int i=0;i<zhengze.length;i++){
			  Pattern p = Pattern.compile(zhengze[i].trim(), Pattern.MULTILINE);
		      mapTest.put(p, zhengze[i].trim());   
		  }
	  }
	  
	  //备注： 如果传入的不是url，而是refer的前一页的话，注意先url_decode一下
	  public Text evaluate(String url) {
		  if(url==null){
			  return null;
		  }
		  try{
			   String referhost = "kong";
	           if(!url.equals("")){
	    		   referhost = mapTest.get(url);
	               if(referhost==null){
	            	   referhost = "other";
	               }
	           }
	           
	           if(referhost.startsWith("http://")){
	        	   referhost = referhost.replace("http://","");
	           }
	           
		       result.set(referhost);
		       return result;
		  }catch(Exception e){
		       result.set("err");
		       return result;
		  }

	  }
	  
	  
	  
	  
	  
	  
	  public static void main(String args[]){
		   
		  
		  
	
		  UDFReferHost test2 = new UDFReferHost();
		  
		  System.out.println(test2.evaluate(null));
	  }
	  
	  
	  
	  
	  
	  
	  
	  
}
