package com.taobao.hive.udf.notcommon;

import java.util.regex.Pattern;

import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.Text;
/**
 *  
 * 
 * CREATE TEMPORARY FUNCTION getbase64string  AS 'com.taobao.hive.udf.notcommon.UDFGetBase64String';
 *  
 *  
 * @author youliang
 *
 */
public class UDFGetBase64String extends UDF{

	  Text result = new Text();
	  public UDFGetBase64String() {
	  }

	  public Text evaluate(String str) {
		  try{
			    if (str == null) {
				      return null;
				    }
					String a = str;

					int length = str.length();

					for (int i = 0; i < length; i++) {
						String s = String.valueOf(str.charAt(i));

						if (s.equals("_")) {
							String lowerStr = String.valueOf(str.charAt(i + 1));
							a = a.replace("_" + lowerStr, lowerStr.toUpperCase());
						}
					}

				    result.set(a);
				    return result;
		  }catch(Exception e){
			    result.set("err");
			    return result;
		  }

	  }
	  
	  public static void main(String args[]){
		  UDFGetBase64String test = new UDFGetBase64String();
		  System.out.println(test.evaluate("aa_k_mdddM_kk"));
		  System.out.println(test.evaluate("fasfsd"));
		 
	  }
}
