package com.taobao.hive.udf;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.Text;
/**
 *  
 *  
 * CREATE TEMPORARY FUNCTION getweekday  AS 'com.taobao.hive.udf.UDFGetWeekDay';
 *  
 *  
 * @author youliang
 *
 */
public class UDFGetWeekDay extends UDF{

	  Text result = new Text();
	  public UDFGetWeekDay() {
	  }

	  //传进去一个日期，得到是周几
	  public Text evaluate(String today) {
		try{
			  if (today == null) {
			     return null;
			  }

			  if(today.endsWith(".0")){
				  today = today.substring(0,today.length()-2);
			  }
			  
			  if(today.indexOf("-")>=0){
				  today = today.substring(0,10);
				  today = today.replaceAll("-","");
			  }else{
				  today = today.substring(0,8);
			  }

				String riqi = today;
		        Calendar ca = Calendar.getInstance();
		        Date dtBegin = new Date();
				try {
					dtBegin = new SimpleDateFormat("yyyyMMdd").parse(riqi);
				} catch (ParseException e1) {
					e1.printStackTrace();
				}
		        ca.setTime(dtBegin);
				  final String dayNames[] = { "7", "1", "2", "3", "4", "5","6" };
		 
				  int dayOfWeek = ca.get(Calendar.DAY_OF_WEEK)-1;
				  String zhouji = dayNames[dayOfWeek];
		    
		      result.set(zhouji);
		      return result;
		}catch(Exception e){
			return null;
		}
	  }
	  
	  public static void main(String args[]){
		  UDFGetWeekDay test = new UDFGetWeekDay();
		  System.out.println(test.evaluate("20090111"));
		  System.out.println(test.evaluate("2010-01-11"));
		  System.out.println(test.evaluate("2010-01-11 aa"));
		  
		  
	  }
}
