package com.taobao.hive.udf;


import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.Text;
/**
 *  
 *  
 * CREATE TEMPORARY FUNCTION udf_concat  AS 'com.taobao.hive.udf.UDFConcat';
 *  
 * 
 * @author youliang   
 *  
 *  
 *     
 *     
 */

 
public class UDFConcatTest extends UDF{

	  Text result = new Text();
	  public UDFConcatTest() {
		  
	  } 
	  
	  
	  public Text evaluate(String...args){
		  return compute(args);
	  }
	  
	
	  public Text compute(String...args){
		  
		  try{
			  if(args==null){
				  result.set("");
				  return result;
			  }
			  
			  
			  String last = args[args.length-1];
			  String split = "";
			  if(last!=null&&last.startsWith("splitby")){
				  split = last.split("_")[1];
			  }
			  StringBuffer return_result = new StringBuffer();
			  for(int i=0;i<args.length;i++){
				  String arg = args[i];
				  if(arg==null){
					  //arg = "";
					  continue;
				  }
				  if(last!=null&&last.startsWith("splitby")){
					  if(i!=args.length-1){
						  return_result.append(arg).append(split);
					  }
				  }else{
					  return_result.append(arg);
				  }
			  }
			  if(last!=null&&last.startsWith("splitby")){
				  result.set(return_result.toString().substring(0, return_result.toString().length()-split.length()));
				  return result;
			  }
			  
			  result.set(return_result.toString());
			  return result;
		  }catch(Exception e){
		       result.set("err");
		       return result;
		  }
	  }

		 
 
	 
	  public static void main(String[] args){
		  UDFConcatTest test = new UDFConcatTest();
          System.out.println(test.evaluate("aa","bb","cc",null));   
          System.out.println(test.evaluate("aa","bb"));
          System.out.println(test.evaluate("aa","bb","cc",null,"dd","splitby_|"));
          System.out.println(test.evaluate("null","bb",":",null,"dd","splitby_,"));
          System.out.println(test.evaluate(null));   
          	  
	  
	  }
}
