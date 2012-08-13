/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.taobao.hive.udf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;

/*
 *add file hdfs://hdpnn:9000/group/taobao/taobao/dw/dim/ip_cityid.txt;
 *CREATE TEMPORARY FUNCTION get_cityid AS 'com.taobao.hive.udf.UDFGetIPCity';
 */

@Description(name = "UDFGetIPCity", value = "_FUNC_(ip) - Returns the city_id of the ip")
public class UDFGetIPCity extends UDF {

  private ArrayList<IpCityList> ipCities = new ArrayList<IpCityList>();
  
  IntWritable result = new IntWritable();
  
  private static final String ip_cityid_files = "./ip_cityid.txt";
//  private static final String ip_cityid_files = "D:\\ip_cityid.txt";//测试语句
//  private static final String ip_cityid_files = "C:\\ip_cityid.txt";//测试语句

  public UDFGetIPCity() {}
  
  private boolean isInited = false;
  
  private boolean init() throws Exception{
	BufferedReader reader = null;
    String line = null;
    File ipCityidFile = new File(ip_cityid_files);
    reader = new BufferedReader(new FileReader(ipCityidFile));
    while (((line = reader.readLine()) != null)) {
	    if(line != null) {
		    addIpCityList(line);
	    }
    }
      return true;
  }

  public IntWritable evaluate(Text s) throws Exception{
    if (s == null) {
      return null;
    }
    if (!isInited) {
    	isInited = init();
    	if (!isInited) {
    		//init ip_city.txt failed
    		return null;
    	}
    }
    String str = s.toString();
    int cityID = getCityIdByIp(str);
    if(cityID == -1) {
      return null;
    }
    result.set(cityID);
    return result;
  }

  private void addIpCityList(String ipCityLine) {
    String[] ipCityStr = ipCityLine.split("\"");
    if(ipCityStr.length < 3) {
      return;
    } else {
      try{
	      IpCityList ipl = new IpCityList();
	      ipl.setStartIP(convertIP(ipCityStr[0]));
	      ipl.setEndIP(convertIP(ipCityStr[1]));
	      ipl.setCityID(Integer.parseInt(ipCityStr[2]));
	      ipCities.add(ipl);
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

  private int getCityIdByIp(String ip) {
    long ipLong = convertIP(ip);
    if(ipCities.size()==0 || ipLong == -1L)
      return -1;
    int begin = 0;
    int end = ipCities.size();
    while(begin < end) {
      int mid = (end + begin) / 2;
      if(ipCities.get(mid).getIpRange(ipLong) == 0) {
        return ipCities.get(mid).getCityID();
      } else if(ipCities.get(mid).getIpRange(ipLong) < 0) {
        end = mid - 1;
      } else {
        begin = mid + 1;
      }
    }
    return -1;
  }

  static class IpCityList {
    private long startIP;
    private long endIP;
    private int CityID;
    
    public int getIpRange(long ip) {
      if(ip >= startIP && ip <= endIP) {
        return 0;
      } else if(ip > endIP) {
        return 1;
      } else {
        return -1;
      }
    }

    public int getCityID() {
      return CityID;
    }

    public long getStartIP() {
      return startIP;
    }

    public long getEndIP() {
      return endIP;
    }

    public void setCityID(int cityID) {
      CityID = cityID;
    }

    public void setStartIP(long startIP) {
      this.startIP = startIP;
    }

    public void setEndIP(long endIP) {
      this.endIP = endIP;
    }
  } 
  
  public static void main(String args[]){
	  UDFGetIPCity test = new UDFGetIPCity();
	   try {
		System.out.println(test.evaluate(new Text("1.52.0.0")));
		System.out.println(test.evaluate(new Text("1.52.1.254")));
		System.out.println(test.evaluate(new Text("1.52.255.255")));
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
  }
}
