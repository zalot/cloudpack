package com.taobao.hive.udf.oracle;

import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.Text;
import java.lang.Math;   
import java.math.BigDecimal; 


/**
 * 返回四舍五入小数点右边n2位后n1的值，n2缺省值为0，如果n2为负数就舍入到小数点左边相应的位上
 * CREATE TEMPORARY FUNCTION round  AS 'com.taobao.hive.udf.oracle.Round';
 * @author youliang
 *
 */
public class Round extends UDF{

	  Text result = new Text();
	  
	  public Round() {
		  
	  }
	  
	  
	  public Text evaluate(String n1) {
		    if (n1 == null) {
		        return null;
		    }
		    try{
		    	double a = Double.parseDouble(n1);
		    	int result_value = (int)Math.round(a);
	    	    result.set(result_value+"");
	    	    return result;
		    }catch(Exception e){
		    	return null;
		    }
	   }
	  
	  
	  
	  
	  

	  public Text evaluate(String n1,String n2) {
		    if (n1 == null || n2 == null) {
		        return null;
		    }
		    try{
		    	BigDecimal b = new BigDecimal(n1); 
		    	BigDecimal one = new BigDecimal("1"); 
		    	double result_value = b.divide(one, Integer.parseInt(n2), BigDecimal.ROUND_HALF_UP).doubleValue(); 
	    	    result.set(result_value+"");
	    	    return result;
		    }catch(Exception e){
		    	return null;
		    }
	   }
	  
	  public static void main(String args[]){
		  Round test = new Round();
		  System.out.println(test.evaluate("25"));
		  System.out.println(test.evaluate("25.01"));
		  System.out.println(test.evaluate("25.0a1"));
		  System.out.println(test.evaluate("25.06"));
		  System.out.println(test.evaluate("25.46"));
		  System.out.println(test.evaluate("25.56"));
		  System.out.println(test.evaluate(null));
		  
		  System.out.println("------------------------------------------------");
		  System.out.println(test.evaluate("25.01","0"));
		  System.out.println(test.evaluate("23.01","-1"));
		  System.out.println(test.evaluate("26.01","-1"));
		  System.out.println(test.evaluate("25.01","1"));
		  System.out.println(test.evaluate("25.01","2"));
		  System.out.println(test.evaluate("25.31","1"));
		  System.out.println(test.evaluate("25.61","1"));
		  System.out.println(test.evaluate("25.65","2"));
		  System.out.println(test.evaluate("25.66","2"));
		  System.out.println(test.evaluate("25.667","2"));
		  System.out.println(test.evaluate("25.667","2.8"));
		  System.out.println(test.evaluate("26.01","-2"));
		  System.out.println(test.evaluate("76.01","-2"));
	  }
}