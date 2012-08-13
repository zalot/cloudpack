package com.taobao.hive.udf;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Pattern;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.Text;
import java.io.*;


/**
 *  
 * url规则整理通用UDF函数。url规则由PD维护。UDF函数通过读取HDFS文件,解析过滤日志
 * CREATE TEMPORARY FUNCTION get_url_record  AS 'com.taobao.hive.udf.UDFGetUrlRecord';
 * 本函数用于上线需求。   
 * 另：UDFGetTmpRecord 用于临时需求。传入的是规则ID
 * 需求地址：http://confluence.taobao.ali.com:8080/pages/viewpage.action?pageId=173441380
 * @author youliang   
 * 2010-08-05
 * 
 * 关于数据库与hive里正则区别的说明:
 * 数据库里的正则  http://www.taobao.com/   则包含这个字符串就满足    
 * 数据库里的正则  ^http://www.taobao.com/  则以这个字符串开头就满足   
 * 数据库里的正则  ^http://www.taobao.com/$ 则必须满足以什么开头,以什么结尾
 * hive中 url rlike '^http://bangpai\\.taobao\\.com/catalog/([\\d]+)-([\\d]+)\\.htm.*'  表示以某个字符串开头
 * hive中 url rlike '^http://bangpai\\.taobao\\.com/catalog/([\\d]+)-([\\d]+)\\.htm'    相当于url rlike '^http://bangpai\\.taobao\\.com/catalog/([\\d]+)-([\\d]+)\\.htm$'
 * 
 * add file  hdfs://hdpnn:9000/group/tbdev/youliang/test/rule_type.txt;
 * add file  hdfs://hdpnn:9000/group/tbdev/youliang/test/rule_url.txt;
 * add file  hdfs://hdpnn:9000/group/tbdev/youliang/test/rule_url_work.txt;
 * add file  hdfs://hdpnn:9000/group/tbdev/youliang/test/rule_url_property.txt;
 * 1. 文件名必须是rule_url_property  不是rule_url_properfy
 * 2. add file的文件是rule_url_property  前面没有日期  20100918rule_url_property
 */
//CREATE TEMPORARY FUNCTION getsql  AS 'com.taobao.hive.udf.UDFGetSql';
public class UDFGetSql extends UDF{
	  private static HashMap rules1 = new HashMap();
	  private static HashMap rules2 = new HashMap();
	  private static HashMap rules3 = new HashMap();
	  private static HashMap rules4 = new HashMap();
	  private static String hdfs1_flag = null;
	  private static String hdfs2_flag = null;
	  private static String hdfs3_flag = null;
	  private static String hdfs4_flag = null;
	  
	  private static final String FILE1 = "./rule_type.txt";
	  private static final String FILE2 = "./rule_url_work.txt";
	  private static final String FILE3 = "./rule_url.txt";
	  private static final String FILE4 = "./rule_url_property.txt";
	  
	  Text result = new Text();
	  
	  public UDFGetSql() throws IOException, ParseException {

	  } 

	  public UDFGetSql(String str) throws IOException, ParseException {
		  
		  
	  }

	  public UDFGetSql(String str1,String str2) throws IOException, ParseException {
		  
	  }
	  
	  
	  public static String getmessage(String rule_id,String rule_type){
      	  String line = (String)rules3.get(rule_id);
     	  String[] strList = line.split("\"",-1);
    	  String RULE_VALUE = strList[0];//规则正则表达式
    	  String PATH = strList[1];//0URL字段 1refer字段规则 2通用
    	  String STARTS = strList[2];//之前放入rules3的时候，就已经把格式处理成yyyyMMdd格式了
    	  String ENDS = strList[3];
    	  
           
    	  
    	  String message = "";
    	  String path_valid = "path is right";
          if(rule_id!=null&&rule_type.equals("url_id")){
        	  if("1".equals(PATH)){
        		  path_valid = "path is error";
        	  }
        	  message = "url_id:"+rule_id+" zhengze:"+RULE_VALUE+" begin_date:"+STARTS+" end_date:"+ENDS+" "+path_valid+"\n";
        	  
          }
          if(rule_id!=null&&rule_type.equals("refer_id")){

        	 if("0".equals(PATH)){
        		 path_valid = "path is error";
        	 }
        	 message = "refer_id:"+rule_id+" zhengze:"+RULE_VALUE+" begin_date:"+STARTS+" end_date:"+ENDS+" "+path_valid+"\n";
        	
          }
		  return message;
	  }
	  
	  public static String getmessage2(String url_id,String refer_id){
      	  String line = (String)rules3.get(url_id);
     	  String[] strList = line.split("\"",-1);
    	  String RULE_VALUE = strList[0];//规则正则表达式
    	  String PATH = strList[1];//0URL字段 1refer字段规则 2通用
    	  String STARTS = strList[2];//之前放入rules3的时候，就已经把格式处理成yyyyMMdd格式了
    	  String ENDS = strList[3];

    	  String message = "";
    	  String path_valid = "path is right";
          if(url_id!=null){
        	  if("1".equals(PATH)){
        		  path_valid = "path is error";
        	  }
        	  message = "url_id:"+url_id+" zhengze:"+RULE_VALUE+" begin_date:"+STARTS+" end_date:"+ENDS+" "+path_valid+"\n";
          }
          
          
      	  line = (String)rules3.get(refer_id);
     	  strList = line.split("\"",-1);
    	  RULE_VALUE = strList[0];//规则正则表达式
    	  PATH = strList[1];//0URL字段 1refer字段规则 2通用
    	  STARTS = strList[2];//之前放入rules3的时候，就已经把格式处理成yyyyMMdd格式了
    	  ENDS = strList[3];
    	  path_valid = "path is right";
          if(refer_id!=null){
        	 if("0".equals(PATH)){
        		 path_valid = "path is error";
        	 }
        	 message += "refer_id:"+refer_id+" zhengze:"+RULE_VALUE+" begin_date:"+STARTS+" end_date:"+ENDS+" "+path_valid+"\n";
        	  
          }
		  return message;
	  }
	  
	  public static String getsql(String rule_id,String rule_type){
      	  String line = (String)rules3.get(rule_id);
     	  String[] strList = line.split("\"",-1);
    	  String RULE_VALUE = strList[0];//规则正则表达式
    	  String PATH = strList[1];//0URL字段 1refer字段规则 2通用
    	  String STARTS = strList[2];//之前放入rules3的时候，就已经把格式处理成yyyyMMdd格式了
    	  String ENDS = strList[3];
    	  
    	  RULE_VALUE = RULE_VALUE.replace("\\","\\\\");
    	  if(RULE_VALUE.indexOf("^")<0){
    		  RULE_VALUE = ".*"+RULE_VALUE;
    	  }
    	  if(RULE_VALUE.indexOf("$")<0){
    		  RULE_VALUE = RULE_VALUE+".*";
    	  }
    	  RULE_VALUE = "'"+RULE_VALUE+"'";
    	  
    	  String work_sql = "";
          if(rule_id!=null&&rule_type.equals("url_id")){
        	  work_sql = " (url rlike "+RULE_VALUE+") or";

          }
          if(rule_id!=null&&rule_type.equals("refer_id")){

 	   	     String pre = "url_decode(getvaluefromrefer(refer,\"&\",\"pre=\"),\"1\",\"2\")";
        	 work_sql = " ("+pre+" rlike "+RULE_VALUE+") or";
        	  

          }
		  return work_sql;
	  }
	  
	  
	  public static String getsql2(String url_id,String refer_id){
      	  String line = (String)rules3.get(url_id);
     	  String[] strList = line.split("\"",-1);
    	  String RULE_VALUE = strList[0];//规则正则表达式
    	  String PATH = strList[1];//0URL字段 1refer字段规则 2通用
    	  String STARTS = strList[2];//之前放入rules3的时候，就已经把格式处理成yyyyMMdd格式了
    	  String ENDS = strList[3];
    	  
    	  RULE_VALUE = RULE_VALUE.replace("\\","\\\\");
    	  if(RULE_VALUE.indexOf("^")<0){
    		  RULE_VALUE = ".*"+RULE_VALUE;
    	  }
    	  if(RULE_VALUE.indexOf("$")<0){
    		  RULE_VALUE = RULE_VALUE+".*";
    	  }
    	  RULE_VALUE = "'"+RULE_VALUE+"'";
    	  
    	  String work_sql = "";
          if(url_id!=null){
        	  work_sql = " (url rlike "+RULE_VALUE+" and ";
          }
          
      	  line = (String)rules3.get(refer_id);
     	  strList = line.split("\"",-1);
    	  RULE_VALUE = strList[0];//规则正则表达式
    	  PATH = strList[1];//0URL字段 1refer字段规则 2通用
    	  STARTS = strList[2];//之前放入rules3的时候，就已经把格式处理成yyyyMMdd格式了
    	  ENDS = strList[3];
    	  
    	  RULE_VALUE = RULE_VALUE.replace("\\","\\\\");
    	  if(RULE_VALUE.indexOf("^")<0){
    		  RULE_VALUE = ".*"+RULE_VALUE;
    	  }
    	  if(RULE_VALUE.indexOf("$")<0){
    		  RULE_VALUE = RULE_VALUE+".*";
    	  }
    	  RULE_VALUE = "'"+RULE_VALUE+"'";

          
          if(refer_id!=null){
 	   	     String pre = "url_decode(getvaluefromrefer(refer,\"&\",\"pre=\"),\"1\",\"2\")";
        	 work_sql += pre+" rlike "+RULE_VALUE+") or";
          }
		  return work_sql;
	  }
	  
	  public static String getPropertyMessage(String rule_id,String property_id,String flag){
		  String line = (String)rules4.get(rule_id+"\""+property_id);
          String[] strList = line.split("\"",-1);
          String name = strList[0];
          String at_path = strList[1];
          String path = strList[2];
          String is_default = strList[3];
          String start_path = strList[4];
          String end_path = strList[5];
          String asc_desc = strList[6];
          if("0".equals(asc_desc)){
        	  asc_desc = "daoshu";
          }else if("1".equals(asc_desc)){
        	  asc_desc = "shunshu";
          }
          
          
		  if("url".equals(flag)){
              if(name!=null&&!name.trim().equals("")){
			       return "get property from url by name. " + name;
              }else{
	           	   if("0".equals(is_default)){
	    			   return "get property from url by location. start_path:/  end_path:.htm  split by:"+at_path+"  order:"+asc_desc+"  location_number:"+path;
	           	   }else{
	           		   return "get property from url by location. start_path:"+start_path+"  end_path:"+end_path+"  split by:"+at_path+"  order:"+asc_desc+"  location_number:"+path;
	           	   }
             }
          }else if("refer".equals(flag)){
              if(name!=null&&!name.trim().equals("")){
            	  return "get property from refer by name " + name;
              }else{
	           	   if("0".equals(is_default)){
	           		   return "get property from refer by location. start_path:/  end_path:.htm  split by:"+at_path+"  order:"+asc_desc+"  location_number:"+path;
	           	   }else{
	           		   return "get property from refer by location. start_path:"+start_path+"  end_path:"+end_path+"  split by:"+at_path+"order:"+asc_desc+"location_number:"+path;
	           	   }
              }
          }
		  

          return null;
	  }
	  
 
	  
	   
 
	  
	  //source的值是url或者refer
	  public static String getValueByName(String source,String name,String rule_type){
		   if(source!=null&&source.indexOf("&"+name)<0&&source.indexOf("?"+name)<0){
			   return null;
		   }
	   	   UDFGetValueFromRefer test = new UDFGetValueFromRefer();
		   String value = test.evaluate(new Text(source), new Text("&"), new Text(name)).toString();
	       return value;
	  }
	  
	  
	  public static String getLocationDefault(String source,String at_path,String path){
		   String urlC = between(source,"/",".htm");
		   if("|X|X|".equals(at_path)){
			   return urlC;
		   }
		   if(urlC.indexOf(at_path)<0){
			   return null;
		   }
		   if("".equals(at_path)){
			   return null;
		   }
		   String[] strList = urlC.split(at_path);
		   String value = strList[strList.length-Integer.parseInt(path)]; 
	       return value;
	  }
	  

	  public static String getLocationValue(String source,String at_path,String path,String start_path,String end_path,String asc_desc){
		   String urlC = between(source,start_path,end_path);
		   if("|X|X|".equals(at_path)){
			   return urlC;
		   }
  		   if(urlC.indexOf(at_path)<0){//url中没有这个分隔符就返回null
  			   return null;
  		   }
  		   if("".equals(at_path)){
  			   return null;
  		   }
  		   String[] strList = urlC.split(at_path);
  		   if("0".equals(asc_desc)){//倒数
       		 String value = strList[strList.length-Integer.parseInt(path)];
			     return value;
  		   }else{//顺数
       		 String value = strList[Integer.parseInt(path)-1];
			     return value;
  		   }  
	  }

	  
	    public static final String ymdFormat(Date date) {
	        if (date == null) {
	            return "";
	        }
	        
	        DateFormat ymdFormat = new SimpleDateFormat("yyyy-MM-dd");
	        return ymdFormat.format(date);
	    }	

	  public Text evaluate(String type_ids) throws IOException{
		  try{
			  initHdfsData();
		  }catch(Exception e){
			  e.printStackTrace();
		  }
		  String sql = "sql for reference: \n"+"select count(1) from r_common_daily where pt = '20100926000000' and (";
		  String message = "";
		  try{

			 String[] typeList = type_ids.split("-");
			 for(int i=0;i<typeList.length;i++){
				 try{
					 String typeid = typeList[i];
					 String work_ids = (String)rules1.get(typeid);
					 if(work_ids==null){
						 continue;
					 }
					 String[] workidList = work_ids.split(",");
					 for(int j=0;j<workidList.length;j++){
						 try{
							 String workid = workidList[j];
							 if(workid!=null&&workid.trim().equals("")){
								 continue;
							 }
		                     String line = (String)rules2.get(workid); 
		                     String[] strList = line.split("\"",-1);
		                     String url_id = strList[0];
		                     String refer_id = strList[1];
		                     if(url_id!=null&&url_id.trim().equals("")){
		                    	 url_id = null;
		                     }
		                     if(refer_id!=null&&refer_id.trim().equals("")){
		                    	 refer_id = null;
		                     }

		                     //String yes = "false";//判别是否满足条件
		                     //同一个work_id里的url_id与refer_id是and关系。 不同work_id是or关系
		                     if(url_id!=null&&refer_id==null){
		                    	 sql += getsql(url_id,"url_id");
		                    	 message += "workid:"+workid+"\n"+getmessage(url_id,"url_id");
		                     }
		                     if(url_id==null&&refer_id!=null){
		                    	 sql += getsql(refer_id,"refer_id");
		                    	 message += "workid:"+workid+"\n"+getmessage(refer_id,"refer_id");
		                     }
		                     if(url_id!=null&&refer_id!=null){
		                    	 sql += getsql2(url_id,refer_id);
		                    	 message += "workid:"+workid+"\n"+getmessage2(url_id,refer_id);
		                     }
		                     if(url_id==null&&refer_id==null){

		                     }
						 }catch(Exception e){
							 continue;
						 }
					 }//work_id循环
				 }catch(Exception e){
					 continue;
				 }
			 }//type_id循环

		  }catch(Exception e){
			  result.set("err_message:"+type_ids);
			  return result;
		  }
		  sql = sql.substring(0,sql.length()-3)+")";
		  String split = "\n-------------------------------------------------------\n";
		  result.set(sql+split+message);
		  return result;
	  }
	  
	  
	

	  public Text evaluate(String type_ids,String property_id,String flag) throws IOException{   
			  try{
				  initHdfsData();
			  }catch(Exception e){
				  e.printStackTrace();
			  }
			  String sql = "sql for reference: \n"+"select count(1) from r_common_daily where pt = '20100926000000' and (";
			  String message = "";
			  try{
				 String[] typeList = type_ids.split("-");
				 for(int i=0;i<typeList.length;i++){
					 try{
						 String typeid = typeList[i];
						 String work_ids = (String)rules1.get(typeid);
						 if(work_ids==null){
							 continue;
						 }
						 String[] workidList = work_ids.split(",");
						 for(int j=0;j<workidList.length;j++){
							 try{
								 String workid = workidList[j];
								 if(workid!=null&&workid.trim().equals("")){
									 continue;
								 }
			                     String line = (String)rules2.get(workid); 
			                     String[] strList = line.split("\"",-1);
			                     String url_id = strList[0];
			                     String refer_id = strList[1];
			                     if(url_id!=null&&url_id.trim().equals("")){
			                    	 url_id = null;
			                     }
			                     if(refer_id!=null&&refer_id.trim().equals("")){
			                    	 refer_id = null;
			                     }
			                     if(url_id!=null&&refer_id==null){
			                    	 sql += getsql(url_id,"url_id");
			                    	 message += "workid:"+workid+"\n"+getmessage(url_id,"url_id");
			                    	 if(flag.equals("refer")){
			                    		 message += "refer_id is null,but flag is refer\n-------------------------------------------------------\n";
			                    	 }
			                    	 message += getPropertyMessage(url_id,property_id,flag)+"\n-------------------------------------------------------\n";
			                    	 
			                    	 
			                     }
			                     if(url_id==null&&refer_id!=null){
			                    	 sql += getsql(refer_id,"refer_id");
			                    	 message += "workid:"+workid+"\n"+getmessage(refer_id,"refer_id");
			                    	 if(flag.equals("url")){
			                    		 message += "url_id is null,but flag is url\n-------------------------------------------------------\n";
			                    	 }
			                    	 message += getPropertyMessage(refer_id,property_id,flag)+"\n-------------------------------------------------------\n";
			                    	 
			                    	 
			                     }
			                     if(url_id!=null&&refer_id!=null){
			                    	 sql += getsql2(url_id,refer_id);
			                    	 message += "workid:"+workid+"\n"+getmessage2(url_id,refer_id);
			                    	 if(flag.equals("url")){
			                    		 message += getPropertyMessage(url_id,property_id,flag);
			                    	 }else if(flag.equals("refer")){
			                    		 message += getPropertyMessage(refer_id,property_id,flag)+"\n-------------------------------------------------------\n";
			                    	 } 
			                     }
			                     if(url_id==null&&refer_id==null){

			                     }
							 }catch(Exception e){
								 continue;
							 }
						 }//work_id循环
					 }catch(Exception e){
						 continue;
					 }
				 }//type_id循环

			  }catch(Exception e){
				  result.set("err_message:"+type_ids);
				  return result;
			  }
			  sql = sql.substring(0,sql.length()-3)+")";
			  String split = "\n-------------------------------------------------------\n";
			  result.set(sql+split+message);
			  return result;
     }
		  
	  
	  
	  public static void initMap1(String hdfsPath) throws IOException{
		  String dst = hdfsPath;  
		  Configuration conf = new Configuration();  
		  FileSystem  fs = FileSystem.get(URI.create(dst), conf);
		  FSDataInputStream hdfsInStream = fs.open(new Path(dst));
		  String line;
		  BufferedReader bufread = new BufferedReader(new InputStreamReader(hdfsInStream,"GBK"));//GBK  
		  while((line = bufread.readLine()) != null){
			   String[] strList = line.split("\"",-1);
			   String type_id = strList[0];
			   String STATUS = strList[3];
			   String WORK_ID = strList[6];
               if("0".equals(STATUS)){
            	   rules1.put(type_id, WORK_ID); 
               }
		  }
		  hdfsInStream.close();
		  fs.close();
	  }
	  
	  
	  public static void initMap2(String hdfsPath) throws IOException{
		  String dst = hdfsPath;  
		  Configuration conf = new Configuration();  
		  FileSystem  fs = FileSystem.get(URI.create(dst), conf);
		  FSDataInputStream hdfsInStream = fs.open(new Path(dst));
		  String line;
		  BufferedReader bufread = new BufferedReader(new InputStreamReader(hdfsInStream,"GBK"));//GBK  
		  while((line = bufread.readLine()) != null){
			   String[] strList = line.split("\"",-1);
			   String work_id = strList[0];
			   String URL_ID = strList[1];
			   String REFER_ID = strList[2];
			   String STATUS = strList[3];
               if("0".equals(STATUS)){
            	   rules2.put(work_id, URL_ID+"\""+REFER_ID); 
               }
		  }
		  hdfsInStream.close();
		  fs.close();
	  }
	  
	  public static void initMap3(String hdfsPath) throws IOException, ParseException{
		  String dst = hdfsPath;  
		  Configuration conf = new Configuration();  
		  FileSystem  fs = FileSystem.get(URI.create(dst), conf);
		  FSDataInputStream hdfsInStream = fs.open(new Path(dst));
		  String line;
		  BufferedReader bufread = new BufferedReader(new InputStreamReader(hdfsInStream,"GBK"));//GBK  
		  while((line = bufread.readLine()) != null){
			   String[] strList = line.split("\"",-1);
			   String rule_id = strList[0];
			   String RULE_VALUE = strList[2];
			   String PATH = strList[3];
			   String STATUS = strList[4];
			   String STARTS = strList[5].replace("-","");
			   String ENDS = strList[6].replace("-","");
               if("0".equals(STATUS)){
            	   rules3.put(rule_id, RULE_VALUE+"\""+PATH+"\""+STARTS+"\""+ENDS); 
               }
		  }
		  hdfsInStream.close();
		  fs.close();
	  }

	  public static void initMap4(String hdfsPath) throws IOException{
		  String dst = hdfsPath;  
		  Configuration conf = new Configuration();  
		  FileSystem  fs = FileSystem.get(URI.create(dst), conf);
		  FSDataInputStream hdfsInStream = fs.open(new Path(dst));
		  String line;
		  BufferedReader bufread = new BufferedReader(new InputStreamReader(hdfsInStream,"GBK"));//GBK  
		  while((line = bufread.readLine()) != null){
			   String[] strList = line.split("\"",-1);
			   String URL_ID = strList[1];
			   String P_ID = strList[2];
			   String STATUS = strList[3];
			   String NAME = strList[6];
			   String AT_PATH = strList[7];
			   String PATH = strList[8];
			   String is_default = strList[9];
			   String start_path = strList[10];
			   String end_path = strList[11];
			   String asc_desc = strList[12];
               if("0".equals(STATUS)){
            	   rules4.put(URL_ID+"\""+P_ID,NAME+"\""+AT_PATH+"\""+PATH+"\""+is_default+"\""+start_path+"\""+end_path+"\""+asc_desc); 
               }
		  }
		  hdfsInStream.close();
		  fs.close();
	  }
	  

		public static String getYesToday(String strBeginDate){
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
			          cld.set(Calendar.DAY_OF_YEAR, day - 1);
			          Date dt = cld.getTime();
			          strDate = new SimpleDateFormat("yyyyMMdd").format(dt);
			          strInputPath = strDate;
			    }catch(NumberFormatException e){
			  	      e.printStackTrace();
			    }
	                  return strInputPath;
		}
	 
		
		
		
		public static String between(String value,String begin,String end){
			  int index1 = value.lastIndexOf(begin);
			  if("".equals(begin)&&"".equals(end)){
				  return value;
			  }
			  if("".equals(begin)){
				  index1 = 0;
			  }
			   if(!end.equals("end")){
				   int index2 = value.indexOf(end);
				   if("".equals(end)){
					   index2 =  value.length();
				   }
				   value = value.substring(index1+begin.length(), index2);
			       return value;
			   }else{
				   value = value.substring(index1+begin.length());
			       return value;
			   }
		}
		
		
		public static void add1(String str){
			   String line1 = str;
			   String[] strList = line1.split("\"",-1);
			   String type_id = strList[0];
			   String STATUS = strList[3];
			   String WORK_ID = strList[6];
			   if("0".equals(STATUS)){
				   rules1.put(type_id, WORK_ID); 
			   }
		}
		
		public static void add2(String str){
			   String line2 = str;
			   String[] strList = line2.split("\"",-1);
			   String work_id = strList[0];
			   String URL_ID = strList[1];
			   String REFER_ID = strList[2];
			   String STATUS = strList[3];
			   if("0".equals(STATUS)){
				   rules2.put(work_id, URL_ID+"\""+REFER_ID); 
			   }
		}
		
		public static void add3(String str){
			   String line3 = str;
			   String[] strList = line3.split("\"",-1);
			   String rule_id = strList[0];
			   String RULE_VALUE = strList[2];
			   String PATH = strList[3];
			   String STATUS = strList[4];
			   String STARTS = strList[5].replace("-","");
			   String ENDS = strList[6].replace("-","");
			   if("0".equals(STATUS)){
				   rules3.put(rule_id, RULE_VALUE+"\""+PATH+"\""+STARTS+"\""+ENDS); 
			   }
		}
		
		public static void add4(String str){
			   String line4 = str;
			   String[] strList = line4.split("\"",-1);
			   String URL_ID = strList[1];
			   String P_ID = strList[2];
			   String STATUS = strList[3];
			   String NAME = strList[6];
			   String AT_PATH = strList[7];
			   String PATH = strList[8];
			   String is_default = strList[9];
			   String start_path = strList[10];
			   String end_path = strList[11];
			   String asc_desc = strList[12];
	           if("0".equals(STATUS)){
	        	   rules4.put(URL_ID+"\""+P_ID,NAME+"\""+AT_PATH+"\""+PATH+"\""+is_default+"\""+start_path+"\""+end_path+"\""+asc_desc); 
	           }
		}
		
		

		
		public static void initHdfsData() throws IOException, ParseException{
			initHdfs1();
			initHdfs2();
			initHdfs3();
			initHdfs4();
		}

		public static void initHdfs1() throws IOException{
			if(hdfs1_flag==null){
				hdfs1_flag = loadData1(FILE1);
			}
		}
		
		public static void initHdfs2() throws IOException{
			if(hdfs2_flag==null){
				hdfs2_flag = loadData2(FILE2);
			}
		}
		
		public static void initHdfs3() throws IOException, ParseException{
			if(hdfs3_flag==null){
				hdfs3_flag = loadData3(FILE3);
			}
		}
		
		public static void initHdfs4() throws IOException{
			if(hdfs4_flag==null){
				hdfs4_flag = loadData4(FILE4);
			}
		}

		public static String loadData1(String file){
			  BufferedReader reader = null;
		      String line = null;
		      try {
		         reader = new BufferedReader(new InputStreamReader(new FileInputStream(file),"GBK"));
		         while (((line = reader.readLine()) != null)) {
		        	   try{
						   String[] strList = line.split("\"",-1);
						   String type_id = strList[0];
						   String STATUS = strList[3];
						   String WORK_ID = strList[6];
			               if("0".equals(STATUS)){
			            	   rules1.put(type_id, WORK_ID); 
			               }
		        	   }catch(Exception e){
		        		   continue;
		        	   }
		         }
		      }catch(Exception e){
		    	  e.printStackTrace();
		      }	
		      return "success";
		}
		
		public static String loadData2(String file){
			  BufferedReader reader = null;
		      String line = null;
		      try {
		         reader = new BufferedReader(new InputStreamReader(new FileInputStream(file),"GBK"));
		         while (((line = reader.readLine()) != null)) {
		        	   try{
						   String[] strList = line.split("\"",-1);
						   String work_id = strList[0];
						   String URL_ID = strList[1];
						   String REFER_ID = strList[2];
						   String STATUS = strList[3];
			               if("0".equals(STATUS)){
			            	   rules2.put(work_id, URL_ID+"\""+REFER_ID); 
			               }  
		        	   }catch(Exception e){
		        		   continue;
		        	   }
		         }
		      }catch(Exception e){
		    	  e.printStackTrace();
		      }	
		      return "success";
		}
		
		
		public static String loadData3(String file){
			  BufferedReader reader = null;
		      String line = null;
		      try {
		         reader = new BufferedReader(new InputStreamReader(new FileInputStream(file),"GBK"));
		         while (((line = reader.readLine()) != null)) {
		        	   try{
						   String[] strList = line.split("\"",-1);
						   String rule_id = strList[0];
						   String RULE_VALUE = strList[2];
						   String PATH = strList[3];
						   String STATUS = strList[4];
						   String STARTS = strList[5].replace("-","");
						   String ENDS = strList[6].replace("-","");
			               if("0".equals(STATUS)){
			            	   rules3.put(rule_id, RULE_VALUE+"\""+PATH+"\""+STARTS+"\""+ENDS); 
			               }
		        	   }catch(Exception e){
		        		   continue;
		        	   }
		         }
		      }catch(Exception e){
		    	  e.printStackTrace();
		      }	
		      return "success";
		}
		
		
		public static String loadData4(String file){
			  BufferedReader reader = null;
		      String line = null;
		      try {
		         reader = new BufferedReader(new InputStreamReader(new FileInputStream(file),"GBK"));
		         while (((line = reader.readLine()) != null)) {
		        	 try{
						   String[] strList = line.split("\"",-1);
						   String URL_ID = strList[1];
						   String P_ID = strList[2];
						   String STATUS = strList[3];
						   String NAME = strList[6];
						   String AT_PATH = strList[7];
						   String PATH = strList[8];
						   String is_default = strList[9];
						   String start_path = strList[10];
						   String end_path = strList[11];
						   String asc_desc = strList[12];
			               if("0".equals(STATUS)){
			            	   rules4.put(URL_ID+"\""+P_ID,NAME+"\""+AT_PATH+"\""+PATH+"\""+is_default+"\""+start_path+"\""+end_path+"\""+asc_desc); 
			               }   
		        	 }catch(Exception e){
		        		 continue;
		        	 }
		         }
		      }catch(Exception e){
		    	  e.printStackTrace();
		      }	
		      return "success";
		}
		
		
		//文件放最根目录下
		public static void initData(){
			    File file = new File("rule_type.txt");
		        BufferedReader reader = null;
		        try {
		            reader = new BufferedReader(new FileReader(file));
		            String tempString = null;
		            while ((tempString = reader.readLine()) != null) {
		                add1(tempString);
		            }
		            reader.close();
		        } catch (IOException e) {
		            e.printStackTrace();
		        } finally {
		            if (reader != null) {
		                try {
		                    reader.close();
		                } catch (IOException e1) {
		                }
		            }
		        }
		        ///////////////////////////////////////////////////////////////////
			    file = new File("rule_url_work.txt");
		        reader = null;
		        try {
		            reader = new BufferedReader(new FileReader(file));
		            String tempString = null;
		            while ((tempString = reader.readLine()) != null) {
		                add2(tempString);
		            }
		            reader.close();
		        } catch (IOException e) {
		            e.printStackTrace();
		        } finally {
		            if (reader != null) {
		                try {
		                    reader.close();
		                } catch (IOException e1) {
		                }
		            }
		        }
		        ///////////////////////////////////////////////////////////////////
			    file = new File("rule_url.txt");
		        reader = null;
		        try {
		            reader = new BufferedReader(new FileReader(file));
		            String tempString = null;
		            while ((tempString = reader.readLine()) != null) {
		                add3(tempString);
		            }
		            reader.close();
		        } catch (IOException e) {
		            e.printStackTrace();
		        } finally {
		            if (reader != null) {
		                try {
		                    reader.close();
		                } catch (IOException e1) {
		                }
		            }
		        }
		        ///////////////////////////////////////////////////////////////////
			    file = new File("rule_url_properfy.txt");
		        reader = null;
		        try {
		            reader = new BufferedReader(new FileReader(file));
		            String tempString = null;
		            while ((tempString = reader.readLine()) != null) {
		                add4(tempString);
		            }
		            reader.close();
		        } catch (IOException e) {
		            e.printStackTrace();
		        } finally {
		            if (reader != null) {
		                try {
		                    reader.close();
		                } catch (IOException e1) {
		                }
		            }
		        }
		        ///////////////////////////////////////////////////////////////////
		}
		
	  public static void main(String[] args) throws IOException, ParseException{
		  //注：本机调试时要被evaluate里initHdfsData();注释掉
		   UDFGetSql test = new UDFGetSql("");
		   initData();
		   //System.out.println(test.evaluate("41"));
		   System.out.println(test.evaluate("41","61","url"));
	 
		   
	  }
	  
	  
	  
}

