package com.taobao.hive.udf;

import java.util.regex.Pattern;
import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.Text;
/**
 * 从url中解析出htm前N位的字符串
 * 用法:
 * CREATE TEMPORARY FUNCTION get_htmqian_n  AS 'com.taobao.hive.udf.UDFGetHtmlQianN';
 * 
 *  
 * @author youliang
 *
 */
public class UDFGetHtmlQianN extends UDF{

	  Text result = new Text();
	  public UDFGetHtmlQianN() {
		  
	  }

	  public Text evaluate(String url,int n) {
		  try{
			  int index = url.indexOf(".htm");
			  if(index<0){
			       result.set("");
			       return result;
			  }
		  	  String a = url.substring(0, index);
		  	  index = a.lastIndexOf("/");
		  	  String b = a.substring(index+1, a.length());
		 	      String[] c = b.split("-");
		 	      if(c.length<n){
				      result.set("");
				      return result;  
		 	      }
		 	      if(n==0){
				      result.set("");
				      return result;  
		 	      }
		 	      String d = c[c.length-n]; 

			       result.set(d);
			       return result;
		  }catch(Exception e){
		       result.set("err");
		       return result;
		  }
		  
		  

	  }
	  
	  
	  public static void main(String[] args){
		  UDFGetHtmlQianN test = new UDFGetHtmlQianN();
		  System.out.println(test.evaluate("www.dfasdfsd.com22-33-44-55-66-77-88-99-22.htm",0));
		  System.out.println(test.evaluate("http://www.dfasdfsd.com/dfsdfsd/dfas/22dsffsd/22-33-44-55-66-77-88-99-22.htm",99));
	  }
}
