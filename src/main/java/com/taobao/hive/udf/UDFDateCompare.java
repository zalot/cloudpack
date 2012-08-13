package com.taobao.hive.udf;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.Text;
/**
 *  
 *  
 * CREATE TEMPORARY FUNCTION dateCompare  AS 'com.taobao.hive.udf.UDFDateCompare';
 *  
 * 这个类单独打成jar
 * @author youliang   2010-04-16
 * 原来的判断条件是 where gmt_create>=  sysdate-6    
 * 现在用     where dateCompare(gmt_create,sysdate,-6)>=0  
 *    gmt_create <  sysdate
 *    dateCompare(gmt_create,sysdate,0)<0 
 */
public class UDFDateCompare extends UDF{
      public static UDFDateFormat dateformat = new UDFDateFormat();
	  Text result = new Text();
	  public UDFDateCompare() {
		  
	  }

	  //inputDate  要yyyyMMdd的格式
	  public Text evaluate(String databaseDate,String inputDate,int days){
		  try{
			  if(databaseDate.endsWith(".0")){
				  databaseDate = databaseDate.substring(0,databaseDate.length()-2);
			  }
			  
			  if(databaseDate.indexOf("-")>=0){
				  databaseDate = databaseDate.substring(0,10);
				  databaseDate = databaseDate.replaceAll("-","");
			  }else{
				  databaseDate = databaseDate.substring(0,8);
			  }

			  
			  String date1 = getDay(databaseDate,0);
			  String date2 = getDay(inputDate,days);

			  if(Integer.parseInt(date1)>Integer.parseInt(date2)){
			       result.set("1");
			       return result;
			  }else if(Integer.parseInt(date1)<Integer.parseInt(date2)){
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
	  
	  //求日期差
	  //比较2个日期相差多少天.  参数为:日期1以及日期1的格式.  日期2以及日期2的格式 
	  public Text evaluate(String inputdate1,String format1,String inputdate2,String format2,String flag){
		  try{
			   String date1 = dateformat.evaluate(inputdate1, format1, "yyyyMMddHHmmss").toString();
			   String date2 = dateformat.evaluate(inputdate2, format2, "yyyyMMddHHmmss").toString();
			  
			  
		        DateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
				Date d1 = format.parse(date1);
				Date d2 = format.parse(date2);
			    double haomiao = d1.getTime() - d2.getTime();
			    
			    int tian = (int)haomiao / 1000 / 60 / 60 / 24;
			    String flag_xiaoxie = flag.toLowerCase();
			    if("ss".equals(flag_xiaoxie)){
			    	tian = (int)haomiao / 1000;
			    }
			    if("mm".equals(flag_xiaoxie)){
			    	tian = (int)haomiao / 1000 / 60;
			    }
			    if("hh".equals(flag_xiaoxie)){
			    	tian = (int)haomiao / 1000 / 60 / 60;
			    }
			    if("day".equals(flag_xiaoxie)){
			    	tian = (int)haomiao / 1000 / 60 / 60 / 24;
			    }
			    
			    result.set(tian+"");
			    return result;
		  }catch(Exception e){
		       result.set("err");
		       return result; 
		  }
	  }
	  
		//strBeginDate 的格式 yyyyMMdd  
		public static String getDay(String strBeginDate,int n){
			 Date dtBegin = new Date();
			try {
				dtBegin = new SimpleDateFormat("yyyyMMdd").parse(strBeginDate);
			} catch (ParseException e1) {
				e1.printStackTrace();
			}
	  Calendar cld = Calendar.getInstance();
	  cld.setTime(dtBegin);
	  int day = cld.get(Calendar.DAY_OF_YEAR);
		 
		    String strYear = strBeginDate.substring(0,4);
	  String strInputPath = "";
	  String strDate = "";
	  try{
	     int nDays = 1;
	      
	    	 cld.setTime(dtBegin);
	         cld.set(Calendar.DAY_OF_YEAR, day + n);
	         Date dt = cld.getTime();
	         strDate = new SimpleDateFormat("yyyyMMdd").format(dt);
	         strYear = strDate.substring(0,4);
	          
	          
	         strInputPath = strDate;
	      
	  }catch(NumberFormatException e){
	 	 e.printStackTrace();
	  }
	    
	        return strInputPath;
		}
		
		
		 
	  public static void main(String[] args){
		  UDFDateCompare test = new UDFDateCompare();
/*		  System.out.println(test.evaluate("2010-02-03 12:33:45.0", "20100209", -6));
		  System.out.println(test.evaluate("2010-02-03 12:33:45", "20100209", -6));
		  System.out.println(test.evaluate("2010-02-03", "20100209", -6));
		  System.out.println(test.evaluate("20100203", "20100209", -6));
		  System.out.println(test.evaluate("20100203123345", "20100209", -6));
		  System.out.println(test.evaluate("201002031233", "20100209", -6));
		  System.out.println(test.evaluate("20100203", "20100209", -6));
		  System.out.println(test.evaluate("2010-02-03 12:33", "20100209", -6));
		  System.out.println(test.evaluate("2010-02-03 12:33", "2010-02-09", -6));*/
		  System.out.println(test.evaluate("20100208 16:43","yyyyMMdd HH:mm" ,"20100209 05:32:23","yyyyMMdd HH:mm:ss","mm"));
		  
	  }
	  
	  
	  
}
