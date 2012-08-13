package com.taobao.hive.udf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.HashMap;
import java.util.regex.Pattern;
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
 * 
 */

public class UDFGetUrlRecord extends UDF{
	  private static HashMap<String,String> rules1 = new HashMap<String,String>();
	  private static HashMap<String,String> rules2 = new HashMap<String,String>();
	  private static HashMap<String,String> rules3 = new HashMap<String,String>();
	  private static HashMap<String,String> rules4 = new HashMap<String,String>();
	  private static HashMap<String,Pattern> rulePattern = new HashMap<String,Pattern>();
	  private static String hdfs1_flag = null;
	  private static String hdfs2_flag = null;
	  private static String hdfs3_flag = null;
	  private static String hdfs4_flag = null;
	  
//	  private static final String FILE1 = "./rule_type.txt";
//	  private static final String FILE2 = "./rule_url_work.txt";
//	  private static final String FILE3 = "./rule_url.txt";
//	  private static final String FILE4 = "./rule_url_property.txt";
	  
	  private static final String FILE1 = "D:\\rule_type.txt";
	  private static final String FILE2 = "D:\\rule_url_work.txt";
	  private static final String FILE3 = "D:\\rule_url.txt";
	  private static final String FILE4 = "D:\\rule_url_property.txt";
	  
	  private static UDFGetValueFromRefer getValueFormRefer = new UDFGetValueFromRefer();
	  private static UDFURLDecode decode = new UDFURLDecode();
	  private Pattern pattern = Pattern.compile("^[\\d]{8,}$");
	  
	  Text result = new Text();
	  
	  public UDFGetUrlRecord() throws IOException, ParseException {
	  } 
	  
	  public static String validate(String rule_id,String rule_type,String time,String url,String refer,String prefix){
      	  String line = (String)rules3.get(rule_id);
     	  String[] strList = line.split("\"",-1);
    	  String PATH = strList[1];//0URL字段 1refer字段规则 2通用
    	  String STARTS = strList[2];//之前放入rules3的时候，就已经把格式处理成yyyyMMdd格式了
    	  String ENDS = strList[3];
          if(rule_id!=null&&rule_type.equals("url_id")){
        	 if(Integer.parseInt(time)>=Integer.parseInt(STARTS)&&Integer.parseInt(time)<=Integer.parseInt(ENDS)){
        		 Pattern p = rulePattern.get(rule_id);
        		 if(PATH.equals("10")||PATH.equals("8")){//作用于URL
    		        if(p != null && p.matcher(url).find()){
    				    return "true";
    		        }else{
    		        	return "url not match";
    		        }
        		 }else{
        			 return "rule effect on url,but path is 1";
        		 }
        	 }else{
        		 return "time is not in range";
        	 }
          }
          if(rule_id!=null&&rule_type.equals("refer_id")){
        	 if(Integer.parseInt(time)>=Integer.parseInt(STARTS)&&Integer.parseInt(time)<=Integer.parseInt(ENDS)){
        		 Pattern p = rulePattern.get(rule_id);
         		 if(PATH.equals("9")||PATH.equals("8")){//作用于refer
         			String pre = "";
         			if(refer!=null){
         	   			pre = getValueFormRefer.evaluate(new Text(refer),new Text("&"),new Text(prefix)).toString();
         	   			pre = decode.evaluate(new Text(pre), "flag1", "flag2").toString();//decode操作
         			}
			        if(p != null && p.matcher(pre).find()){
					    return "true";
			        }else{
			        	return "pre not match";
			        }
         		 }else{
         			return "rule effect on refer,but path is 0";
         		 }
        	 }else{
        		 return "time is not in range";
        	 }
          }
		  return "false";
	  }
	  
	  public static String getValue(String rule_id,String rule_type,String property_id,String url,String refer,String prefix){
		  String line = (String)rules4.get(rule_id+"\""+property_id);
          String[] strList = line.split("\"",-1);
          String name = strList[0];
          String at_path = strList[1];
          String path = strList[2];
          String is_default = strList[3];
          String start_path = strList[4];
          String end_path = strList[5];
          String asc_desc = strList[6];
		  if("url".equals(rule_type)){
              if(name!=null&&!name.trim().equals("")){
			       return getValueByName(url,name,rule_type);
              }else{
	           	   if("0".equals(is_default)){
	    			   return getLocationDefault(url,at_path,path);
	           	   }else{
	           		   return getLocationValue(url,at_path,path,start_path,end_path,asc_desc); 
	           	   }
             }
          }else if("refer".equals(rule_type)){
              if(name!=null&&!name.trim().equals("")){
    		       return getValueByName(refer,name,rule_type);
              }else{
        		   String pre = "";
        		   if(refer!=null){
           			  pre = getValueFormRefer.evaluate(new Text(refer),new Text("&"),new Text(prefix)).toString();
           			  pre = decode.evaluate(new Text(pre), "flag1", "flag2").toString();//decode操作
        		   }
	           	   if("0".equals(is_default)){
	    			   return getLocationDefault(pre,at_path,path);
	           	   }else{
	           		   return getLocationValue(pre,at_path,path,start_path,end_path,asc_desc); 
	           	   }
              }
          }
		  

          return null;
	  }
	  
	   
 
	  
	  //source的值是url或者refer
	  public static String getValueByName(String source,String name,String rule_type){
/*		   if("refer".equals(rule_type)){
	    		  String pre = "";
	    		  if(source!=null){
	       			  pre = getValueFormRefer.evaluate(new Text(source),new Text("&"),new Text("pre=")).toString();
	       			  pre = decode.evaluate(new Text(pre), "flag1", "flag2").toString();//decode操作
	       			  source = pre;
	    		  }
		   }*/
		   if(source!=null&&source.indexOf("&"+name)<0&&source.indexOf("?"+name)<0){
			   return null;
		   }
		   String value = getValueFormRefer.evaluate(new Text(source), new Text("&"), new Text(name)).toString();
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

	  

	  public Text evaluate(String time,String url,String refer,String type_ids) throws IOException{
		  return evaluate(time,url,refer,type_ids,"pre=");
	  }
	  
	  public Text evaluate(String time,String url,String refer,String type_ids,String prefix) throws IOException{
		  if(time==null||url==null||refer==null||type_ids==null){
			    result.set("args is null:"+time+"\""+url+"\""+refer+"\""+type_ids);
			    return result;
		  }
		  if (!validateParam(time)) {
				result.set("false");
				return result;
		  }
		  initHdfsData();
		  String yes = "false";//判别是否满足条件
     	  String yes1 = "false";
    	  String yes2 = "false";
		  try{
			 time = time.substring(0,8);  //先格式化成yyyyMMdd形式
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
		                    	 yes = validate(url_id,"url_id",time,url,refer,prefix);
		                     }
		                     if(url_id==null&&refer_id!=null){
		                    	 yes = validate(refer_id,"refer_id",time,url,refer,prefix);
		                     }
		                     if(url_id!=null&&refer_id!=null){
		                    	 yes1 = validate(url_id,"url_id",time,url,refer,prefix);
		                    	 yes2 = validate(refer_id,"refer_id",time,url,refer,prefix);
		                    	 if("true".equals(yes1)&&"true".equals(yes2)){
		                    		 yes = "true";
		                    	 }
		                     }
		                     if(url_id==null&&refer_id==null){

		                     }
		                     if("true".equals(yes)){
		              		     result.set(yes);
		             		     return result;
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
			  result.set("err_message:"+time+"\""+url+"\""+refer+"\""+type_ids);
			  return result;
		  }
		  result.set("result:"+yes+"\""+"rule_id:"+yes1+"\""+"refer_id:"+yes2);
		  return result;
	  }

	  public Text evaluate(String time,String url,String refer,String type_ids,String property_id,String flag) throws IOException{
		  return evaluate(time,url,refer,type_ids,property_id,flag,"pre=");
	  }  
	  
	  public Text evaluate(String time,String url,String refer,String type_ids,String property_id,String flag,String prefix) throws IOException{
		  if(time==null||url==null||refer==null||type_ids==null||property_id==null){
			  return null;
		  }
		  if (!validateParam(time)) return null;
		  if(!"url".equals(flag)&&!"refer".equals(flag)){
			  return null;
		  }
		  initHdfsData();
		  try{
			 time = time.substring(0,8);  //先格式化成yyyyMMdd形式
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
		                     if(url_id!=null&&url_id.equals("")){
		                    	 url_id = null;
		                     }
		                     if(refer_id!=null&&refer_id.equals("")){
		                    	 refer_id = null;
		                     }

		                     //同一个work_id里的url_id与refer_id是and关系。 不同work_id是or关系
		                     if(url_id!=null&&refer_id==null){
		                    	 if(flag.equals("refer")){
		                    		 continue;
		                    	 }
		                    	 String yes = validate(url_id,"url_id",time,url,refer,prefix);
		                    	 if("true".equals(yes)){
		                             String property_value = getValue(url_id,flag,property_id,url,refer,prefix);
		                             if(property_value==null){
		                            	 continue;//如果getValue是null,应该进入下一个循环,继续换其他方式去取属性值。
		                             }
						   		     result.set(property_value);
								     return result; 
		        				 }
		                      }
		                     if(url_id==null&&refer_id!=null){
		                    	 if(flag.equals("url")){
		                    		 continue;
		                    	 }
		                    	 String yes = validate(refer_id,"refer_id",time,url,refer,prefix); 
		  				         if("true".equals(yes)){
		  				             String property_value = getValue(refer_id,flag,property_id,url,refer,prefix);
		                             if(property_value==null){
		                            	 continue;
		                             }
						   		     result.set(property_value);
								     return result; 
		  				         }
		                     }
		                     if(url_id!=null&&refer_id!=null){
		                    	 String yes = "false";
		                    	 String yes1 = validate(url_id,"url_id",time,url,refer,prefix);
		                    	 String yes2 = validate(refer_id,"refer_id",time,url,refer,prefix);
		                    	 if("true".equals(yes1)&&"true".equals(yes2)){
		                    		 yes = "true";
		                    	 }
		                    	 if("true".equals(yes)){
		                    		 if(flag.equals("url")){
		      				             String property_value = getValue(url_id,flag,property_id,url,refer,prefix);
		                                 if(property_value==null){
		                                	 continue;
		                                 }
		    				   		     result.set(property_value);
		    						     return result; 
		                    		 }
		                    		 if(flag.equals("refer")){
		      				             String property_value = getValue(refer_id,flag,property_id,url,refer,prefix);
		                                 if(property_value==null){
		                                	 continue;
		                                 }
		    				   		     result.set(property_value);
		    						     return result; 
		                    		 } 
		                    	 }
		                     }
						 }catch(Exception e){
							 continue;
						 }
					 } //work_id循环
				 }catch(Exception e){
					 continue;
				 }
			 }//type_id循环
		  }catch(Exception e){
			   return null;
		  }
		  return null;
	  }  
	  
	private boolean validateParam(String time) {
		return pattern.matcher(time).matches();
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
				   if("".equals(end) || index2==-1){
					   index2 =  value.length();
				   }
				   if (index2>=index1+begin.length())
					   value = value.substring(index1+begin.length(), index2);
			       return value;
			   }else{
				   value = value.substring(index1+begin.length());
			       return value;
			   }
		}

		public static void initHdfsData() throws IOException{
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
		
		public static void initHdfs3() throws IOException{
			if(hdfs3_flag==null){
				hdfs3_flag = loadData3(FILE3);
			}
		}
		
		public static void initHdfs4() throws IOException{
			if(hdfs4_flag==null){
				hdfs4_flag = loadData4(FILE4);
			}
		}

		public static String loadData1(String file) throws IOException{
			  BufferedReader reader = null;
		      String line = null;
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
		      return "success";
		}
		
		public static String loadData2(String file) throws IOException{
			  BufferedReader reader = null;
		      String line = null;
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
		      return "success";
		}
		
		
		public static String loadData3(String file) throws IOException{
			  BufferedReader reader = null;
		      String line = null;
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
		            	   rulePattern.put(rule_id, Pattern.compile(RULE_VALUE));
		               }
	        	   }catch(Exception e){
	        		   continue;
	        	   }
	         }
		      return "success";
		}
		
		
		public static String loadData4(String file) throws IOException{
			  BufferedReader reader = null;
		      String line = null;
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
		      return "success";
		}  
		
		public static void main(String[] args) {
			try {
				UDFGetUrlRecord udf = new UDFGetUrlRecord();
				System.out.println(udf.evaluate("20120719","http://www.taobao.com","1.gif?pre=http://search.taobao.com","262"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
}
