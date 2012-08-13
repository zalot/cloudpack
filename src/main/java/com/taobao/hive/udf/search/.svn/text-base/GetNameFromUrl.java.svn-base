package com.taobao.hive.udf.search;

import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.Text;
import com.taobao.hive.udf.UDFGetValueFromRefer;
import com.taobao.hive.udf.SearchURLDecode;
import com.taobao.hive.udf.search.CommonData;


/**
 * 从url中解析中文（如query等）
 * 
 * @author 宋智
 * @created 2011-03-10
 */
public class GetNameFromUrl extends UDF{

	private UDFGetValueFromRefer getValue = new UDFGetValueFromRefer();
	private SearchURLDecode decoder = new SearchURLDecode();
	
	public GetNameFromUrl() {
	}
	
	/**
	 * @param url 日志中的url
	 * @param index_String 第一级解析的值
	 * @return 解码后解析的值
	 */
	public Text evaluate(Text url,Text index_String) {
		return getName(url, index_String);
	}
	
	/**
	 * @param url 日志中的url	 * 
	 * @return 解码后解析的q=的值
	 */
	public Text evaluate(Text url) {
		return getName(url, CommonData.QUERY_CHAR);
	}
	
	private Text getName(Text url,Text index_String) {
		if (url == null 				 
				|| index_String == null
				|| CommonData.SPLIT_CHAR == null
				|| CommonData.REPLACED_CHAR == null
				|| CommonData.REPLACE_CHAR == null) {
			return null;
		}		
		
		Text value =  getValue.evaluate(url, 
				                 CommonData.SPLIT_CHAR, 
				                 index_String);
		
		if(value == null) {
			return null;
		}
		
		if(value.toString().equals("")) {
			return new Text("");
		}
		
		String value_replaced = value.toString().replace(CommonData.REPLACED_CHAR.toString(), CommonData.REPLACE_CHAR.toString());		

		return decoder.evaluate(new Text(value_replaced));
	}
	

//	public static void main(String[] args) {
//		// TODO Auto-generated method stub
//		GetNameFromUrl test = new GetNameFromUrl();
//
//		System.out.println(test.evaluate(new Text("http://s.taobao.com/search?q=%C8%FD%B0%CB%C5%AE%C8%CB%BD%DA&keyword=&commend=all&ssid=s5-e&search_type=item&atype=&tracelog=&sourceId=tb.index"),
//				 new Text("q=")));
//	}

}
