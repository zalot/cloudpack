package com.taobao.hive.udf;

import java.io.UnsupportedEncodingException;
import java.util.regex.Pattern;
import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.Text;

import com.taobao.base64.Base64;


/**
 * 对字符串进行base64编码
 * 用法:
 * CREATE TEMPORARY FUNCTION encodebase64  AS 'com.taobao.hive.udf.UDFEncodeBase64';
 * 
 * 
 * @author youliang
 *
 */
public class UDFEncodeBase64 extends UDF{

	  Text result = new Text();
	  
	  public UDFEncodeBase64() {
		  
		
	  }
	  
	
	  public Text evaluate(String str,String encode) {
		  String base64str = "";
		  if(str==null){
			   return null;
		  }
		  
		  
		  try {
			  base64str = new String(Base64.encodeBase64(str.getBytes()), encode);
		  } catch (UnsupportedEncodingException e) {
			  e.printStackTrace();
		  }
	       result.set(base64str);
	       return result;
	  }

	  public static void main(String args[]){

		  UDFEncodeBase64 test2 = new UDFEncodeBase64();
		  
		  System.out.println(test2.evaluate("aaaa","gbk"));
	  }
	  
	  
	  
	  
	  
	  
	  
	  
}
