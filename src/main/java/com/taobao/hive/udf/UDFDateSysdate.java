package com.taobao.hive.udf;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.Text;
/**
 *  
 *  
 * CREATE TEMPORARY FUNCTION sysdate  AS 'com.taobao.hive.udf.UDFDateSysdate';
 *  
 */
public class UDFDateSysdate extends UDF{

	  Text result = new Text();
	  public UDFDateSysdate() {
	  }
//	  public Text evaluate(String format){
//		    Date date = new Date();
//		    DateFormat ymdhmsFormat = new SimpleDateFormat(format);
//
//			result.set(ymdhmsFormat.format(date));
//			return result;
//	  }
//	  
//
//	  public static void main(String[] args){
//		  UDFDateSysdate test = new UDFDateSysdate();
//		  System.out.println(test.evaluate("yyyyMMddHHmmss"));
//	  }

	  private String lastpreFormat;
	  private DateFormat ymdhmsFormat;
	  
	  public Text evaluate(String format) {
	  		Date date = new Date();
	  		if (lastpreFormat == null || !lastpreFormat.equals(format)) {
	  			ymdhmsFormat = new SimpleDateFormat(format);
	  			lastpreFormat = format;
	  		}
	  		result.set(ymdhmsFormat.format(date));
	  		return result;
	  	}
	  
	  	public static void main(String[] args) {
	  		UDFDateSysdate test = new UDFDateSysdate();
	  		System.out.println(test.evaluate("yyyyMMddHHmmss"));
	  	}
	  
	  
}
