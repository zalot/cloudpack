package com.taobao.hive.udf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.Text;

public class UDFPeriodID extends UDF {
	
	private boolean initFlag = false;

	private Map<String,String[]> periods;
	
	private Text result ;
	
	public UDFPeriodID() {
		periods = new HashMap<String,String[]>();
		initFlag = false;
		result = new Text();
	}
	
	/**
	 * 默认返回periodId
	 * @param endDate
	 * @param periodLabelId
	 * @return
	 * @throws IOException
	 */
	public Text evaluate(String endDate, String periodLabelId) throws IOException{
		return evaluate(endDate,periodLabelId,0);
	}
	
	/**
	 * type=0 返回 periodId
	 * type=1 返回 periodCode
	 * type=2 返回 periodName
	 * type=3 返回 periodtypeId
	 * type=4 返回 periodtypeName
	 * type=5 返回 periodstart_date
	 * type=6 返回 periodtypeend_date
	 * type=7 返回 period_label_id
	 * type=8 返回 period_label_name
	 * @param endDate
	 * @param periodLabelId
	 * @param type
	 * @return
	 * @throws IOException
	 */
	public Text evaluate(String endDate, String periodLabelId, int type) throws IOException{
		
		if (endDate == null || periodLabelId == null) return null;
		if (type<0 || type>8) return null;
		
		if (!initFlag) {
			init();
			initFlag = true;
		}
		
		
		String key = formatEndDate(endDate) + "," + periodLabelId;
		String[] period = periods.get(key);
		if (period == null || period.length != 9) return null;
		result.set(period[type]);
		return result;
	}
	
	public String formatEndDate(String endDate) {
		if (endDate.length()<=8) return endDate;
		
		endDate = endDate.replace("-", "");
		if (endDate.length()>8) endDate = endDate.substring(0,8);
		
		return endDate;
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
				String[] p = new String[9];
				if (items.length >=12) {
					p[0] = items[0]; //period_id
					p[1] = items[1]; //period_code
					p[2] = items[2]; //period_name
					p[3] = items[3]; //period_type_id
					p[4] = items[4]; //period_type_name
					p[5] = items[5]; //start_date
					p[6] = items[6]; //end_date
				    p[7] = items[9]; //period_label_id
				    p[8] = items[11];//period_label_name
				    periods.put(items[6]+","+items[9], p);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			reader.close();
		}
	}
	
	public static void main(String[] args) {
		UDFPeriodID udf = new UDFPeriodID();
		try {
			System.out.println(udf.formatEndDate("20120328"));
			System.out.println(udf.formatEndDate("2012-03-28"));
			System.out.println(udf.formatEndDate("20120328000"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
