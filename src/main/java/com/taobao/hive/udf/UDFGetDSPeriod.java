package com.taobao.hive.udf;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.ql.exec.UDF;

public class UDFGetDSPeriod extends UDF {
	
	private Map<String,String[]> periods;
	private boolean initFlag;
	private String[] columns;
	
	public UDFGetDSPeriod() {
		periods = new HashMap<String,String[]>();
		initFlag = false;
		columns = new String[]{"period_name","period_type_id","start_date"
				,"end_date","period_label_id","period_label_name"};
	}
	
	public String evaluate(String periodId, String propName) throws IOException{
		if (periodId == null || propName == null || "".equals(periodId) || "".equals(propName)) return null;
		
		if (!initFlag) init();
		if (periods.get(periodId) == null) return null;
		String[] values = periods.get(periodId);
		for (int i=0;i<columns.length;i++) {
			if (propName.equals(columns[i])) return values[i];
		}
		return null;
	}

	private void init() throws IOException{
		initFlag = true;
		Path dimPath = new Path("/group/taobao/taobao/dw/fact/");
		FileSystem fs = dimPath.getFileSystem(new Configuration());
		FSDataInputStream in;
		String ruleFile = null;
		Path inputPath = null;
		
		ruleFile = "/group/taobao/taobao/dw/dim/ds_ddf_period/ds_ddf_period";
		inputPath = new Path(ruleFile);
		if (!fs.exists(inputPath))
			throw new IOException("/group/taobao/taobao/dw/dim/ds_ddf_period/ds_ddf_period not found");
		if (!fs.isFile(inputPath))
			throw new IOException("Input should be a file (period.txt)");
		in = fs.open(inputPath);
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		
//		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("D:\\ds_ddf_period")));
		try {
			String line = null;
			while((line=reader.readLine()) != null) {
				String[] items = line.split("\t");
				String[] p = new String[6];
				if (items.length >=12) {
					p[0] = items[2]; //period_name
					p[1] = items[3]; //period_type_id
					p[2] = items[5]; //period_start_date
					p[3] = items[6]; //period_end_date
				    p[4] = items[9]; //period_label_id
				    p[5] = items[11];//period_label_name
				    periods.put(items[0], p);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			reader.close();
		}
	}
	
	public static void main(String[] args) {
		UDFGetDSPeriod udf = new UDFGetDSPeriod();
		try {
			System.out.println(udf.evaluate("20162391", "period_name"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
