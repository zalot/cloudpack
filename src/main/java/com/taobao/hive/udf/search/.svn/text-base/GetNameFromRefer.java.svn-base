package com.taobao.hive.udf.search;

import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.Text;
import com.taobao.hive.udf.UDFGetValueFromRefer;
import com.taobao.hive.udf.SearchURLDecode;
import com.taobao.hive.udf.search.CommonData;

/**
 * 从refer中解析中文（如query等）
 * 
 * @author 宋智
 * @created 2011-03-10
 */
public class GetNameFromRefer extends UDF{


	private UDFGetValueFromRefer getValue = new UDFGetValueFromRefer();
	private SearchURLDecode decoder = new SearchURLDecode();
	
	public GetNameFromRefer() {
	}
	
	/**
	 * @param refer 日志中的refer
	 * @param index_String_1 第一级解析的值，一般为pre=或者searchurl=
	 * @param index_String_2 第一级解析的值
	 * @return 解码后解析的值
	 */
	public Text evaluate(Text refer,Text index_String_1, Text index_String_2) {
		return getName(refer, index_String_1, index_String_2);
	}
	
	/**
	 * @param refer 日志中的refer
	 * @param index_String_1 第一级解析的值，一般为pre=或者searchurl=	
	 * @return 解码后解析的q=的值
	 */
	public Text evaluate(Text refer,Text index_String_1) {
		return getName(refer, index_String_1, CommonData.QUERY_CHAR);
	}
	
	/**
	 * @param refer 日志中的refer
	 * @param index_String_1 第一级解析的值，一般为pre=或者searchurl=
	 * @param index_String_2 第二级解析的值
	 * @return 解码后解析的值
	 */
	private Text getName(Text refer,Text index_String_1, Text index_String_2) {
		if (refer == null 				 
				|| index_String_1 == null 
				|| index_String_2 == null
				|| CommonData.SPLIT_CHAR == null
				|| CommonData.REPLACED_CHAR == null
				|| CommonData.REPLACE_CHAR == null) {
			return null;
		}		
		
		//先取pre或者searchurl=的值，再解码，然后取值
		Text value =  getValue.evaluate(
				                 decoder.evaluate(getValue.evaluate(refer, CommonData.SPLIT_CHAR, index_String_1)), 
				                 CommonData.SPLIT_CHAR, 
				                 index_String_2);
		
		if(value == null) {
			return null;
		}
		
		if(value.toString().equals("")) {
			return new Text("");
		}
		//用%替换
		String value_replaced = value.toString().replace(CommonData.REPLACED_CHAR.toString(), CommonData.REPLACE_CHAR.toString());		
        
		//decode之后空格会被trim
		return decoder.evaluate(new Text(value_replaced));
	}
	
//	public static void main(String[] args) {
//		// TODO Auto-generated method stub
//		GetNameFromRefer test = new GetNameFromRefer();
//
//		System.out.println(test.evaluate(new Text("/search?t=53853&searchurl=http%3A%2F%2Fs.taobao.com%2Fsearch%3Fq%3D\\xD2\\xC6\\xB6\\xAF\\xD3\\xB2\\xC5\\xCC\\xBA\\xD0%26keyword%3D%26commend%3Dall%26ssid%3Ds5-e%26search_type%3Ditem%26atype%3D%26tracelog%3D%26sourceId%3Dtb.index&at_lflog=1-1-0-0-1-29659-2-all&at_bucketid=2&at_alitrackid=www.taobao.com&price_interval=1"),
//				 new Text("searchurl=")));	
//	}
}
