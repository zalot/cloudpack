package com.taobao.hive.udf.search;

import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import com.taobao.hive.udf.UDFGetValueFromRefer;
import com.taobao.hive.udf.search.CommonData;

/**
 * 对取值为null或者trim后为空""的值替换为指定值
 * 
 * @author 宋智
 * @created 2011-03-10
 */
public class ReplaceNullAndEmpty extends UDF{
	
	private UDFGetValueFromRefer getValue = new UDFGetValueFromRefer();
	private GetValueFromPre getPreValue = new GetValueFromPre();
	private GetValueFromSplit getSplitValue = new GetValueFromSplit();
	
	//一级参数解析
	/**
	 * @param refer 日志中的refer或者url
	 * @param index_String 第一级解析的值
	 * @param replace_String 替换的值
	 * @return 第一级解析后的值为null或者trim后为空，则返回替换的值，否则返回trim后的值
	 */
	public Text evaluate(Text refer,Text index_String, Text replace_String) {
		Text value = getValue.evaluate(refer, CommonData.SPLIT_CHAR, index_String);
		if(value == null || value.toString().trim().equals("")) {
			return replace_String;
		}
		
		return new Text(value.toString().trim());
	}
	
	//二级参数解析
	/**
	 * @param refer 日志中的refer
	 * @param index_String_1 第一级解析的值，一般为pre=或者searchurl=
	 * @param index_String_2 第二级解析的值
	 * @param replace_String 替换的值
	 * @return 第二级解析后的值为null或者trim后为空，则返回替换的值，否则返回trim后的值
	 */
	public Text evaluate(Text refer,Text index_String_1, Text index_String_2, Text replace_String) {
		Text value = getPreValue.evaluate(refer, index_String_1, index_String_2);
		if(value == null || value.toString().trim().equals("")) {
			return replace_String;
		}
		
		return new Text(value.toString().trim());
	}
	
	//split取值型
	/**
	 * @param refer 日志中的refer或者url
	 * @param index_String 第一级解析的值
	 * @param split 分隔符
	 * @param index 分隔的索引
	 * @param replace_String 替换的值
	 * @return split解析后的值为null或者trim后为空，则返回替换的值，否则返回trim后的值
	 */
	public Text evaluate(Text refer,Text index_String, Text split, IntWritable index, Text replace_String) {
		Text value = getSplitValue.evaluate(refer, index_String, split, index);
		
		if(value == null || value.toString().trim().equals("")){
			return replace_String;
		}
		
		return new Text(value.toString().trim());
	}
	
	
//	public static void main(String[] args) {
//		// TODO Auto-generated method stub
//		ReplaceNullAndEmpty test = new ReplaceNullAndEmpty();
//		System.out.println(test.evaluate(new Text("/search?t=53853&searchurl=http%3A%2F%2Fs.taobao.com%2Fsearch%3Fq%3D%25D2%25C6%25B6%25AF%25D3%25B2%25C5%25CC%25BA%25D0%26keyword%3D%26commend%3Dall%26ssid%3Ds5-e%26search_type%3Ditem%26atype%3D%26tracelog%3D%26sourceId%3Dtb.index&at_lflog=1-1-0-0-1-29659-2-all&at_bucketid=2&at_alitrackid=www.taobao.com&price_interval=1"),
//				 new Text("at_alitrackid="), new Text("-1")));
//		
//		System.out.println(test.evaluate(new Text("/search?t=53853&searchurl=http%3A%2F%2Fs.taobao.com%2Fsearch%3Fq%3D%25D2%25C6%25B6%25AF%25D3%25B2%25C5%25CC%25BA%25D0%26keyword%3D%26commend%3Dall%26ssid%3Ds5-e%26search_type%3Ditem%26atype%3D%26tracelog%3D%26sourceId%3Dtb.index&at_lflog=1-1-0-0-1-29659-2-all&at_bucketid=2&at_alitrackid=www.taobao.com&price_interval=1"),
//				new Text("searchurl="), null, new Text("11")));		
//	}

}
