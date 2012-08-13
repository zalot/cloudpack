/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.taobao.hive.udf;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.Text;
/**
 * 
 * �÷�:
 * CREATE TEMPORARY FUNCTION date_format  AS 'com.taobao.hive.udf.UDFDateFormat';
 * 
 *  
 * 
 *
 */
public class UDFDateFormat extends UDF {

  private SimpleDateFormat internalFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  
  private SimpleDateFormat lastDateFormater = null; 
  
  private Text lastDateFormatStr = null;   
  
  private SimpleDateFormat sdfy = null; 
  
  private SimpleDateFormat  sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss 'CST' yyyy", Locale.US);
  
  private SimpleDateFormat lastDateFormater2 = null; 
  
  private String lastDateFormatStr1;
  
  private String lastDateFormatStr2;  
  
  Text t = new Text();
  public UDFDateFormat() {
  }

  public Text evaluate(Text date, Text datepattern)  {
    if (date == null || datepattern == null) {
      return null;
    }
    
    
    try{
  	  String year = date.toString().substring(0,2);
	  if(!"18".equals(year)&&!"19".equals(year)&&!"20".equals(year)&&!"21".equals(year)){
		  return null;
	  }
    }catch(Exception e){
    	return null;
    }
 
    if (lastDateFormater == null) {
    	lastDateFormater = new SimpleDateFormat(datepattern.toString());
    	lastDateFormatStr = datepattern;
    }
    
    if (lastDateFormater != null && !datepattern.equals(lastDateFormatStr)) {
    	lastDateFormater = new SimpleDateFormat(datepattern.toString());
    	lastDateFormatStr = datepattern;
    }
    
    try {
	      Date date1 = internalFormatter.parse(date.toString());
	      t.set(lastDateFormater.format(date1));
    } catch (Exception e) {
        Date d = new Date();
		try {
			d = lastDateFormater.parse(date.toString());
			t.set(lastDateFormater.format(d));
		} catch (ParseException ee) {
			try{
				String date_yuanlai = date.toString();
				String year = date_yuanlai.substring(0,4);
				String month = date_yuanlai.substring(4,6);
				String day = date_yuanlai.substring(6,8);
				String date_now = year+"-"+month+"-"+day;
				d = lastDateFormater.parse(date_now);
				t.set(lastDateFormater.format(d));
			}catch(Exception eee){
				return null;
			}
		}
        return t;
    }

    return t;
  }
  
  
  public Text evaluate(String date,String prePattern,String nowPattern) throws ParseException  {
	  try{
		  if(prePattern.equals("english")){//CSTʱ��ת��Ϊ��׼��ʽ
			  if (sdfy == null) {
				sdfy = new SimpleDateFormat(nowPattern);  
			  }
		      String result = sdfy.format(sdf.parse(date));
			  t.set(result);
			  return t;
		  }else{
			  String year = date.substring(0,2);
			  if(!"18".equals(year)&&!"19".equals(year)&&!"20".equals(year)&&!"21".equals(year)){
				  return null;
			  }
			  
			  if (lastDateFormater  == null) {
			    	lastDateFormater   = new SimpleDateFormat(prePattern);		
			    	lastDateFormatStr1 = prePattern;
			  }
			  if (lastDateFormater != null && !prePattern.equals(lastDateFormatStr1)) {
			    	lastDateFormater = new SimpleDateFormat(prePattern);
			    	lastDateFormatStr1 = prePattern;
			    }
			  if (lastDateFormater2  == null) {
			    	lastDateFormater2   = new SimpleDateFormat(nowPattern);		
			    	lastDateFormatStr2 = nowPattern;
			  }
			  if (lastDateFormater2 != null && !nowPattern.equals(lastDateFormatStr2)) {
			    	lastDateFormater2 = new SimpleDateFormat(nowPattern);
			    	lastDateFormatStr2 = nowPattern;
			    }
			  String result = lastDateFormater2.format(lastDateFormater.parse(date)).toString();
		      
			  t.set(result);
		      return t;
		  } 
	  }catch(Exception e){
	       t.set("");
	       return t;
	  }
  }
  
 
  
  public static void main(String[] args) throws Exception {
    UDFDateFormat df = new UDFDateFormat();
//    System.out.println(df.evaluate("0000-00-00","yyyy-MM-dd","yyyyMMdd"));
//    System.out.println(df.evaluate(new Text("2012-06-04 22:23:24"),new Text("yyyyMMdd")));
    System.out.println(df.evaluate("2012-06-04","yyyy-MM-dd","yyyyMMdd"));
    System.out.println(df.evaluate("20120604","yyyyMMdd","yyyy-MM-dd"));
    
//  System.out.println(df.evaluate(new Text("2012-06-04 22:23:24"),new Text("yyyyMMdd")));
//  System.out.println(df.evaluate(new Text("2012-06-04 22:23:24"),new Text("yyyy-MM-dd")));
//  System.out.println(df.evaluate(new Text("2012-06-04 22:23:24"),new Text("yyyy-MM-dd")));
//  System.out.println(df.evaluate(new Text("2012-06-04 22:23:24"),new Text("yyyyMMdd")));
    
    
    /*  System.out.println(df.evaluate(new Text("2009-10-04 22:23:00"), new Text("yyyy-MM")));
    System.out.println(df.evaluate(new Text("2010-01-01 22:23:00"),new Text("yyyy-MM-dd")));

    System.out.println(df.evaluate(new Text("20100101 22:23:00"),new Text("yyyyMMdd")));
    System.out.println(df.evaluate(new Text("2010-01-01 22:23:00"),new Text("yyyyMMdd")));

    System.out.println(df.evaluate(new Text("2010-01-01 22:23:00"),new Text("yyyy-MM-dd")));
    System.out.println(df.evaluate("Sat Aug 07 03:16:47 CST 2010", "english","yyyy-MM-dd hh:mm:ss"));
  
    System.out.println(df.evaluate(new Text("20100101"),new Text("yyyy-MM-dd")));
    System.out.println(df.evaluate(new Text("20100101 22:23:00"),new Text("yyyy-MM-dd")));
    System.out.println(df.evaluate(new Text("20100101 22:23:00"),new Text("yyyy.MM")));
    System.out.println(df.evaluate(new Text("20100101 22:23:00"),new Text("yyyy-MM")));*/
    //   System.out.println(df.evaluate(new Text("20100101 22:23:00"),new Text("yyyyMM")));������BUG
  }
}