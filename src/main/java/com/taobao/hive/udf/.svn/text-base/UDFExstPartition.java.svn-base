package com.taobao.hive.udf;

import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.BooleanWritable;

//CREATE TEMPORARY FUNCTION exst_pt AS 'com.taobao.hive.udf.UDFExstPartition';

@Description(name = "exst_pt", value = "_FUNC_(pt_start,pt_end,start_date[,end_date]) \n" +
		"start_date[,end_date] must be 'yyyyMMdd' or 'yyyyMMddHHmmss'")
public class UDFExstPartition extends UDF{
	
	static final BooleanWritable falseWritable = new BooleanWritable(false);
	static final BooleanWritable trueWritable = new BooleanWritable(true);
	
	public BooleanWritable evaluate(String pt_start, String pt_end,
			String start) {
		return evaluate(pt_start,pt_end,start,start);
	}
	
	public BooleanWritable evaluate(String pt_start, String pt_end,
			String start, String end) {
		if (pt_start == null || pt_end == null || start == null || end == null || pt_end.length()<7) {
			return falseWritable;
		}
		start = fomartDate(start);
		end = fomartDate(end);
		if(end == null || start == null)  return falseWritable;
		// Deal with _INFINITY ...
		if (pt_end.charAt(6) == '_') {
			if ( !pt_end.substring(0, 6).equals(end.substring(0, 6)))
				return falseWritable;
		}
		// pt_start <= end
		if (pt_start.compareTo(end) > 0) {
			return falseWritable;
		}
		// pt_end > start
		if (pt_end.compareTo(start) <= 0) {
			return falseWritable;
		}
		return trueWritable;
	}
	
	private String fomartDate(String date){
		if(date.length()<8) return null;
		date = date.length() == 8 ? date : date.substring(0,8);
		return date;
	}
	
	public static void main(String[] args) {
		UDFExstPartition udf = new UDFExstPartition();
		System.out.println(udf.evaluate("20110701", "20110712", "20110712"));
		System.out.println(udf.evaluate("20110701", "20110712", "20110712000000"));
		System.out.println(udf.evaluate("20110701", "201107_INFINITY", "20110712000000"));
	}
}
