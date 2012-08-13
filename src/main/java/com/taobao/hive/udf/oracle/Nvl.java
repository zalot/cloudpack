package com.taobao.hive.udf.oracle;

import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.Text;
 

/**
 * Âß¼­µÈ¼ÛÓÚIF c1 is null THEN c2 ELSE c1 END
 * CREATE TEMPORARY FUNCTION Nvl  AS 'com.taobao.hive.udf.oracle.Nvl';
 * @author youliang
 *
 */
public class Nvl extends UDF{

	  Text result = new Text();
	  
	  public Nvl() {
		  
	  }

	  public Text evaluate(String n1,String n2) {
	    try{
	    	if(n1==null){
	    	    result.set(n2+"");
	    	    return result;
	    	}else{
	    	    result.set(n1+"");
	    	    return result;
	    	}
	    }catch(Exception e){
	    	return null;
	    }
	  }
	  
	  public static void main(String args[]){
		  Nvl test = new Nvl();
		  System.out.println(test.evaluate("222","333"));
		  System.out.println(test.evaluate(null,"333"));
		  System.out.println(test.evaluate("222",null));
		  System.out.println(test.evaluate(null,null));
	  }
}