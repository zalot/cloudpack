package com.taobao.hive.udf;


import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.Text;
/**
 *  
 *  
 * CREATE TEMPORARY FUNCTION udf_last_xing  AS 'com.taobao.hive.udf.UDFLastXing';
 *  
 * 
 * @author youliang   
 *  
 *  
 *     
 *     
 */

//给临时需求用
public class UDFLastXing extends UDF{

	  Text result = new Text();
	  public UDFLastXing() {
		  
	  } 
	  public Text evaluate(String str,String i){
		  
		  try{
			  if(str.length()>=Integer.parseInt(i)){
				  String str1 = str.substring(0,str.length()-Integer.parseInt(i));
				  //System.out.println(str1);
				  String str2 = "";
				  for(int j=0;j<Integer.parseInt(i);j++){
					  str2+="*";
				  }
				  //System.out.println(str1+str2);
				  result.set(str1+str2);
				  return result;
			  }else{
				  //System.out.println("222222");
			  }
			  
			  
		  }catch(Exception e){
		       result.set("err");
		       return result;
		  }
		return result;
	  }

 

		 
 
	 
	  public static void main(String[] args){
		  UDFLastXing test = new UDFLastXing();
          System.out.println(test.evaluate("12345557890", "3"));
          	  
	  
	  }
}
