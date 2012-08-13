package com.taobao.hive.udf;

import java.util.regex.Pattern;

import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.Text;
/**
 *  
 * �÷�:  http://www.taobao.com/aaa.html   ���  www.taobao.com  
 * CREATE TEMPORARY FUNCTION getrealurl  AS 'com.taobao.hive.udf.UDFGetRealUrl';
 *  
 *  
 * @author youliang
 *
 */
public class UDFGetRealUrl extends UDF{

	  Text result = new Text();
	  
	  Pattern wwPattern = Pattern.compile("127.0.0.1|/auth");
	  public UDFGetRealUrl() {
	  }

	  public Text evaluate(String s) {
	    if (s == null) {
	      return null;
	    }

	    String sourceUrl = s.toString().toLowerCase();//��ȫ��תΪСд
	      
        if(sourceUrl.startsWith("http://")){
     	   sourceUrl = sourceUrl.substring(7);
        }
 
        if(sourceUrl.startsWith("https://")){
      	   sourceUrl = sourceUrl.substring(8);
        }

        if(sourceUrl.startsWith("http%3a%2f%2f")){
       	   sourceUrl = sourceUrl.substring(13);
        }
        
        if(sourceUrl.startsWith("https%3a%2f%2f")){
        	   sourceUrl = sourceUrl.substring(14);
         }

       
         if(sourceUrl.startsWith("http%3a//")){
      	    sourceUrl = sourceUrl.substring(9);
         }

         if(sourceUrl.startsWith("https%3a//")){
        	sourceUrl = sourceUrl.substring(10);
         }
 
         
         if(sourceUrl.startsWith("http:%2f%2f")){
       	    sourceUrl = sourceUrl.substring(11);
         }
         if(sourceUrl.startsWith("https:%2f%2f")){
        	sourceUrl = sourceUrl.substring(12);
         }
         
 
	       int i = sourceUrl.indexOf("/");

	       if(i<0){
	    	   i = sourceUrl.indexOf("%2f");
	    	   if(i<0){
	    		   i = sourceUrl.length();
	    	   } 
	       }
	        
	       sourceUrl = sourceUrl.substring(0,i);
	   
	       Pattern p = Pattern.compile("shop[0-9]+.taobao.com", Pattern.MULTILINE);
	       if(p.matcher(sourceUrl).find()){
	    	   sourceUrl = "shopXX.taobao.com";
	       }
	       if(sourceUrl.startsWith("127.0.0.1")){
	    	   sourceUrl = "wangwang";
	       }

	    result.set(sourceUrl);
	    return result;
	  }
	  
	  public static void main(String args[]){
		  UDFGetRealUrl test = new UDFGetRealUrl();
		  UDFURLDecode decode = new UDFURLDecode();
		  System.out.println(decode.evaluate(new Text("/1.gif?cache=2360481&pre=http%3A//%2520juzijun.taobao.com/&scr=1024x768&category=item_50010850&userid=&channel=112&at_isb=0&at_autype=5_58730116&ad_id="), "1", "2"));
		  System.out.println(decode.evaluate(new Text("/1.gif?cache=3977993&pre=http%3A%5C%5Cwww.taobao.com/&scr=1024x768&category=&userid=&channel=112&ad_id="), "1", "2"));
		  System.out.println(decode.evaluate(new Text("http%3A//%3A@item.taobao.com/item.htm%3Fid%3D4537059174"), "1", "2"));		  
		  System.out.println(test.evaluate("http://127.0.0.1:8080/"));
		  
		  
	  }
}
