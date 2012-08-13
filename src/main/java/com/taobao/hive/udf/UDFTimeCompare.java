package com.taobao.hive.udf;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.Text;
/**
 *  
 *  
 * CREATE TEMPORARY FUNCTION timeCompare  AS 'com.taobao.hive.udf.UDFTimeCompare';
 *  
 */
public class UDFTimeCompare extends UDF{

	  Text result = new Text();
	  public UDFTimeCompare() {
		  
	  }

	  //date1,date2是yyyyMMddhhmmss的形式   diff单位是秒
	  public Text evaluate(String date1,String date2,int diff){
		  try{
			  String time1 = getTime(date1,0);
			  String time2 = getTime(date2,diff);

			  if(Long.valueOf(time1)>Long.valueOf(time2)){
			       result.set("1");
			       return result;
			  }else if(Long.valueOf(time1)<Long.valueOf(time2)){
			       result.set("-1");
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
	  
	  
	  public Text evaluate(String date1,String date2){
		  try{
				String a = getTime(date1,0);
				String b = getTime(date2,0);
		        Double c = Double.parseDouble(a)-Double.parseDouble(b);
		        int s = (int)(c/1000);
			    result.set(s+"");
			    return result;
		  }catch(Exception e){
		       result.set("err");
		       return result; 
		  }
	  }
	  
	  
	  
		
	  //  n的单位是秒   getTime()得到的是毫秒
		public static String getTime(String strBeginDate,int n){
	        DateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
	        
	        Date d = new Date();
			try {
				d = format.parse(strBeginDate);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	        if (d == null) {
	            return null;
	        }

	        return String.valueOf(d.getTime()+n*1000);
	        
		}
		
		
		
		
 
 
		 
	  public static void main(String[] args){
		  UDFTimeCompare test = new UDFTimeCompare();
		  System.out.println(test.evaluate("20100404123454", "20100404123444", 30));
		  System.out.println(test.evaluate(null, "20100404123444", 30));
		  System.out.println(test.evaluate("20100404123454", "20100404123444"));
	  
	  }
	   
	  
	  
}
