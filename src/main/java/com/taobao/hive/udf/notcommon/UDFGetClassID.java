package com.taobao.hive.udf.notcommon;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.hadoop.hive.ql.exec.UDF;
/**
 * 
 * type=1 classId
 * type=2 child_classId
 *
 */
public class UDFGetClassID extends UDF{
	
	public UDFGetClassID(){};
	
	private Map<String,String> mapUrl = new ConcurrentHashMap<String,String>();
	private static final String FILE_AP_URL = "./ap_url.csv";
//	private static final String FILE_AP_URL = "D:\\ap_url.csv";
	private boolean isLoaded = false;
	
	private void loadMapURL() throws Exception{
		mapUrl.clear();
		BufferedReader reader = null;
		reader = new BufferedReader(new InputStreamReader(new FileInputStream(FILE_AP_URL)));
		String strLine = null;
		String[] strList;
		while ((strLine = reader.readLine()) != null) {
			strList = strLine.split("\"");
			if (strList.length < 6)
				continue;
			mapUrl.put(strList[5], strList[1] + "\"" + strList[2]);
		}
		isLoaded = true;
		reader.close();
	}
	
	public String evaluate(String refer,String url,String type){
		if (!isLoaded) { // load file if not loaded
			try {
				loadMapURL();
			}catch (Exception e){
				e.printStackTrace();
			}
		}
	    String classid = "0";
	    String child_classid = "0";

	    String tmp = new String();
	    try{
	      int start = 0,end = 0;
	      String pureUrl = new String(url);
	      int nIndex = pureUrl.indexOf("?");
	      if(nIndex >= 0)
	        pureUrl= pureUrl.substring(0,nIndex);
	      if((tmp = mapUrl.get(pureUrl)) != null){
	        classid = tmp.split("\"")[1];
	        child_classid = tmp.split("\"")[0];
	      }
	      else if(url.startsWith("https://taobao.alipay.com/trade/trade_payment.htm")
	            ||url.startsWith("http://strade1.taobao.com/auction/buy_item.jhtml")){
	        classid = "1";
	        child_classid = "0";
	      }
	      else if(url.startsWith("http://list.taobao.com")){
	           if(refer.indexOf("category=search_") >= 0){
	              classid = "3";
		      start = refer.indexOf("category=search_");
		      start += 16;
		      end = refer.indexOf("&",start);
		      if(end == -1)
			end = refer.length();
		      child_classid = refer.substring(start,end);	      
	        }
	      }
	      else if(url.startsWith("http://list1.taobao.com")){
	           if(url.indexOf("cat=") > 0)
	           {
	              classid = "3";
	              start = url.indexOf("cat=");
	              start += 4;
	              end = url.indexOf("&",start);
	              if(end == -1)
	                 end = url.length();
	              if(start != end)
	              {
	                 child_classid = url.substring(start,end);
	                 if(child_classid.indexOf("-") > 0)
	                 {
	                    String[] t = child_classid.split("-");
	                    child_classid = t[t.length-1];
	                 }
	                 try{
	                    Integer.valueOf(child_classid);
	                 }
	                 catch(Exception e)
	                 {child_classid = "0";}
	              }
	           }
	      }
	      else if(url.startsWith("http://search1.taobao.com")){
		   if(refer.indexOf("category=search_") >= 0){
	              classid = "4";
	              start = refer.indexOf("category=search_");
	              start += 16;
	              end = refer.indexOf("&",start);         
	              if(end == -1)
	                end = refer.length();
	              child_classid = refer.substring(start,end);
	        }
	      }
	      else if(url.startsWith("http://search.taobao.com") || url.startsWith("http://s.taobao.com")){
		   if(url.indexOf("cat=") > 0)
		   {
		      classid = "4";
		      start = url.indexOf("cat=");
	              start += 4;
	              end = url.indexOf("&",start);
	              if(end == -1)
	                 end = url.length();
	              if(start != end)
	              {
	                 child_classid = url.substring(start,end);
			 if(child_classid.indexOf("-") > 0)
			 {
			    String[] t = child_classid.split("-");
			    child_classid = t[t.length-1];
		 	 }
			 try{
			    Integer.valueOf(child_classid);
			 }
		         catch(Exception e)
			 {child_classid = "0";}
		      }
		   }
	      }
	      else if(url.startsWith("http://member1.taobao.com/member/user-profile-")){
	        classid = "5";
	        child_classid = "1";
	      }
	      else if(url.startsWith("http://my.taobao.com/mytaobao/user-rate-")){
	        classid = "5";
	        child_classid = "2";
	      }
	      else if(url.startsWith("http://forum.taobao.com")){
		if(refer.indexOf("category=forum_") >= 0){
	              classid = "6";
	              start = refer.indexOf("category=forum_");
	              start += 15;
	              end = refer.indexOf("_",start);
	              if(end == -1)
	                end = refer.length();
	              child_classid = refer.substring(start,end);
		      int a = Integer.valueOf(child_classid) * 10000;
		      child_classid = String.valueOf(a);
	        }
	      }
	      else if(url.startsWith("http://info.taobao.com")){
		   if(refer.indexOf("category=forum_") >= 0){
	              classid = "6";
	              start = refer.indexOf("category=forum_");
	              start += 15;
	              end = refer.indexOf("_",start);
	              if(end == -1)
	                end = refer.length();
	              child_classid = refer.substring(start,end);
	              int a = Integer.valueOf(child_classid) * 10000;
	              child_classid = String.valueOf(a);
	        }
	      }
	      else if(url.startsWith("http://item.taobao.com")){
		  if(refer.indexOf("category=item_") >= 0){
	              classid = "9";
	              start = refer.indexOf("category=item_");
	              start += 14;
	              end = refer.indexOf("&",start);
	              if(end == -1)
	                end = refer.length();
	              child_classid = refer.substring(start,end);
	        }
	      }
	      else if(url.startsWith("http://archive.taobao.com")){
		  if(refer.indexOf("category=item_") >= 0){
	              classid = "9";
	              start = refer.indexOf("category=item_");
	              start += 14;
	              end = refer.indexOf("&",start);
	              if(end == -1)
	                end = refer.length();
	              child_classid = refer.substring(start,end);
	        }
	      }
	      else if(url.matches("http://shop[0-9]*.taobao.com.*")){
	        classid = "5";
	        child_classid = "0";
	      }
	      else{
	        classid = "0";
	        child_classid = "0";
	      }
	    }
	    catch(Exception e){
	        e.printStackTrace();
	        return "-1";
	    }
	    if ("1".equals(type)) return classid;
	    else if ("2".equals(type)) return child_classid;
	    else return "-1";
	  }

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		UDFGetClassID class_id = new UDFGetClassID();
		String refer = "/1.gif?cache=6575569&pre=&scr=1280x1024&category=item_50010850&userid=&tid=de6630b13e47231a28f48335b4804848&channel=112&at_isb=0&at_autype=5_57112488&ad_id=";
		String url ="http://item.taobao.com/item.htm?id=9445666625" ;
		System.out.println(class_id.evaluate(refer, url, "1"));
	}
}

