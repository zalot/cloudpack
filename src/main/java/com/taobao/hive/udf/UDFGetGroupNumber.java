package com.taobao.hive.udf;



import java.io.IOException;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
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
 * CREATE TEMPORARY FUNCTION get_group_number  AS 'com.taobao.hive.udf.UDFGetGroupNumber';
 * select get_group_number(s,a,b) from ...
 * 
 * @author youliang   
 * 2010-08-05
 *  
 *     
 *     
 */

public class UDFGetGroupNumber extends UDF{
	  
	  Text result = new Text();
	  
	  public UDFGetGroupNumber() throws IOException {
		 

	  } 


	  public Text evaluate(String str,String flag1,String flag2) throws IOException{
		    Pattern p = null;
		    try{
		    	p = Pattern.compile("('"+flag1+"'.*?'"+flag2+"')");
		    }catch(Exception e){
		    	return null;
		    }
	        Matcher m = p.matcher(str);
	        int i = 0;
	        while(m.find()){
	        	i++;
	        }
		    result.set(i+"");
		    return result;
	  }  
	  
	  

	  public Text evaluate(String str,String flag) throws IOException{
		    Pattern p = null;
		    try{
		    	p = Pattern.compile("('"+flag+"')");
		    }catch(Exception e){
		    	return null;
		    }
	        Matcher m = p.matcher(str);
	        int i = 0;
	        while(m.find()){
	        	i++;
	        }
		    result.set(i+"");
		    return result;
	  }  
		   
		
	 
	  public static void main(String[] args) throws IOException{
		  UDFGetGroupNumber test = new UDFGetGroupNumber();
		  System.out.println(test.evaluate("2010-08-04 21:48:24,102 Mozilla/5.0 (iPad; U; CPU OS 3_2 like Mac OS X; zh-cn) AppleWebKit/531.21.10 (KHTML, like Gecko) Version/4.0.4 Mobile/7B367 Safari/531.21.10        http://member1.taobao.com/member/mini_register.htm?TPL_redirect_url=http%3A%2F%2Fbuy.taobao.com%2Fauction%2Ffastbuy%2Fvalidate_reg.htm&mobile=13366998667   ae21dc8d713da278de3991fa57ef2a59        228665  Wed Aug 04 21:44:35 CST 2010        Wed Aug 04 21:48:23 CST 2010    {'mouse_pos': [], 'browser': 'Safari|4|an unknown OS', 'mouse_click': [{'y': 206, 'x': 205, 'button': 'left', 'target': 'J_Email'}, {'y': 206, 'x': 205, 'button': 'left', 'target': 'J_Email'}, {'y': 317, 'x': 137, 'button': 'left', 'target': 'J_Code'}, {'y': 317, 'x': 137, 'button': 'left', 'target': 'J_Code'}, {'y': 200, 'x': 223, 'button': 'left', 'target': 'J_Email'}, {'y': 200, 'x': 223, 'button': 'left', 'target': 'J_Email'}, {'y': 365, 'x': 137, 'button': 'left', 'target': 'J_Code'}, {'y': 365, 'x': 137, 'button': 'left', 'target': 'J_Code'}, {'y': 200, 'x': 250, 'button': 'left', 'target': 'J_Email'}, {'y': 200, 'x': 250, 'button': 'left', 'target': 'J_Email'}, {'y': 198, 'x': 239, 'button': 'left', 'target': 'J_Email'}, {'y': 198, 'x': 239, 'button':","left","J_Email"));
		  // 参数 "Safari" 实际查的是  'Safari'
		  System.out.println(test.evaluate("2010-08-04 21:48:24,102 Mozilla/5.0 (iPad; U; CPU OS 3_2 like Mac OS X; zh-cn) AppleWebKit/531.21.10 (KHTML, like Gecko) Version/4.0.4 Mobile/7B367 'Safari'/531.21.10        http://member1.taobao.com/member/mini_register.htm?TPL_redirect_url=http%3A%2F%2Fbuy.taobao.com%2Fauction%2Ffastbuy%2Fvalidate_reg.htm&mobile=13366998667   ae21dc8d713da278de3991fa57ef2a59        228665  Wed Aug 04 21:44:35 CST 2010        Wed Aug 04 21:48:23 CST 2010    {'mouse_pos': [], 'browser': 'Safari|4|an unknown OS', 'mouse_click': [{'y': 206, 'x': 205, 'button': 'left', 'target': 'J_Email'}, {'y': 206, 'x': 205, 'button': 'left', 'target': 'J_Email'}, {'y': 317, 'x': 137, 'button': 'left', 'target': 'J_Code'}, {'y': 317, 'x': 137, 'button': 'left', 'target': 'J_Code'}, {'y': 200, 'x': 223, 'button': 'left', 'target': 'J_Email'}, {'y': 200, 'x': 223, 'button': 'left', 'target': 'J_Email'}, {'y': 365, 'x': 137, 'button': 'left', 'target': 'J_Code'}, {'y': 365, 'x': 137, 'button': 'left', 'target': 'J_Code'}, {'y': 200, 'x': 250, 'button': 'left', 'target': 'J_Email'}, {'y': 200, 'x': 250, 'button': 'left', 'target': 'J_Email'}, {'y': 198, 'x': 239, 'button': 'left', 'target': 'J_Email'}, {'y': 198, 'x': 239, 'button':","Safari"));
		  System.out.println(test.evaluate("2010-08-04 21:48:24,102 Mozilla/5.0 (iPad; U; CPU OS 3_2 like Mac OS X; zh-cn) AppleWebKit/531.21.10 (KHTML, like Gecko) Version/4.0.4 Mobile/7B367 'Safari'/531.21.10        http://member1.taobao.com/member/mini_register.htm?TPL_redirect_url=http%3A%2F%2Fbuy.taobao.com%2Fauction%2Ffastbuy%2Fvalidate_reg.htm&mobile=13366998667   ae21dc8d713da278de3991fa57ef2a59        228665  Wed Aug 04 21:44:35 CST 2010        Wed Aug 04 21:48:23 CST 2010    {'mouse_pos': [], 'browser': 'Safari|4|an unknown OS', 'mouse_click': [{'y': 206, 'x': 205, 'button': 'left', 'target': 'J_Email'}, {'y': 206, 'x': 205, 'button': 'left', 'target': 'J_Email'}, {'y': 317, 'x': 137, 'button': 'left', 'target': 'J_Code'}, {'y': 317, 'x': 137, 'button': 'left', 'target': 'J_Code'}, {'y': 200, 'x': 223, 'button': 'left', 'target': 'J_Email'}, {'y': 200, 'x': 223, 'button': 'left', 'target': 'J_Email'}, {'y': 365, 'x': 137, 'button': 'left', 'target': 'J_Code'}, {'y': 365, 'x': 137, 'button': 'left', 'target': 'J_Code'}, {'y': 200, 'x': 250, 'button': 'left', 'target': 'J_Email'}, {'y': 200, 'x': 250, 'button': 'left', 'target': 'J_Email'}, {'y': 198, 'x': 239, 'button': 'left', 'target': 'J_Email'}, {'y': 198, 'x': 239, 'button':","Safari"));
	  }
	  
	  
	  
}
