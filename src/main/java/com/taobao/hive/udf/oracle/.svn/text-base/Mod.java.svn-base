package com.taobao.hive.udf.oracle;

import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.Text;
 

/**
 * 返回n1除n2的余数，如果n2=0则返回n1的值。
 * CREATE TEMPORARY FUNCTION mod  AS 'com.taobao.hive.udf.oracle.Mod';
 * @author youliang
 *
 */
public class Mod extends UDF{

	  Text result = new Text();
	  
	  public Mod() {
		  
	  }

	  public Text evaluate(String n1,String n2) {
	    if (n1 == null||n2 == null) {
	        return null;
	    }
	    try{
	    	int a1 = Integer.parseInt(n1);
	    	int a2 = Integer.parseInt(n2);
	    	if(a2==0){
	    	    result.set(a1+"");
	    	    return result;
	    	}
	    	int result_value = a1%a2;
    	    result.set(result_value+"");
    	    return result;
	    }catch(Exception e){
	    	return null;
	    }
	  }
	  
	  public static void main(String args[]){
		  Mod test = new Mod();
		  System.out.println(test.evaluate("25","0"));
		  System.out.println(test.evaluate("25","3"));
		  System.out.println(test.evaluate("25","5"));
		  System.out.println(test.evaluate("0","25"));
		  System.out.println(test.evaluate(null,"25"));
		  System.out.println(test.evaluate(null,null));
		  System.out.println(test.evaluate("a","25"));
		  
		  
	  }
}