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
 * CREATE TEMPORARY FUNCTION getDate  AS 'com.taobao.hive.udf.UDFGetDate';
 *  
 * ����൥�����jar
 * @author youliang   
 *  
 *  
 *     
 *     
 */

 
public class UDFGetDate extends UDF{

	  Text result = new Text();
	  public UDFGetDate() {
		  
	  } 
	  
	  
	  //���ָ������ ��N�������
	  public Text evaluate(String riqi,int days){
		  
		  try{
			  
		  	  String year = riqi.substring(0,2);
			  if(!"18".equals(year)&&!"19".equals(year)&&!"20".equals(year)&&!"21".equals(year)){
				  return null;
			  }

			  String date = "";
			  if(riqi.endsWith(".0")){
				  riqi = riqi.substring(0,riqi.length()-2);
			  }
			  
			  if(riqi.indexOf("-")>=0){
				  riqi = riqi.substring(0,10);
				  riqi = riqi.replaceAll("-","");
			  }else{
				  riqi = riqi.substring(0,8);
			  }
			  try{
				   date = getDay(riqi,days);
			  }catch(Exception e){
			       result.set("err");
			       return result; 
			  }
			    result.set(date);
			    return result;
		  }catch(Exception e){
		       result.set("err");
		       return result;
		  }
	  }
	 

	  //���ָ������Ϊ��׼�� ��N�� ��һ�죬���һ�죬�Լ�ָ�����������    19870900  ĩ��λ00�������׼
	  public Text evaluate(String riqi,int n,String flag){
		  
		  
		    try{
		    	  String year = riqi.substring(0,2);
		  	  if(!"18".equals(year)&&!"19".equals(year)&&!"20".equals(year)&&!"21".equals(year)){
		  		  return null;
		  	  }
		    }catch(Exception e){
		      	return null;
		    }
		  
		  
		  
		  try{
			  String date = "";
			  if(riqi.endsWith(".0")){
				  riqi = riqi.substring(0,riqi.length()-2);
			  }
			  
			  if(riqi.indexOf("-")>=0){
				  riqi = riqi.substring(0,10);
				  riqi = riqi.replaceAll("-","");
			  }else{
				  riqi = riqi.substring(0,8);
			  }
			  try{
				  
				  if("first".equals(flag)){
					  date = getMonthStartDate(riqi,n);
				  }else if("last".equals(flag)){
					  date = getMonthEndDate(riqi,n);
				  }else{
					  date = getMonthDate(riqi,n,flag);
				  }
			  }catch(Exception e){
			       result.set("err");
			       return result; 
			  }
			    result.set(date);
			    return result;
		  }catch(Exception e){
		       result.set("err");
		       return result;
		  }
	  }
	  
      //���ϵͳ��ǰʱ����n�µ����� ����yyyyMMddHHmmss
	  public Text evaluate(int n){
		    String date = getNmonthAgo(n);
		    result.set(date);
		    return result;
	  }
	  
	  
	    public static String getMonthDate(String riqi,int n,String flag) {
	        Calendar ca = Calendar.getInstance();
	        Date dtBegin = new Date();
			try {
				dtBegin = new SimpleDateFormat("yyyyMMdd").parse(riqi);
			} catch (ParseException e1) {
				e1.printStackTrace();
			}
			int day = Integer.parseInt(flag);
	        ca.setTime(dtBegin);
	        ca.add(Calendar.MONTH, n);
	        ca.set(Calendar.HOUR_OF_DAY, 0);
	        ca.set(Calendar.MINUTE, 0);
	        ca.set(Calendar.SECOND, 0);
	        ca.set(Calendar.DAY_OF_MONTH, day);

	        Date firstDate = ca.getTime();

	        return ymdFormat(firstDate);
	    }
	  
	    public static String getNmonthAgo(int n) {
	        Calendar ca = Calendar.getInstance();
	        Date dtBegin = new Date();
	        ca.setTime(dtBegin);
	        ca.add(Calendar.MONTH, n);
	        Date firstDate = ca.getTime();

	        return ymdhmsFormat(firstDate);
	    }

	  
		
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
		
		
	    /**
	     * �������һ������
	     * @return
	     */
	    public static String getCurrentMonthEndDate() {
	        Calendar ca = Calendar.getInstance();

	        ca.setTime(new Date());
	        ca.set(Calendar.HOUR_OF_DAY, 23);
	        ca.set(Calendar.MINUTE, 59);
	        ca.set(Calendar.SECOND, 59);
	        ca.set(Calendar.DAY_OF_MONTH, 1);
	        ca.add(Calendar.MONTH, 1);
	        ca.add(Calendar.DAY_OF_MONTH, -1);

	        Date lastDate = new Date(ca.getTime().getTime());

	        return ymdFormat(lastDate);
	    }

	    /**
	     * ���µ�һ������
	     * @return
	     */
	    public static String getCurrentMonthStartDate() {
	        Calendar ca = Calendar.getInstance();

	        ca.setTime(new Date());
	        ca.set(Calendar.HOUR_OF_DAY, 0);
	        ca.set(Calendar.MINUTE, 0);
	        ca.set(Calendar.SECOND, 0);
	        ca.set(Calendar.DAY_OF_MONTH, 1);

	        Date firstDate = ca.getTime();

	        return ymdFormat(firstDate);
	    }
	    
	    public static String getMonthStartDate(String riqi,int n) {
	        Calendar ca = Calendar.getInstance();
	        Date dtBegin = new Date();
			try {
				dtBegin = new SimpleDateFormat("yyyyMMdd").parse(riqi);
			} catch (ParseException e1) {
				e1.printStackTrace();
			}
	        ca.setTime(dtBegin);
	        ca.add(Calendar.MONTH, n);
	        ca.set(Calendar.HOUR_OF_DAY, 0);
	        ca.set(Calendar.MINUTE, 0);
	        ca.set(Calendar.SECOND, 0);
	        ca.set(Calendar.DAY_OF_MONTH, 1);

	        Date firstDate = ca.getTime();

	        return ymdFormat(firstDate);
	    }
	    
	    public static String getMonthEndDate(String riqi,int n) {
	        Calendar ca = Calendar.getInstance();
	        Date dtBegin = new Date();
			try {
				dtBegin = new SimpleDateFormat("yyyyMMdd").parse(riqi);
			} catch (ParseException e1) {
				e1.printStackTrace();
			}
	        ca.setTime(dtBegin);
	        ca.add(Calendar.MONTH, n);
	        ca.set(Calendar.HOUR_OF_DAY, 23);
	        ca.set(Calendar.MINUTE, 59);
	        ca.set(Calendar.SECOND, 59);
	        ca.set(Calendar.DAY_OF_MONTH, 1);
	        ca.add(Calendar.MONTH, 1);
	        ca.add(Calendar.DAY_OF_MONTH, -1);

	        Date lastDate = new Date(ca.getTime().getTime());

	        return ymdFormat(lastDate);
	    }
	    
	    /**
	     * ����ת���ַ�
	     * ��ʽΪyyyy-MM-dd
	     * @param date
	     * @return
	     */
	    public static final String ymdFormat(Date date) {
	        if (date == null) {
	            return "";
	        }
	        
	        DateFormat ymdFormat = new SimpleDateFormat("yyyy-MM-dd");
	        return ymdFormat.format(date);
	    }
	    
	    public static final String ymdhmsFormat(Date date) {
	        if (date == null) {
	            return "";
	        }
	        
	        DateFormat ymdFormat = new SimpleDateFormat("yyyyMMddHHmmss");
	        return ymdFormat.format(date);
	    }
	  public static void main(String[] args){
		  UDFGetDate test = new UDFGetDate();
		  
		  System.out.println(test.evaluate("19870900",1,"1"));
		 
	  }
	  
	  //���µļ�¼
	  //select * from �� where pt=..   
	  //and date_format(gmt_create,"ԭ���ĸ�ʽ","yyyyMMdd")>=udfstring("replace",getDate('$env.date',0,"1"),"-","")
      //and date_format(gmt_create,"ԭ���ĸ�ʽ","yyyyMMdd")<=udfstring("replace",getDate('$env.date',0,"last"),"-","")
		  
	    
}
