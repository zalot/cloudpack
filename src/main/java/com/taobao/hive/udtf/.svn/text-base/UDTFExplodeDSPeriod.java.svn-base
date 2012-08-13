package com.taobao.hive.udtf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.exec.UDFArgumentLengthException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDTF;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;

public class UDTFExplodeDSPeriod extends GenericUDTF {
	
	private Map<String,List<Period>> periods;
	private boolean init = false;
	private SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd");
	
	@Override
	public StructObjectInspector initialize(ObjectInspector[] args)
			throws UDFArgumentException {
		// TODO Auto-generated method stub
		if (args.length !=1 && args.length != 3 && args.length != 4) {
		    throw new UDFArgumentLengthException("UDTFExplodeDSPeriod takes one(three) argument(s)");
		}
		
		if (args.length ==1 && args[0].getCategory() != ObjectInspector.Category.PRIMITIVE) {
			throw new UDFArgumentException("UDTFExplodeDSPeriod takes string as a parameter");
		}
		
		if (args.length >=3) {
			if ((args[0].getCategory() != ObjectInspector.Category.PRIMITIVE ||
				args[1].getCategory() != ObjectInspector.Category.PRIMITIVE || 
				args[2].getCategory() != ObjectInspector.Category.PRIMITIVE)) {
				throw new UDFArgumentException("UDTFExplodeDSPeriod takes string as a parameter");
			}
		}
		
		ArrayList<String> fieldNames = new ArrayList<String>();
	    ArrayList<ObjectInspector> fieldOIs = new ArrayList<ObjectInspector>();
	    fieldNames.add("col1");
	    fieldOIs.add(PrimitiveObjectInspectorFactory.javaStringObjectInspector);
	    fieldNames.add("col2");
	    fieldOIs.add(PrimitiveObjectInspectorFactory.javaStringObjectInspector);
	    fieldNames.add("col3");
	    fieldOIs.add(PrimitiveObjectInspectorFactory.javaStringObjectInspector);
	    
	    return ObjectInspectorFactory.getStandardStructObjectInspector(fieldNames,fieldOIs);
	}

	@Override
	public void process(Object[] args) throws HiveException {
		// TODO Auto-generated method stub
		if (args == null || (args.length != 1 && args.length != 3 && args.length != 4)) return;
		//init period
		if (!init) {
			init = true;
			try {
				if (args.length == 4) {
					buildPeriod(args[1].toString(),args[2].toString(),true);
				} else {
					buildPeriod(null,null,false);
				}
			} catch (Exception e) {
				throw new HiveException("/group/taobao/taobao/dw/dim/ds_ddf_period/ds_ddf_period not found.");
			}
		}
		
		String bizDate = args[0].toString();
		List<Period> days = periods.get(bizDate);
		if (days != null && days.size()>0) {
			for (Period p : days) {
				if (args.length == 3) {
					if (p.getEndDate().equals(args[1].toString()) 
							&& p.getPeriodLabel().equals(args[2].toString())) {
						forward(new String[]{p.getPeriodID(),p.getPeriodCode(),p.getPeriodName()});
//						System.out.println(p.getPeriodID()+","+p.getPeriodCode()+","+p.getPeriodName());						
					}
				} else {
					forward(new String[]{p.getPeriodID(),p.getPeriodCode(),p.getPeriodName()});
//					System.out.println(p.getPeriodID()+","+p.getPeriodCode()+","+p.getPeriodName());
				}
			}
		} else {
			forward(new String[3]);
		}
	}

	@Override
	public void close() throws HiveException {
		// TODO Auto-generated method stub

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		UDTFExplodeDSPeriod udtf = new UDTFExplodeDSPeriod();
		try {
			udtf.process(new String[]{"20111209"});
			System.out.println("---------------------------------------");
			udtf.buildPeriod("20111209","day",false);
			udtf.process(new String[]{"20111209","20111209","day"});
			System.out.println("---------------------------------------");
			udtf.buildPeriod("20111209","day",true);
			udtf.process(new String[]{"20111209","20111209","day"});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void buildPeriod(String endDate,String periodLabel,boolean endFlag) throws Exception{
		periods = new HashMap<String,List<Period>>();
//		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("D:\\ds_dff_period.txt")));
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
		
		try {
			String line = null;
			while((line=reader.readLine()) != null) {
				String[] items = line.split("\t");
				if (items.length >=10) {
					if (endFlag) {
						if (endDate != null && !"".equals(endDate)) {
							if (!endDate.equals(items[6])) continue;
						}
						if (periodLabel != null && !"".equals(periodLabel)) {
							if (!periodLabel.equals(items[9])) continue;
						}
					}
					List<String> days = explodeDate(items[5],items[6]);
					if(days != null && days.size() > 0) {
						for (String date : days) {
							if (periods.get(date) == null) {
								List<Period> pd = new ArrayList<Period>();
								Period p = new Period();
								p.setPeriodCode(items[1]);
								p.setPeriodID(items[0]);
								p.setPeriodName(items[2]);
								p.setEndDate(items[6]);
								p.setPeriodLabel(items[9]);
								pd.add(p);
								periods.put(date, pd);
							} else {
								Period p = new Period();
								p.setPeriodCode(items[1]);
								p.setPeriodID(items[0]);
								p.setPeriodName(items[2]);
								p.setEndDate(items[6]);
								p.setPeriodLabel(items[9]);
								periods.get(date).add(p);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			reader.close();
		}
	}
	
	private List<String> explodeDate(String startDate, String endDate) {
		Date start;
		Date end;
		try {
			start = dateFormatter.parse(startDate);
			end = dateFormatter.parse(endDate);
		}catch (Exception e) {
			return null;
		}
		List<String> lDate = new ArrayList<String>();    
	    lDate.add(dateFormatter.format(start));  
	    if (!startDate.equals(endDate)) {
	    	lDate.add(dateFormatter.format(end));
	    }
        Calendar cal = Calendar.getInstance();  
        //使用给定的 Date 设置此 Calendar 的时间 
        cal.setTime(start);    
    
        boolean bContinue = true;    
    
        while (bContinue) {   
         //根据日历的规则，为给定的日历字段添加或减去指定的时间量 
             cal.add(Calendar.DAY_OF_MONTH, 1);    
                // 测试此日期是否在指定日期之后 
             if (end.after(cal.getTime())) {    
                 lDate.add(dateFormatter.format(cal.getTime()));    
             } else {    
                 break;    
             }    
        }    
        return lDate;  
	}
	
	class Period {
		private String periodID;
		private String periodName;
		private String periodCode;
		private String endDate;
		private String periodLabel;
		
		public String getPeriodID() {
			return periodID;
		}
		public void setPeriodID(String periodID) {
			this.periodID = periodID;
		}
		public String getPeriodName() {
			return periodName;
		}
		public void setPeriodName(String periodName) {
			this.periodName = periodName;
		}
		public String getPeriodCode() {
			return periodCode;
		}
		public void setPeriodCode(String periodCode) {
			this.periodCode = periodCode;
		}
		public String getEndDate() {
			return endDate;
		}
		public void setEndDate(String endDate) {
			this.endDate = endDate;
		}
		public String getPeriodLabel() {
			return periodLabel;
		}
		public void setPeriodLabel(String periodLabel) {
			this.periodLabel = periodLabel;
		}
		
	}

}
