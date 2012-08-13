package com.taobao.hive.udf;



import java.io.IOException;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
 * CREATE TEMPORARY FUNCTION gethdfs  AS 'com.taobao.hive.udf.UDFGetHDFS';
 *  
 * 
 * @author youliang   
 *  
 *  
 *     
 *     
 */

 
public class UDFGetHDFS extends UDF{
	  static RegexMapNew<String> mapTest = new RegexMapNew<String>();
	  Text result = new Text();
	  
	  public UDFGetHDFS() throws IOException {
		  Date dtBegin = new Date();
		  String date = new SimpleDateFormat("yyyyMMdd").format(dtBegin);
		  String yestoday = getYesToday(date);
		  String hdfsPath = "hdfs://hdpnn:9000/group/taobao/taobao/dw/fact/"+yestoday+"/dim_taohua/dim_taohua.txt";  
		  initMap(hdfsPath);
	  } 

	  public static void initMap(String hdfsPath) throws IOException{
		  String dst = hdfsPath;  
		  Configuration conf = new Configuration();  
		  FileSystem  fs = FileSystem.get(URI.create(dst), conf);
		  FSDataInputStream hdfsInStream = fs.open(new Path(dst));
		  byte[] ioBuffer = new byte[4096];
		  int readLen = hdfsInStream.read(ioBuffer);
		  while(-1 != readLen){
			   String line = new String(ioBuffer, 0, ioBuffer.length,"UTF-8");
			   String[] strList = line.split("\"");
			   String name = strList[1];
			   String zhengze = strList[2];
			   Pattern p = Pattern.compile(zhengze.trim(), Pattern.MULTILINE);
			   mapTest.put(p, name.trim());   
		       readLen = hdfsInStream.read(ioBuffer);
		  }
		  hdfsInStream.close();
		  fs.close();
	  }

	  public Text evaluate(String url,String flag) throws IOException{
		  if(url==null){
			  return null;
		  }
		  if(flag==null){
			  return null;
		  }
		  if(flag.equals("taohua")){
			  String name = mapTest.get(url);
			  if(name==null){
				  return null;
			  }
			  result.set(name);
			  return result;
		  }else{
			  return null;
		  }
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
	          strYear = strDate.substring(0,4);
	           
	           
	          strInputPath = strDate;
	       
	   }catch(NumberFormatException e){
	  	 e.printStackTrace();
	   }
	     
	   return strInputPath;
		}
	 
	  public static void main(String[] args) throws IOException{
		  UDFGetHDFS test = new UDFGetHDFS();
		  
		  
	  }
	  
	  
	  
}
