package com.taobao.hive.udf;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.Text;
/**
 *  
 * 用法:  
 * CREATE TEMPORARY FUNCTION getgroup  AS 'com.taobao.hive.udf.UDFGetPatternGroup';
 * select getgroup("123,456", "(\\d+,)(\\d+)", 1) from dual;
 * 从字符串中解析出符合指定正则的某一组group的值  group从1开始
 * @author youliang
 *
 */
public class UDFGetPatternGroup extends UDF{

	  Text result = new Text();
	  public UDFGetPatternGroup() {
	  }

	  public Text evaluate(String s,String pattern,int group) {
	    if (s == null) {
	       return null;
	    }
	    if (pattern == null) {
		   return null;
		}
	    
	    Pattern p = null;
	    try{
	    	p = Pattern.compile(pattern);
	    }catch(Exception e){
	    	return null;
	    }
        Matcher m = p.matcher(s);
        String resultReturn = null;
        while(m.find()){
             try{
            	 resultReturn = m.group(group);
             }catch(Exception e){
            	 
             }  
        }
        if(resultReturn==null){
        	return null;
        }
	    result.set(resultReturn);
	    return result;
	  }

	  public static void main(String args[]){
		  UDFGetPatternGroup test = new UDFGetPatternGroup();
		  System.out.println(test.evaluate("123,456", "(\\d+,)(\\d+)", 2));
	  }
}
