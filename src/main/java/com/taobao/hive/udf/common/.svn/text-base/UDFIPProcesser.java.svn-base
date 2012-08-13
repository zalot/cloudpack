package com.taobao.hive.udf.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDF;

@Description(name = "UDFIPProcesser", value = "_FUNC_(ip,position) - 通过IP获取指定文件中position位对应的值\n" +
		"_FUNC_(ip,position,splitor,files) - 通过IP获取指files中position位对应的值,文件中字段的分隔符为：splitor\n")
public class UDFIPProcesser extends UDF {
	
	private String SPLITOR = ",";
	
	private String FILE_NAMES = "";
	
	private boolean initFlag = false;
	
	private int RETURN_POSITION = 1;
	
	private ArrayList<IPEntry> ipEntries = new ArrayList<IPEntry>();
	
	public String evaluate(String ip,int position) throws IOException{
		return evaluate(ip,position,FILE_NAMES,SPLITOR);
	}
	
	public String evaluate(String ip,int position, String fileName, String splitor) throws IOException{
		if (ip == null || position<1 || fileName == null || splitor == null) return null;
		if ("".equals(ip) || "".equals(fileName) || "".equals(splitor)) return null;
		RETURN_POSITION = position;
		if (!initFlag) init(fileName);
	    String value = getValueByIp(ip,position);
		return value;
	}
	
	private void init(String files) throws IOException{
		if (files == null || "".equals(files)) return;
		ipEntries.clear();
		String[] fileName = files.split(",");
		for (int i=0; i<fileName.length;i++) {
			loadFile("./" + fileName[i]);
//			loadFile("D:\\" + fileName[i]);
		}
        Collections.sort(ipEntries,new IPEntryComparator());  
		initFlag = true;
	}
	
	private void loadFile(String fileName) throws IOException{
		BufferedReader reader = null;
	    String line = null;
	    try{
		    File ipCityidFile = new File(fileName);
		    reader = new BufferedReader(new FileReader(ipCityidFile));
		    while (((line = reader.readLine()) != null)) {
			    if(line != null) {
				    addIpCityList(line);
			    }
		    }
	    } catch(Exception e){
	    	throw new IOException("load file: "+fileName+" error.\n" + e.getMessage());
	    }
	}
	
	private void addIpCityList(String ipLine) {
	    String[] ipCityStr = ipLine.split(SPLITOR);
	    if(ipCityStr.length < 2 || RETURN_POSITION > ipCityStr.length) {
	      return;
	    } else {
	      try{
	    	  IPEntry ipl = new IPEntry();
		      ipl.setStartIP(convertIP(ipCityStr[0]));
		      ipl.setEndIP(convertIP(ipCityStr[1]));
		      ipl.setValue(ipCityStr[RETURN_POSITION - 1]);
		      ipEntries.add(ipl);
	      }catch (Exception e) {
	    	  return;
	      }
	    }
	}
	
	private long convertIP(String ipStr) { //把IP地址转换成long数值
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
	
	private String getValueByIp(String ip,int position) {
	    if(ipEntries.size()==0 || position<1)
	      return null;
	    long ipLong = convertIP(ip);
	    if (ipLong == -1) return null;
	    int begin = 0;
	    int end = ipEntries.size();
	    while(begin <= end) {
	      int mid = (end + begin) / 2;
	      if(ipEntries.get(mid).getIpRange(ipLong) == 0) {
	    	  return ipEntries.get(mid).getValue();
	      } else if(ipEntries.get(mid).getIpRange(ipLong) < 0) {
	    	  end = mid - 1;
	      } else {
	    	  begin = mid + 1;
	      }
	    }
	    return null;
	}
	
	class IPEntryComparator implements Comparator<IPEntry>{   
		public int compare(IPEntry ip1,IPEntry ip2){   
		    if(ip1.getStartIP()>ip2.getStartIP())
		    	return 1;   
		    else if (ip1.getStartIP()<ip2.getStartIP())
		    	return -1;
		    else
		    	return 0;   
		}   
	}   
	
	class IPEntry {
	    private long startIP;
	    private long endIP;
	    private String value;
	    
	    public int getIpRange(long ip) {
	      if(ip >= startIP && ip <= endIP) {
	        return 0;
	      } else if(ip > endIP) {
	        return 1;
	      } else {
	        return -1;
	      }
	    }
	    
	    public long getStartIP() {
	      return startIP;
	    }
	    public long getEndIP() {
	      return endIP;
	    }
	    public void setStartIP(long startIP) {
	      this.startIP = startIP;
	    }
	    public void setEndIP(long endIP) {
	      this.endIP = endIP;
	    }
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}
	} 

}
