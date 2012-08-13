package com.taobao.hive.udf;

import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.hive.serde2.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;

public class UDFFormat extends UDF {
	
	public Text evaluate(DoubleWritable d, Text format){
		if (d == null)
			return null;
		return new Text(String.format(format.toString(), d.get()));
	}
	
	public Text evaluate(LongWritable l, Text format){
		if (l == null)
			return null;
		return new Text(String.format(format.toString(), l.get()));
	}
	
	public Text evaluate(IntWritable i, Text format){
		if (i == null)
			return null;
		return new Text(String.format(format.toString(), i.get()));
	}
	
	public Text evaluate(DoubleWritable d){
		return evaluate(d, new Text("%f"));
	}

	public Text evaluate(LongWritable l){
		return evaluate(l, new Text("%d"));
	}
	
	public Text evaluate(IntWritable i){
		return evaluate(i, new Text("%d"));
	}
	

}

