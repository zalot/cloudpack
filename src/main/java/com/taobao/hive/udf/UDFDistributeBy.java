package com.taobao.hive.udf;

import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.Text;

//CREATE TEMPORARY FUNCTION distribute_by AS 'com.taobao.hive.udf.UDFDistributeBy';
public class UDFDistributeBy extends UDF {
	
	private Text result = new Text();

	public Text evaluate(String key,int number) {
		if (key == null || "".equals(key)) {
			result.set("0");
			return result;
		}
		if (number == 0) number = 2;
		int ret = (key.hashCode()&Integer.MAX_VALUE)%number;
		result.set(ret+"");
		return result;
	}
	
	public Text evaluate(String key) {
		return evaluate(key,2);
	}
}
