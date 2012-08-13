package com.taobao.hive.udf.oracle;

import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.Text;
 

/**
 * 如果c1非空则返回c2,如果c1为空则返回c3
 * CREATE TEMPORARY FUNCTION nvl2  AS 'com.taobao.hive.udf.oracle.Nvl2';
 * @author youliang
 *
 */
public class Nvl2 extends UDF{

	  Text result = new Text();
	  
	  public Nvl2() {
		  
	  }

	  public Text evaluate(String c1,String c2,String c3) {
	    try{
	    	if(c1==null){
	    	    result.set(c3+"");
	    	    return result;
	    	}else{
	    	    result.set(c2+"");
	    	    return result;
	    	}
	    }catch(Exception e){
	    	return null;
	    }
	  }
	  
	  public static void main(String args[]){
		  Nvl2 test = new Nvl2();
		  System.out.println(test.evaluate("222","333","444"));
		  System.out.println(test.evaluate(null,"333","444"));
		  System.out.println(test.evaluate(null,null,"444"));
		  System.out.println(test.evaluate(null,"333",null));
	  }
}