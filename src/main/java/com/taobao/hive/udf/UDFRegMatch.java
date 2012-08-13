package com.taobao.hive.udf;

import java.util.regex.Pattern;
import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.Text;
/**
 * 判断url是否符合正则表达式
 * 用法:
 * CREATE TEMPORARY FUNCTION regMatch  AS 'com.taobao.hive.udf.UDFRegMatch';
 * 返回0表示不匹配,返回1表示匹配
 *  
 * @author youliang
 *
 */
public class UDFRegMatch extends UDF{

	  Text result = new Text();
	  public UDFRegMatch() {
		  
	  }

	  //返回0表示不匹配,返回1表示匹配
	  public Text evaluate(String url,String pattern) {
	       String returnResult = "0";
 		   Pattern p = Pattern.compile(pattern, Pattern.MULTILINE);
		   if(p.matcher(url).find()){
			  returnResult = "1";
		   }
	     
	       result.set(returnResult);
	       return result;
	  }
}
