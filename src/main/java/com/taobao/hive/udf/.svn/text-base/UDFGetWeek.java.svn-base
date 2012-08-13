package com.taobao.hive.udf;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.Text;
/**
 *  
 *  
 * CREATE TEMPORARY FUNCTION getweek  AS 'com.taobao.hive.udf.UDFGetWeek';
 *  
 *  
 * @author youliang
 *
 */
public class UDFGetWeek extends UDF{

	  Text result = new Text();
	  public UDFGetWeek() {
	  }

	  //传进去一个日期，得到是第几周     礼拜一作为一周的第一天
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

			  SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
			  Date date = format.parse(today);
			  Calendar calendar = Calendar.getInstance();
			  calendar.setFirstDayOfWeek(Calendar.MONDAY);
			  calendar.setTime(date);
			  int week_number = calendar.get(Calendar.WEEK_OF_YEAR);
		    
		      result.set(week_number+"");
		      return result;
		}catch(Exception e){
			return null;
		}
	  }
	  
	  public static void main(String args[]){
		  UDFGetWeek test = new UDFGetWeek();
		  System.out.println(test.evaluate("20090111"));
		  System.out.println(test.evaluate("2010-01-11"));
		  System.out.println(test.evaluate("2010-01-11 aa"));
		  
		  
	  }
}
