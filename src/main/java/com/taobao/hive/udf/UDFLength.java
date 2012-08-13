package com.taobao.hive.udf;


import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.Text;
/**
 *  
 *  
 * CREATE TEMPORARY FUNCTION udf_length  AS 'com.taobao.hive.udf.UDFLength';
 *  
 * 
 * @author youliang   
 *  
 *  
 *     
 *     
 */

 
public class UDFLength extends UDF{

	  Text result = new Text();
	  public UDFLength() {
		  
	  } 
 

	  public Text evaluate(String value){
		  
		  try{
			    int valueLength = 0;
		        String chinese = "[\u0391-\uFFE5]";
		        /* 获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1 */
		        for (int i = 0; i < value.length(); i++) {
		            /* 获取一个字符 */
		            String temp = value.substring(i, i + 1);
		            /* 判断是否为中文字符 */
		            if (temp.matches(chinese)) {
		                /* 中文字符长度为2 */
		                valueLength += 2;
		            } else {
		                /* 其他字符长度为1 */
		                valueLength += 1;
		            }
		        }
			    result.set(valueLength+"");
			    return result;
		  }catch(Exception e){
		       result.set("err");
		       return result;
		  }
	  }

		 
 
	 
	  public static void main(String[] args){
		  UDFLength test = new UDFLength();
 
          System.out.println(test.evaluate("111我33暗暗6-我"));   
          	  
	  
	  }
}
