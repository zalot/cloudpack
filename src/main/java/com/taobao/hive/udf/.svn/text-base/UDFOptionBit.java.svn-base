package com.taobao.hive.udf;

import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.Text;


/**
 * 
 * ÓÃ·¨:
 * CREATE TEMPORARY FUNCTION optionbit  AS 'com.taobao.hive.udf.UDFOptionBit';
 * 
 * 
 * @author youliang
 *
 */
public class UDFOptionBit extends UDF{

	  Text result = new Text();
	  
	  public UDFOptionBit() {
  
		 
	  }
	  public Text evaluate(String str,String path) {
		  if(str==null){
		       result.set(new Text("0"));
		       return result;
		  }
/*		  if(path==null){
			  return null;
		  }*/
		  
		  
		  int returnvalue = 0;
		  if(str.indexOf("vip")>=0){
			  returnvalue = 1;
		  }
		  if(str.indexOf("ppay")>=0){
			  if(returnvalue!=0){
				  returnvalue = bitor(returnvalue,64);
			  }else{
				  returnvalue = 64;
			  }
		  }
		  if(str.indexOf("suitSeller")>=0){
			  if(returnvalue!=0){
				  returnvalue = bitor(returnvalue,16384);
			  }else{
				  returnvalue = 16384;
			  }
		  }
		  if(str.indexOf("suitBuyer")>=0){
			  if(returnvalue!=0){
				  returnvalue = bitor(returnvalue,32768);
			  }else{
				  returnvalue = 32768;
			  }
		  }
		  if(str.indexOf("wap")>=0){
			  if(returnvalue!=0){
				  returnvalue = bitor(returnvalue,262144);
			  }else{
				  returnvalue = 262144;
			  }
		  }
		  if(str.indexOf("fbuy:1")>=0){
			  if(returnvalue!=0){
				  returnvalue = bitor(returnvalue,524288);
			  }else{
				  returnvalue = 524288;
			  }
		  }
		  if(path!=null&&path.length()>0){
			  if(returnvalue!=0){
				  returnvalue = bitor(returnvalue,131072); 
			  }else{
				  returnvalue = 131072;
			  }
		  }
	     
	       result.set(new Text(returnvalue+""));
	       return result;
	  }
	  
	  
	  public static int bitand(int m,int n){
		  return m&n; 
	  }
	  public static int bitor(int m,int n){
		  int result = (m+n)-bitand(m,n);
		  return result; 
	  }
	  
	  
	  
	  
	  public static void main(String args[]){
		   
		  
		  
	
		  UDFOptionBit test = new UDFOptionBit();
		  System.out.println(test.evaluate("vipfbuy:1suitSeller", "ddd"));
		 
	  }
	  
	  
	  
	  
	  
	  
	  
	  
}
