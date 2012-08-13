package com.taobao.hive.udf;


import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.Text;
/**
 *  
 *  
 * CREATE TEMPORARY FUNCTION udf_least  AS 'com.taobao.hive.udf.UDFLeast';
 *  
 * 
 * @author youliang   
 *  
 *  
 *     
 *     
 */

 
public class UDFLeast extends UDF{

	  Text result = new Text();
	  public UDFLeast() {
		  
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
			  double least = Double.parseDouble(args[0]);
			  double yuansu = 0;
			  for(int i=0;i<args.length;i++){
				  try{
					  yuansu = Double.parseDouble(args[i]);
				  }catch(Exception e){
					  continue;
				  }
				  if(yuansu<least){
					  least = yuansu;
				  }
			  }
			  result.set(least+"");
			  return result;
		  }catch(Exception e){
		       result.set("err");
		       return result;
		  }
	  }

		 
 
	 
	  public static void main(String[] args){
		  UDFLeast test = new UDFLeast();
          System.out.println(test.evaluate("0","-27","-3","splitby_,"));   
          System.out.println(test.evaluate("aa","bb"));
          System.out.println(test.evaluate("aa","bb","cc",null,"dd","splitby_,"));
          System.out.println(test.evaluate("null","bb",":",null,"dd"));
          System.out.println(test.evaluate(null));   
          	  
	  
	  }
}
