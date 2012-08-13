package com.taobao.hive.udf;


import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.Text;
/**
 *  
 *  
 * CREATE TEMPORARY FUNCTION udfstring  AS 'com.taobao.hive.udf.UDFString';
 *  
 * 实现与java的string类一样的功能.
 * @author youliang   
 *  
 *  
 *     
 *     
 */

 
public class UDFString extends UDF{

	  Text result = new Text();
	  public UDFString() {
		  
	  } 

	  
	  public Text evaluate(String methodName,String value){
		  return compute(methodName,value);
	  }
	  
	  public Text evaluate(String methodName,String value,String arg1){
		  return compute(methodName,value,arg1);
	  }
	  
	  
	  public Text evaluate(String methodName,String value,String arg1,String arg2){
		  return compute(methodName,value,arg1,arg2);
	  }
	  
	 
	  public Text compute(String methodName,String value,String...args){
		  
		  try{
			   methodName = methodName.toLowerCase();
			   if(methodName.equals("tolowercase")){
				    value = value.toLowerCase();
				    result.set(value);
				    return result;
			   }
			   if(methodName.equals("touppercase")){
				    value = value.toUpperCase();
				    result.set(value);
				    return result;
			   }
			   if(methodName.equals("length")){
				   if(value==null){
					    result.set("0");
					    return result;  
				   }
				    value = String.valueOf(value.length());
				    result.set(value);
				    return result;
			   }
			   if(methodName.equals("trim")){
				    value = value.trim();
				    result.set(value);
				    return result;
			   }
			   if(methodName.equals("indexof")){
					    int i = value.indexOf(args[0]);
				        result.set(String.valueOf(i));
				        return result;
			   }
			   if(methodName.equals("split_count")){
				    String[] strList = value.split(args[0]);
				    int i = strList.length;
			        result.set(String.valueOf(i));
			        return result;
		       }
			   if(methodName.equals("not_null")){
				    if(value==null||"".equals(value.trim())){
				        result.set(String.valueOf(args[0]));
				        return result;
				    }
			        result.set(value.trim());
			        return result;
		       }
			   if(methodName.equals("lastindexof")){ 
					    int i = value.lastIndexOf(args[0]);
				        result.set(String.valueOf(i));
				        return result; 
			   }
			   if(methodName.equals("endswith")){
					    boolean i = value.endsWith(args[0]);
				        result.set(String.valueOf(i));
				        return result;  
			   }
			   if(methodName.equals("equals")){
					    boolean i = value.equals(args[0]);
				        result.set(String.valueOf(i));
				        return result;
			   }
			   if(methodName.equals("startswith")){
					    boolean i = value.startsWith(args[0]);
				        result.set(String.valueOf(i));
				        return result;
			   }
			   if(methodName.equals("replaceall")){ 
				    value = value.replaceAll(args[0],args[1]);
			        result.set(value);
			        return result;
			   }
			   if(methodName.equals("replacefirst")){
				    value = value.replaceFirst(args[0],args[1]);
			        result.set(value);
			        return result;
			   }
			   if(methodName.equals("replace")){
				    value = value.replace(args[0],args[1]);
			        result.set(value);
			        return result;
			   }
			   if(methodName.equals("substring")){
				    int last = Integer.parseInt(args[1]);
				    if(String.valueOf(last).equals("-1")){
				    	last = value.length();
				    }
/*				    if(last > value.length()){
				    	last = value.length();
				    }*/
				    value = value.substring(Integer.parseInt(args[0]),last);
			        result.set(value);
			        return result;
			   }
			   if(methodName.equals("between")){
				   int index1 = value.indexOf(args[0]);
				   if(index1<0){
					   return null;
				   }
				   
				   if(!args[1].equals("end")){
					   value = value.substring(index1+args[0].length());
					   int index2 = value.indexOf(args[1]);
					   if(index2>=0){
						   value = value.substring(0,index2);
					       result.set(value);
					       return result;
					   }
				       result.set(value);
				       return result;
				   }else{
					   value = value.substring(index1+args[0].length());
				       result.set(value);
				       return result;
				   }

			   }
			   if(methodName.equals("cutby")){
				   int index1 = value.indexOf(args[0]);
				   if(index1>=0){
					   value = value.substring(0,index1);
				       result.set(value);
				       return result;
				   }
			       result.set(value);
			       return result;
			   }
			   if(methodName.equals("getnumber")){
				   String return_result = "0";
				   try{
					   return_result = Double.parseDouble(value)+"";
				   }catch(Exception e){
					   return_result = "0";
				   }
				   result.set(return_result);
				   return result;
			   }
			   value = "err";
			   result.set(value);
			   return result;
		  }catch(Exception e){
		       result.set("err");
		       return result;
		  }
	  }
		   


			    
		   
		    
		  
		  
		  
 
	  
		
	  
  //      http://www.taobao.com/aaaaa/ghllll=333.html
	 
	  public static void main(String[] args){
		  UDFString test = new UDFString();
/*		  System.out.println(test.evaluate("tolowercase","aAAAkkddEEE"));
		  System.out.println(test.evaluate("touppercase","aAAAkkddEEE"));
		  System.out.println(test.evaluate("length","aAAAkkddEEE"));
		  System.out.println(test.evaluate("trim","   aAAAk kddEEE    "));
		  System.out.println(test.evaluate("indexof","aAAAkkdgdEEE","g"));//索引从0开始
		  System.out.println(test.evaluate("lastindexof","aAAAkkdgdEEE","k")); 
		  System.out.println(test.evaluate("endswith","aAAAkkdgdEEE","EE"));
		  System.out.println(test.evaluate("equals","aAAAkkdgdEEE","aAAAkkdagdEEE"));
		  System.out.println(test.evaluate("startswith","aAAAkkdgdEEE","aAAAk"));
		  System.out.println(test.evaluate("replaceall","aAAAkkdgdEEE","A","a"));
		  System.out.println(test.evaluate("replacefirst","aAAAkkdgdEEE","A","a"));
		  System.out.println(test.evaluate("replace","aAAAkkdgdEEE","A","a"));
		  System.out.println(test.evaluate("substring","aAAAkkdgdEEE","0","3"));*/
		  //System.out.println(test.evaluate("between","http://store.taobao.com/shop/view_shop-581c28361f58812188bbbcd8f5585aed.htm?nekot=g,wcxlzl6tzi1235135718775","view_shop-",".htm"));
		  //System.out.println(test.evaluate("between","http://store.taobao.com/shop/view_shop-581c28361f58812188bbbcd8f5585aed.htm?nekot=g,wcxlzl6tzi1235135718775","view_shop-","end"));
		  //System.out.println(test.evaluate("cutby","http://store.taobao.com/shop/view_shop-581c28361f58812188bbbcd8f5585aed.htm?nekot=g,wcxlzl6tzi1235135718775","view_shop-"));
		  //System.out.println(test.evaluate("not_null","1","9"));
		  //System.out.println(test.evaluate("getnumber","1"));
		  //System.out.println(test.evaluate("getnumber","13.5"));
		  //System.out.println(test.evaluate("getnumber","1.8"));
		  //System.out.println(test.evaluate("getnumber","1.0"));
		  //System.out.println(test.evaluate("getnumber","1a"));
		  //select udfstring("between","http://jie.taobao.com/street-37--1-grid-1-.htm","http://jie.taobao.com/street-","-") from dual;
		  System.out.println(test.evaluate("between","http://jie.taobao.com/street-37--1-grid-1-.htm","a","|"));
		  //System.out.println(test.evaluate("between","http://store.taobao.com/shop/view_shop-581c28361f58812188bbbcd8f5585aed.htm?nekot=g,wcxlzl6tzi1235135718775","view_shop-",".htm"));
		  //System.out.println(test.evaluate("between","http://store.taobao.com/shop/view_shop-581c28361f58812188bbbcd8f5585aed.htm?nekot=g,wcxlzl6tzi1235135718775","view_shop-","end"));
		  //System.out.println(test.evaluate("substring","http://store.taobao.com/shop/view_shop-581c28361f58812188bbbcd8f5585aed.htm?nekot=g,wcxlzl6tzi1235135718775","1","-1"));
		  
	  }
	  
	  
	  
}
