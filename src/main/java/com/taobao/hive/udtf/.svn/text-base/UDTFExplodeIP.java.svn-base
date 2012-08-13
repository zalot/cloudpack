package com.taobao.hive.udtf;

import java.util.ArrayList;

import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.exec.UDFArgumentLengthException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDTF;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;

public class UDTFExplodeIP extends GenericUDTF{
	
	StringBuilder sb = new StringBuilder("");
	
	@Override
	public void close() throws HiveException {
		// TODO Auto-generated method stub	
	}

	@Override
	public StructObjectInspector initialize(ObjectInspector[] args)
			throws UDFArgumentException {
		if (args.length != 2) {
		    throw new UDFArgumentLengthException("UDTFExplodeKeyValue takes two arguments");
		}
		if (args[0].getCategory() != ObjectInspector.Category.PRIMITIVE || args[1].getCategory() != ObjectInspector.Category.PRIMITIVE) {
			throw new UDFArgumentException("UDTFExplodeIP takes string as a parameter");
		}
		
		ArrayList<String> fieldNames = new ArrayList<String>();
	    ArrayList<ObjectInspector> fieldOIs = new ArrayList<ObjectInspector>();
	    fieldNames.add("col1");
	    fieldOIs.add(PrimitiveObjectInspectorFactory.javaStringObjectInspector);
	    return ObjectInspectorFactory.getStandardStructObjectInspector(fieldNames,fieldOIs);
	}

	@Override
	public void process(Object[] args) throws HiveException {
		if(args==null || args.length != 2){
			forward(new String[1]);
			return;
		}
		long startIp = convertIP2Long(args[0].toString());
		long endIp = convertIP2Long(args[1].toString());
		if (startIp>0L && endIp>0L && startIp<endIp) {
			for (long i=startIp; i<=endIp; i++) {
				forward(new String[]{convertIP2Str(i)});
			}
		}
	}
	
	public long convertIP2Long(String ipStr) { //把IP地址转换成long数值
		try{
			long[] ipLongs=new long[4];
		    String[] ips = ipStr.split("\\.");
		    if(ips.length != 4) {
		      return -1L;
		    } else {
		      for(int i=0; i<ips.length; i++) {
		        long ipLong = Long.parseLong(ips[i]);
		        if(ipLong > 255L) {
		          return -1L;
		        }
		        ipLongs[i] = ipLong;
		      }
		    }
		    return (ipLongs[0]<<24)+(ipLongs[1]<<16)+(ipLongs[2]<<8)+ipLongs[3]; //ip1*256*256*256+ip2*256*256+ip3*256+ip4
		} catch (Exception e) {
			return -1L;
		}
	  }
	
	public String convertIP2Str(long ip) {
		try {
			sb.delete(0, sb.length());
		    sb.append(String.valueOf(ip >>> 24));// 直接右移24位
		    sb.append(".");
		    sb.append(String.valueOf((ip & 0x00ffffff) >>> 16)); // 将高8位置0，然后右移16位
		    sb.append(".");
		    sb.append(String.valueOf((ip & 0x0000ffff) >>> 8)); 
		    sb.append(".");
		    sb.append(String.valueOf(ip & 0x000000ff));
		    return sb.toString();
		} catch (Exception e) {
			return "";
		}
	}
	
	
	public static void main(String[] args) {
		UDTFExplodeIP ip = new UDTFExplodeIP();
		String start_str = "1.9.0.0";
		String end_str = "1.9.1.255"; 
		long startIp = ip.convertIP2Long(start_str);
		long endIp = ip.convertIP2Long(end_str);
		System.out.println(Integer.valueOf("1090000",16).toString());
		System.out.println(Long.toHexString(startIp));
		System.out.println(startIp + "--" + endIp);
		System.out.println(ip.convertIP2Str(new Long(Integer.valueOf("1090000",16).toString())));//119.114.98.181
//		if (startIp>0L && endIp>0L && startIp<endIp) {
//			for (long i=startIp; i<=endIp; i++) {
//				System.out.println(ip.convertIP2Str(i));
//			}
//		}
	}
//CREATE TEMPORARY FUNCTION explode_ip AS 'com.taobao.hive.udtf.UDTFExplodeIP';
}
