package com.taobao.hive.udf;


import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.Text;
/**
 *  
 *  
 * CREATE TEMPORARY FUNCTION udf_avg  AS 'com.taobao.hive.udf.UDFAvg';
 *  
 * 
 * @author youliang   
 *  
 *  
 *     
 *     
 */

 
public class UDFAvg extends UDF{

	  Text result = new Text();
	  public UDFAvg() {
		  
	  } 
	  public Text evaluate(String arg1){
		  return compute(arg1);
	  }
	  public Text evaluate(String arg1,String arg2){
		  return compute(arg1,arg2);
	  }
	  public Text evaluate(String arg1,String arg2,String arg3){
		  return compute(arg1,arg2,arg3);
	  }
	  public Text evaluate(String arg1,String arg2,String arg3,String arg4){
		  return compute(arg1,arg2,arg3,arg4);
	  }
	  public Text evaluate(String arg1,String arg2,String arg3,String arg4,String arg5){
		  return compute(arg1,arg2,arg3,arg4,arg5);
	  }
	  public Text evaluate(String arg1,String arg2,String arg3,String arg4,String arg5,String arg6){
		  return compute(arg1,arg2,arg3,arg4,arg5,arg6);
	  }
	  public Text evaluate(String arg1,String arg2,String arg3,String arg4,String arg5,String arg6,String arg7){
		  return compute(arg1,arg2,arg3,arg4,arg5,arg6,arg7);
	  }
	  public Text evaluate(String arg1,String arg2,String arg3,String arg4,String arg5,String arg6,String arg7,String arg8){
		  return compute(arg1,arg2,arg3,arg4,arg5,arg6,arg7,arg8);
	  }
	  public Text evaluate(String arg1,String arg2,String arg3,String arg4,String arg5,String arg6,String arg7,String arg8,String arg9){
		  return compute(arg1,arg2,arg3,arg4,arg5,arg6,arg7,arg8,arg9);
	  }
	  public Text evaluate(String arg1,String arg2,String arg3,String arg4,String arg5,String arg6,String arg7,String arg8,String arg9,String arg10){
		  return compute(arg1,arg2,arg3,arg4,arg5,arg6,arg7,arg8,arg9,arg10);
	  }
	  public Text evaluate(String arg1,String arg2,String arg3,String arg4,String arg5,String arg6,String arg7,String arg8,String arg9,String arg10,String arg11){
		  return compute(arg1,arg2,arg3,arg4,arg5,arg6,arg7,arg8,arg9,arg10,arg11);
	  }
	  public Text evaluate(String arg1,String arg2,String arg3,String arg4,String arg5,String arg6,String arg7,String arg8,String arg9,String arg10,String arg11,String arg12){
		  return compute(arg1,arg2,arg3,arg4,arg5,arg6,arg7,arg8,arg9,arg10,arg11,arg12);
	  }
	  public Text evaluate(String arg1,String arg2,String arg3,String arg4,String arg5,String arg6,String arg7,String arg8,String arg9,String arg10,String arg11,String arg12,String arg13){
		  return compute(arg1,arg2,arg3,arg4,arg5,arg6,arg7,arg8,arg9,arg10,arg11,arg12,arg13);
	  }
	  public Text evaluate(String arg1,String arg2,String arg3,String arg4,String arg5,String arg6,String arg7,String arg8,String arg9,String arg10,String arg11,String arg12,String arg13,String arg14){
		  return compute(arg1,arg2,arg3,arg4,arg5,arg6,arg7,arg8,arg9,arg10,arg11,arg12,arg13,arg14);
	  }
	  public Text evaluate(String arg1,String arg2,String arg3,String arg4,String arg5,String arg6,String arg7,String arg8,String arg9,String arg10,String arg11,String arg12,String arg13,String arg14,String arg15){
		  return compute(arg1,arg2,arg3,arg4,arg5,arg6,arg7,arg8,arg9,arg10,arg11,arg12,arg13,arg14,arg15);
	  }
	  public Text evaluate(String arg1,String arg2,String arg3,String arg4,String arg5,String arg6,String arg7,String arg8,String arg9,String arg10,String arg11,String arg12,String arg13,String arg14,String arg15,String arg16){
		  return compute(arg1,arg2,arg3,arg4,arg5,arg6,arg7,arg8,arg9,arg10,arg11,arg12,arg13,arg14,arg15,arg16);
	  }
	  public Text evaluate(String arg1,String arg2,String arg3,String arg4,String arg5,String arg6,String arg7,String arg8,String arg9,String arg10,String arg11,String arg12,String arg13,String arg14,String arg15,String arg16,String arg17){
		  return compute(arg1,arg2,arg3,arg4,arg5,arg6,arg7,arg8,arg9,arg10,arg11,arg12,arg13,arg14,arg15,arg16,arg17);
	  }
	  public Text evaluate(String arg1,String arg2,String arg3,String arg4,String arg5,String arg6,String arg7,String arg8,String arg9,String arg10,String arg11,String arg12,String arg13,String arg14,String arg15,String arg16,String arg17,String arg18){
		  return compute(arg1,arg2,arg3,arg4,arg5,arg6,arg7,arg8,arg9,arg10,arg11,arg12,arg13,arg14,arg15,arg16,arg17,arg18);
	  }
	  public Text evaluate(String arg1,String arg2,String arg3,String arg4,String arg5,String arg6,String arg7,String arg8,String arg9,String arg10,String arg11,String arg12,String arg13,String arg14,String arg15,String arg16,String arg17,String arg18,String arg19){
		  return compute(arg1,arg2,arg3,arg4,arg5,arg6,arg7,arg8,arg9,arg10,arg11,arg12,arg13,arg14,arg15,arg16,arg17,arg18,arg19);
	  }
	  public Text evaluate(String arg1,String arg2,String arg3,String arg4,String arg5,String arg6,String arg7,String arg8,String arg9,String arg10,String arg11,String arg12,String arg13,String arg14,String arg15,String arg16,String arg17,String arg18,String arg19,String arg20){
		  return compute(arg1,arg2,arg3,arg4,arg5,arg6,arg7,arg8,arg9,arg10,arg11,arg12,arg13,arg14,arg15,arg16,arg17,arg18,arg19,arg20);
	  }
	  public Text evaluate(String arg1,String arg2,String arg3,String arg4,String arg5,String arg6,String arg7,String arg8,String arg9,String arg10,String arg11,String arg12,String arg13,String arg14,String arg15,String arg16,String arg17,String arg18,String arg19,String arg20,String arg21){
		  return compute(arg1,arg2,arg3,arg4,arg5,arg6,arg7,arg8,arg9,arg10,arg11,arg12,arg13,arg14,arg15,arg16,arg17,arg18,arg19,arg20,arg21);
	  }
	  public Text compute(String...args){
		  try{
			  double total = 0.0;
			  int n = 0;
			  for(int i=0;i<args.length;i++){
				  String  arg = args[i];
				  try{
					  Double a = Double.parseDouble(arg);
					  total +=a;
					  if(a-0.0==0.0){
					  }else{
						  n++;
					  }
				  }catch(Exception e){
					  continue;
				  }
			  }
			  if(n!=0){
				  double avg_value = total/n;
				  result.set(avg_value+"");
				  return result; 
			  }else{
				  result.set("0");
				  return result; 
			  }
		  }catch(Exception e){
		       result.set("err");
		       return result;
		  }
	  }

		 
 
	 
	  public static void main(String[] args){
		  UDFAvg test = new UDFAvg();
          System.out.println(test.evaluate("0","2","3","1"));   
          
          	  
	  
	  }
}
