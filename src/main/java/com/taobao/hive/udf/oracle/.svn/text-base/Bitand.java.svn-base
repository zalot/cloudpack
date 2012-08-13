package com.taobao.hive.udf.oracle;

import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.Text;



/**
 *  
 * CREATE TEMPORARY FUNCTION bitand  AS 'com.taobao.hive.udf.oracle.Bitand';
 * @author youliang
 *
 */
public class Bitand extends UDF{

	  Text result = new Text();
	  
	  public Bitand() {
		  
	  }
	  
	  
	  public Text evaluate(String n1,String n2) {
		    if (n1 == null || n2 == null) {
		        return null;
		    }
		    try{
		    	int m = Integer.parseInt(n1);
		    	int n = Integer.parseInt(n2);
		    	int result_value = m&n;
	    	    result.set(result_value+"");
	    	    return result;
		    }catch(Exception e){
		    	return null;
		    }
	   }
	  
	  
	  
	  
	  

	  
	  
	  public static void main(String args[]){
		  Bitand test = new Bitand();
		  System.out.println(test.evaluate("3333","77777"));
		  System.out.println(test.evaluate("3333","77777"));
		  System.out.println(test.evaluate(null,"77777"));
		  System.out.println(test.evaluate("323","77777"));
		 
	  }
}