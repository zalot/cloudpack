package com.taobao.hive.udf;



import java.io.IOException;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.Text;


/**
 *  
 * 
 * CREATE TEMPORARY FUNCTION get_group_value  AS 'com.taobao.hive.udf.UDFGetGroupValue';
 *  
 * 
 * @author youliang   
 * 2010-08-05
 *  
 *     
 *     
 */

public class UDFGetGroupValue extends UDF{
	  
	  Text result = new Text();
	  
	  public UDFGetGroupValue() throws IOException {
		 

	  } 


	  public Text evaluate(String str,String flag) throws IOException{
		  try{
			   //str  = "{'target': 'j_username','key': 'target'}{:'j_username', 'key': 'U'},erwrtwe{'target': 'j_username', 'key': 'a'},{'target': 'j_username', 'key': 'c'},{'target': 'j_username', 'key': 'key_code:229'}";  
			   //flag = "'target': 'j_username', 'key':";
			   String[] strList = str.split("}");
			   String returnValue = "";
			   for(int i=0;i<strList.length;i++){
				   String duan = strList[i];
				   //System.out.println("=="+duan);
				   int index = duan.indexOf(flag);
				   
				   if(index<0){
					   continue;
				   }
				   String value = duan.substring(index+flag.length()).trim();
				   value = value.replace("'","");
				   if(value.indexOf("key_code:229")<0){
					   returnValue += value + ",";
				   }
			   }
			   returnValue = returnValue.substring(0,returnValue.length()-1);
			   result.set(returnValue);
			   return result;
		  }catch(Exception e){
			  e.printStackTrace();
		  }
		   result.set("");
		   return result;
	  }  
	  
	  
 
		   
		
	 
	  public static void main(String[] args) throws IOException{
		  UDFGetGroupValue test = new UDFGetGroupValue();
		  System.out.println(test.evaluate("1","2"));
		  
	  }
	  
	  
	  
}
