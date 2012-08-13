package com.taobao.hive.udf;

import java.io.UnsupportedEncodingException;
import java.util.regex.Pattern;
import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.Text;

import com.taobao.base64.Base64;


/**
 * 从url中解析出指定的referhost
 * 用法:
 * CREATE TEMPORARY FUNCTION decodebase64  AS 'com.taobao.hive.udf.UDFDecodeBase64';
 * 
 * 
 * @author youliang
 *
 */
public class UDFDecodeBase64 extends UDF{

	  Text result = new Text();
	  
	  public UDFDecodeBase64() {
		  
		
	  }
	  
	
	  public Text evaluate(String str,String encode) {

		  
		  String base64str = "";
		  try {
			  
			  if(str==null){
				   return null;
			  }
			  base64str = new String(Base64.decodeBase64(str.getBytes()), encode);
		  } catch (Exception e) {
		       result.set("err");
		       return result;
		  }
	       result.set(base64str);
	       return result;
	  }

	  public static void main(String args[]){

		  UDFDecodeBase64 test2 = new UDFDecodeBase64();
		  
		  System.out.println(test2.evaluate("YWFhYQ==","gbk"));
	  }
	  
	  
	  
	  
	  
	  
	  
	  
}
