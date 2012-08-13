package com.taobao.hive.udf.search;

import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.Text;
import com.taobao.hive.udf.UDFGetValueFromRefer;
import com.taobao.hive.udf.SearchURLDecode;
import com.taobao.hive.udf.search.CommonData;

/**
 * 从refer中解析值
 * 
 * @author 宋智
 * @created 2011-03-10
 */
public class GetValueFromPre extends UDF{

	private UDFGetValueFromRefer getValue = new UDFGetValueFromRefer();
	private SearchURLDecode decoder = new SearchURLDecode();
	
	public GetValueFromPre() {
	}
	
	/**
	 * @param refer 日志中的refer
	 * @param index_String_1 第一级解析的值，一般为pre=或者searchurl=
	 * @param index_String_2 第二级解析的值
	 * @return 解析的值
	 */
	public Text evaluate(Text refer,Text index_String_1, Text index_String_2) {
		if (refer == null				 
				|| index_String_1 == null 
				|| index_String_2 == null
				|| CommonData.SPLIT_CHAR == null
				|| CommonData.REPLACED_CHAR == null
				|| CommonData.REPLACE_CHAR == null) {
			return null;
		}		
		
		Text value =  getValue.evaluate(
				                 decoder.evaluate(getValue.evaluate(refer, CommonData.SPLIT_CHAR, index_String_1)), 
				                 CommonData.SPLIT_CHAR, 
				                 index_String_2);
		if(value == null) {
			return null;
		}

		return new Text(value.toString());
	}	
	
//	public static void main(String[] args) {
//		// TODO Auto-generated method stub
//		GetValueFromPre test = new GetValueFromPre();
//		System.out.println(test.evaluate(new Text("/search?t=53853&searchurl=http%3A%2F%2Fs.taobao.com%2Fsearch%3Fq%3D%25D2%25C6%25B6%25AF%25D3%25B2%25C5%25CC%25BA%25D0%26keyword%3D111%26commend%3Dall%26ssid%3Ds5-e%26search_type%3Ditem%26atype%3D%26tracelog%3D%26sourceId%3D21&at_lflog=1-1-0-0-1-29659-2-all&at_bucketid=2&at_alitrackid=www.taobao.com&price_interval=1"),
//				 new Text("searchurl="),new Text("")));		
//	}
}
