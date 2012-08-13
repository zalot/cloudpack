package com.taobao.hive.udf;

import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.Text;

//CREATE TEMPORARY FUNCTION url_encode  AS 'com.taobao.hive.udf.UDFUrlEncode';
@Description(
	    name = "url_encode",
	    value = "_FUNC_(expr[,codec]) - encode url by java.net.URLEncoder.",
	    extended = "Example:\n " +
	        "  > SELECT _FUNC_('http://taobao.com','utf-8') FROM dual;\n"
	    )
public class UDFUrlEncode extends UDF{

	private Text result = new Text();
	
	public Text evaluate(String url) {
		if (url == null || "".equals(url)) return null;
		return evaluate(url,"utf-8");
	}
	
	public Text evaluate(String url, String encode) {
		if (encode == null || "".equals(encode)) encode="utf-8";
		try {
			result.set(java.net.URLEncoder.encode(url, encode.toLowerCase()));
			return result;
		} catch (Exception e) {
			return null;
		}
	}
}
