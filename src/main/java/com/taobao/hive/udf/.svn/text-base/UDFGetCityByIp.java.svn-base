
package com.taobao.hive.udf;
/**
 * 
 *    CREATE TEMPORARY FUNCTION getcity  AS 'com.taobao.hive.udf.UDFGetCityByIp';
 *    select getcity("121.0.29.225","city") from dual;
 *    select getcity("121.0.29.225","prov") from dual;
 *    select getcity("121.0.29.225","country") from dual;
 *    select getcity("121.0.29.225","area") from dual;
 */
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.io.Text;

public class UDFGetCityByIp extends UDF {

  private ArrayList<IPCityEntry> ipCities = new ArrayList<IPCityEntry> ();
  Text result = new Text("");
  private boolean HDFS_INIT_FLAG = false;
  
  public void initHDFS(String filename) throws IOException,HiveException{
	  HDFS_INIT_FLAG = true;
	  Path dimPath = new Path("/group/taobao/taobao/dw/dim/");
	  FileSystem fs = dimPath.getFileSystem(new Configuration());
	  Path ipCityPath = new Path("/group/taobao/taobao/dw/dim/" + filename);
	  if (!fs.exists(ipCityPath))
          throw new IOException("file not found:/group/taobao/taobao/dw/dim/" + filename);
	  FSDataInputStream in = fs.open(ipCityPath);
	  BufferedReader reader = new BufferedReader(new InputStreamReader(in));
	  String line = null;
	  ipCities.clear();
	  while (((line = reader.readLine()) != null)) {
		  try {
			  String[] list = line.trim().split(",");
			  if (list.length == 7) {
				  IPCityEntry entry = new IPCityEntry();
				  entry.setStartIP(Long.parseLong(list[0]));
				  entry.setEndIP(Long.parseLong(list[1]));
				  entry.setCountry("-".equals(list[2]) ? "" : list[2]);
				  entry.setProv("-".equals(list[3]) ? "" : list[3]);
				  entry.setCity("-".equals(list[4]) ? "" : list[4]);
				  entry.setArea("-".equals(list[5]) ? "" : list[5]);
				  entry.setSchool("-".equals(list[6]) ? "" : list[6]);
				  ipCities.add(entry);
			  }
		  }catch(Exception e){
	   		   continue;
	   	  }
      }
  }
  
  public void initLocalFS(String fileName) throws IOException{
	  HDFS_INIT_FLAG = true;
	  BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("D:\\"+fileName)));
	  String line = null;
	  ipCities.clear();
	  while (((line = reader.readLine()) != null)) {
		  try {
			  String[] list = line.trim().split(",");
			  if (list.length == 7) {
				  IPCityEntry entry = new IPCityEntry();
				  entry.setStartIP(Long.parseLong(list[0]));
				  entry.setEndIP(Long.parseLong(list[1]));
				  entry.setCountry("-".equals(list[2]) ? "" : list[2]);
				  entry.setProv("-".equals(list[3]) ? "" : list[3]);
				  entry.setCity("-".equals(list[4]) ? "" : list[4]);
				  entry.setArea("-".equals(list[5]) ? "" : list[5]);
				  entry.setSchool("-".equals(list[6]) ? "" : list[6]);
				  ipCities.add(entry);
			  }
		  }catch(Exception e){
	   		   continue;
	   	  }
      }
//	  Collections.sort(ipCities,new IPEntryComparator());
  }
  
  public Text evaluate(String ip,String flag) throws Exception {
	  return evaluate(ip,flag,"ipdata.txt");
  }
	
  public Text evaluate(String ip,String flag,String filename) throws Exception{
	  if (ip==null || flag==null || "".equals(ip) || "".equals(flag) || filename == null || "".equals(filename)){
		  result.set("");
		  return result;
	  }
	  if (!HDFS_INIT_FLAG) initHDFS(filename);
//	  if (!HDFS_INIT_FLAG) initLocalFS(filename);
	  IPCityEntry ipEntry = getCityIdByIp(ip);
	  if (ipEntry != null) {
		  if ("city".equals(flag)) {
			  result.set(ipEntry.getCity());
		  } else if ("area".equals(flag)) {
			  result.set(ipEntry.getArea());
		  } else if ("prov".equals(flag)) {
			  result.set(ipEntry.getProv());
		  } else if ("country".equals(flag)) {
			  result.set(ipEntry.getCountry());
		  } else if ("school".equals(flag)){
			  result.set(ipEntry.getSchool());
		  } else {
			  result.set("");
		  }
	  } else {
		  result.set("");
	  }
	  return result;
  }
  
  private long convertIP(String ipStr) {
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
  
//  class IPEntryComparator implements Comparator<IPCityEntry>{   
//		public int compare(IPCityEntry ip1,IPCityEntry ip2){   
//		    if(ip1.getStartIP()>ip2.getStartIP())
//		    	return 1;   
//		    else if (ip1.getStartIP()<ip2.getStartIP())
//		    	return -1;
//		    else
//		    	return 0;   
//		}   
//	}   

  private IPCityEntry getCityIdByIp(String ip) {
	  long ipLong = convertIP(ip);
	  if(ipCities.size()==0 || ipLong == -1L)
	      return null;
	  int begin = 0;
	  int end = ipCities.size()-1;
	  while(begin <= end) {
	      int mid = (end + begin) / 2;
	      if(ipCities.get(mid).getIpRange(ipLong) == 0) {
	          return ipCities.get(mid);
	      } else if(ipCities.get(mid).getIpRange(ipLong) < 0) {
	    	  end=mid-1;
	      } else {
	          begin = mid+1;
	      }
	  }
	  return null;
  }

  class IPCityEntry {
    private long startIP;
    private long endIP;
    private String country;
    private String prov;
    private String city;
    private String area;
    private String school;
    
    public String getSchool() {
		return school;
	}
	public void setSchool(String school) {
		this.school = school;
	}
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

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getProv() {
		return prov;
	}

	public void setProv(String prov) {
		this.prov = prov;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}
    
  } 
  
  public static void main(String args[]){
	   try {
//        UDFGetCity test = new UDFGetCity();
//		System.out.println(test.evaluate("221.217.209.112","city"));
//		System.out.println(test.evaluate("221.217.209.112","country"));
//		System.out.println(test.evaluate("221.217.209.112","prov"));
//		System.out.println(test.evaluate("221.217.209.112","area"));
		System.out.println("======================");
		UDFGetCityByIp test = new UDFGetCityByIp();
		System.out.println(test.evaluate("125.77.170.106","city"));
		System.out.println(test.evaluate("125.77.170.106","country"));
		System.out.println(test.evaluate("125.77.170.106","prov"));
		System.out.println(test.evaluate("125.77.170.106","area"));
		System.out.println(test.evaluate("223.245.63.33","city"));
		System.out.println(test.evaluate("223.245.63.33","country"));
		System.out.println(test.evaluate("223.245.63.33","prov"));
		System.out.println(test.evaluate("223.245.63.33","area"));
		System.out.println(test.evaluate("223.245.63.33","school"));
		System.out.println("======================");
//		test = new UDFGetCity();
//		System.out.println(test.evaluate("1.184.123.123","city","ipdata_cernet.txt"));
//		System.out.println(test.evaluate("1.184.123.123","country","ipdata_cernet.txt"));
//		System.out.println(test.evaluate("1.184.123.123","prov","ipdata_cernet.txt"));
//		System.out.println(test.evaluate("1.184.123.123","area","ipdata_cernet.txt"));
//		System.out.println(test.evaluate("1.184.123.123","school","ipdata_cernet.txt"));
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
  }
}

