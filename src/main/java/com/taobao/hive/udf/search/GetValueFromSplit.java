package com.taobao.hive.udf.search;

import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import com.taobao.hive.udf.UDFGetValueFromRefer;
import com.taobao.hive.udf.search.CommonData;


/**
 * 从url或者refer中解析特定分隔符分隔出的值
 * 
 * @author 宋智
 * @created 2011-03-10
 */
public class GetValueFromSplit extends UDF{

	private UDFGetValueFromRefer getValue = new UDFGetValueFromRefer();
	
	/**
	 * @param refer 日志中的refer或者url
	 * @param index_String 第一级解析的值
	 * @param split 分隔符
	 * @param index 分隔的索引
	 * @return 分隔索引
	 */
	public Text evaluate(Text refer,Text index_String, Text split, IntWritable index) {
		
		if(refer == null 
				|| index_String == null
				|| split == null				
				|| index == null
				|| CommonData.SPLIT_CHAR == null){
			return null;
		}	
		
		if(index.get() < 0){
			return null;
		}
		
		String returnValue = "";
		//从refer中取值
		Text value = getValue.evaluate(refer, CommonData.SPLIT_CHAR, new Text(index_String));
		
		if(value == null) {
			return null;
		}
		
		String valueToString = value.toString().trim();
		
		String[] strList = valueToString.split(split.toString());
		
		if(strList.length > index.get()) {
		    returnValue = strList[index.get()];//index从0开始
		} else {	
		    return null;
		}
		
		return new Text(returnValue);
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		GetValueFromSplit test = new GetValueFromSplit();
//		System.out.println(test.evaluate(new Text("/search?t=53853&searchurl=http%3A%2F%2Fs.taobao.com%2Fsearch%3Fq%3D%25D2%25C6%25B6%25AF%25D3%25B2%25C5%25CC%25BA%25D0%26keyword%3D%26commend%3Dall%26ssid%3Ds5-e%26search_type%3Ditem%26atype%3D%26tracelog%3D%26sourceId%3Dtb.index&at_lflog=1-1-0-0-1-29659-2-all&at_bucketid=2&at_alitrackid=www.taobao.com&price_interval=1"),
//				 new Text("at_lflog="),new Text("-"), new IntWritable(0)));
		System.out.println(test.evaluate(new Text("http://8.etao.com/thread_detail.htm?bar_id=90077&thread_id=1.2.31&author_id=0&page=3"),
				 new Text("thread_id="),new Text("--"), new IntWritable(0)));
	}
}
