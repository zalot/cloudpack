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


import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.Text;


//CREATE TEMPORARY FUNCTION getipschool  AS 'com.taobao.hive.udf.UDFGetIPSchool';
public class UDFGetIPSchool extends UDF {
  private static Log LOG = LogFactory.getLog(UDFGetIPSchool.class.getName());

  private ArrayList<IpCityList> ipCities = new ArrayList<IpCityList> (42631);
  
  Text result = new Text();

  public UDFGetIPSchool() {
	  ipCities.clear();
	  addIpCityLine("202.112.64.0\"202.112.79.0\"2642");
	  addIpCityLine("202.112.80.0\"202.112.95.0\"2695");
	  addIpCityLine("202.112.96.0\"202.112.111.0\"3502");
	  addIpCityLine("202.112.112.0\"202.112.127.0\"74363");
	  addIpCityLine("202.112.128.0\"202.112.143.0\"2645");
	  addIpCityLine("202.112.144.0\"202.112.159.0\"2655");
	  addIpCityLine("202.112.160.0\"202.112.175.0\"74359");
	  addIpCityLine("202.112.176.0\"202.112.191.0\"79353");
	  addIpCityLine("202.112.192.0\"202.112.207.0\"3480");
	  addIpCityLine("202.112.224.0\"202.112.227.0\"2665");
	  addIpCityLine("202.113.0.0\"202.113.15.0\"55829");
	  addIpCityLine("202.113.16.0\"202.113.31.0\"39973");
	  addIpCityLine("202.113.112.0\"202.113.127.0\"19817");
	  addIpCityLine("202.204.16.0\"202.204.23.0\"79378");
	  addIpCityLine("202.204.32.0\"202.204.47.0\"3509");
	  addIpCityLine("202.204.48.0\"202.204.63.0\"2668");
	  addIpCityLine("202.204.64.0\"202.204.79.0\"3491");
	  addIpCityLine("202.204.80.0\"202.204.95.0\"2674");
	  addIpCityLine("202.204.96.0\"202.204.111.0\"74289");
	  addIpCityLine("202.204.112.0\"202.204.127.0\"2678");
	  addIpCityLine("202.204.144.0\"202.204.159.0\"51554");
	  addIpCityLine("202.206.64.0\"202.206.79.0\"19809");
	  addIpCityLine("202.206.80.0\"202.206.95.0\"19842");
	  addIpCityLine("202.206.96.0\"202.206.111.0\"20534");
	  addIpCityLine("202.206.240.0\"202.206.255.0\"66996");
	  addIpCityLine("202.207.0.0\"202.207.15.0\"38843");
	  addIpCityLine("202.207.208.0\"202.207.223.0\"47679");
	  addIpCityLine("202.207.240.0\"202.207.255.0\"54969");
	  addIpCityLine("202.117.0.0\"202.117.63.0\"61313");
	  addIpCityLine("202.117.80.0\"202.117.95.0\"61522");
	  addIpCityLine("202.117.96.0\"202.117.111.0\"61515");
	  addIpCityLine("202.117.112.0\"202.117.127.0\"61281");
	  addIpCityLine("202.117.128.0\"202.117.143.0\"61508");
	  addIpCityLine("202.117.144.0\"202.117.159.0\"48352");
	  addIpCityLine("202.117.208.0\"202.117.223.0\"61533");
	  addIpCityLine("202.200.32.0\"202.200.47.0\"82759");
	  addIpCityLine("202.200.80.0\"202.200.95.0\"61341");
	  addIpCityLine("202.200.96.0\"202.200.111.0\"61297");
	  addIpCityLine("202.200.112.0\"202.200.127.0\"61319");
	  addIpCityLine("202.200.144.0\"202.200.159.0\"61311");
	  addIpCityLine("202.201.0.0\"202.201.15.0\"31445");
	  addIpCityLine("202.201.128.0\"202.201.143.0\"41139");
	  addIpCityLine("202.201.240.0\"202.201.255.0\"64238");
	  addIpCityLine("202.115.0.0\"202.115.31.0\"9812");
	  addIpCityLine("202.115.32.0\"202.115.47.0\"52473");
	  addIpCityLine("202.115.48.0\"202.115.63.0\"61745");
	  addIpCityLine("202.115.64.0\"202.115.79.0\"75098");
	  addIpCityLine("202.115.80.0\"202.115.95.0\"74743");
	  addIpCityLine("202.115.112.0\"202.115.127.0\"61754");
	  addIpCityLine("202.115.144.0\"202.115.151.0\"61742");
	  addIpCityLine("202.115.152.0\"202.115.159.0\"75095");
	  addIpCityLine("202.202.240.0\"202.202.255.0\"74779");
	  addIpCityLine("202.203.0.0\"202.203.15.0\"17459");
	  addIpCityLine("202.203.176.0\"202.203.191.0\"71508");
	  addIpCityLine("202.203.192.0\"202.203.207.0\"71480");
	  addIpCityLine("202.203.208.0\"202.203.223.0\"71482");
	  addIpCityLine("202.203.224.0\"202.203.239.0\"71718");
	  addIpCityLine("202.116.0.0\"202.116.31.0\"25888");
	  addIpCityLine("202.116.32.0\"202.116.47.0\"23266");
	  addIpCityLine("202.116.64.0\"202.116.95.0\"74487");
	  addIpCityLine("202.116.240.0\"202.116.255.0\"18814");
	  addIpCityLine("202.192.112.0\"202.192.127.0\"16414");
	  addIpCityLine("202.192.128.0\"202.192.143.0\"50337");
	  addIpCityLine("202.192.144.0\"202.192.159.0\"48375");
	  addIpCityLine("202.192.240.0\"202.192.255.0\"60230");
	  addIpCityLine("202.193.0.0\"202.193.15.0\"16624");
	  addIpCityLine("202.193.64.0\"202.193.79.0\"17834");
	  addIpCityLine("202.193.104.0\"202.193.111.0\"17840");
	  addIpCityLine("202.193.160.0\"202.193.175.0\"16719");
	  addIpCityLine("202.193.192.0\"202.193.207.0\"17870");
	  addIpCityLine("202.114.32.0\"202.114.47.0\"23486");
	  addIpCityLine("202.114.48.0\"202.114.63.0\"60609");
	  addIpCityLine("202.114.64.0\"202.114.79.0\"60553");
	  addIpCityLine("202.114.80.0\"202.114.95.0\"60609");
	  addIpCityLine("202.114.96.0\"202.114.111.0\"60553");
	  addIpCityLine("202.114.112.0\"202.114.127.0\"60553");
	  addIpCityLine("202.114.144.0\"202.114.159.0\"22531");
	  addIpCityLine("202.114.192.0\"202.114.207.0\"74289");
	  addIpCityLine("202.114.224.0\"202.114.239.0\"74466");
	  addIpCityLine("202.196.64.0\"202.196.79.0\"74004");
	  addIpCityLine("202.196.96.0\"202.196.111.0\"20662");
	  addIpCityLine("202.196.240.0\"202.196.255.0\"1139");
	  addIpCityLine("202.197.64.0\"202.197.79.0\"74467");
	  addIpCityLine("202.197.96.0\"202.197.111.0\"22708");
	  addIpCityLine("202.119.0.0\"202.119.31.0\"10570");
	  addIpCityLine("202.119.32.0\"202.119.63.0\"39754");
	  addIpCityLine("202.119.64.0\"202.119.79.0\"39762");
	  addIpCityLine("202.119.80.0\"202.119.95.0\"39771");
	  addIpCityLine("202.119.96.0\"202.119.111.0\"39790");
	  addIpCityLine("202.119.112.0\"202.119.127.0\"20597");
	  addIpCityLine("202.119.176.0\"202.119.191.0\"74389");
	  addIpCityLine("202.119.192.0\"202.119.207.0\"74348");
	  addIpCityLine("202.119.208.0\"202.119.223.0\"39773");
	  addIpCityLine("202.119.224.0\"202.119.239.0\"39947");
	  addIpCityLine("202.119.240.0\"202.119.255.0\"39760");
	  addIpCityLine("202.194.0.0\"202.194.15.0\"47033");
	  addIpCityLine("202.194.16.0\"202.194.31.0\"83080");
	  addIpCityLine("202.194.32.0\"202.194.47.0\"74316");
	  addIpCityLine("202.194.80.0\"202.194.95.0\"47049");
	  addIpCityLine("202.194.112.0\"202.194.127.0\"66470");
	  addIpCityLine("202.194.240.0\"202.194.255.0\"19428");
	  addIpCityLine("202.195.48.0\"202.195.63.0\"67014");
	  addIpCityLine("202.195.96.0\"202.195.103.0\"27094");
	  addIpCityLine("202.195.128.0\"202.195.143.0\"53467");
	  addIpCityLine("202.195.144.0\"202.195.159.0\"27029");
	  addIpCityLine("202.195.160.0\"202.195.175.0\"27087");
	  addIpCityLine("202.195.176.0\"202.195.191.0\"39943");
	  addIpCityLine("202.195.192.0\"202.195.207.0\"27106");
	  addIpCityLine("202.195.208.0\"202.195.215.0\"39949");
	  addIpCityLine("202.195.224.0\"202.195.239.0\"39939");
	  addIpCityLine("202.195.240.0\"202.195.255.0\"39785");
	  addIpCityLine("202.120.0.0\"202.120.63.0\"48801");
	  addIpCityLine("202.120.64.0\"202.120.79.0\"80351");
	  addIpCityLine("202.120.80.0\"202.120.95.0\"23251");
	  addIpCityLine("202.120.96.0\"202.120.111.0\"23247");
	  addIpCityLine("202.120.112.0\"202.120.127.0\"48767");
	  addIpCityLine("202.120.128.0\"202.120.143.0\"48801");
	  addIpCityLine("202.120.160.0\"202.120.175.0\"57116");
	  addIpCityLine("202.120.176.0\"202.120.191.0\"57116");
	  addIpCityLine("202.120.224.0\"202.120.255.0\"14296");
	  addIpCityLine("202.121.0.0\"202.121.15.0\"62335");
	  addIpCityLine("202.121.16.0\"202.121.23.0\"23311");
	  addIpCityLine("202.121.32.0\"202.121.47.0\"13620");
	  addIpCityLine("202.121.48.0\"202.121.63.0\"13689");
	  addIpCityLine("202.121.64.0\"202.121.79.0\"79402");
	  addIpCityLine("202.121.80.0\"202.121.95.0\"81349");
	  addIpCityLine("202.121.112.0\"202.121.127.0\"48784");
	  addIpCityLine("202.121.128.0\"202.121.159.0\"73233");
	  addIpCityLine("202.121.176.0\"202.121.183.0\"73233");
	  addIpCityLine("202.121.224.0\"202.121.239.0\"23246");
	  addIpCityLine("202.121.240.0\"202.121.255.0\"39310");
	  addIpCityLine("202.118.0.0\"202.118.31.0\"10241");
	  addIpCityLine("202.118.32.0\"202.118.39.0\"50609");
	  addIpCityLine("202.118.40.0\"202.118.47.0\"74392");
	  addIpCityLine("202.118.48.0\"202.118.63.0\"33514");
	  addIpCityLine("202.118.64.0\"202.118.79.0\"7970");
	  addIpCityLine("202.118.80.0\"202.118.95.0\"7960");
	  addIpCityLine("202.118.116.0\"202.118.119.0\"10242");
	  addIpCityLine("202.118.120.0\"202.118.127.0\"33556");
	  addIpCityLine("202.118.160.0\"202.118.167.0\"10245");
	  addIpCityLine("202.118.176.0\"202.118.191.0\"18146");
	  addIpCityLine("202.118.192.0\"202.118.207.0\"18157");
	  addIpCityLine("202.118.208.0\"202.118.223.0\"10243");
	  addIpCityLine("202.118.224.0\"202.118.239.0\"18147");
	  addIpCityLine("202.198.16.0\"202.198.31.0\"25456");
	  addIpCityLine("202.198.32.0\"202.198.47.0\"25456");
	  addIpCityLine("202.198.64.0\"202.198.79.0\"18147");
	  addIpCityLine("202.198.80.0\"202.198.87.0\"18511");
	  addIpCityLine("202.198.96.0\"202.198.111.0\"21311");
	  addIpCityLine("202.198.144.0\"202.198.159.0\"25456");
	  addIpCityLine("202.198.160.0\"202.198.175.0\"25456");
	  addIpCityLine("202.198.192.0\"202.198.207.0\"66546");
	  addIpCityLine("202.198.128.0\"202.198.143.0\"10248");
	  addIpCityLine("202.198.144.0\"202.198.159.0\"25456");
	  addIpCityLine("202.198.160.0\"202.198.175.0\"25456");
	  addIpCityLine("202.198.192.0\"202.198.207.0\"66546");
	  addIpCityLine("202.198.216.0\"202.198.223.0\"66546");
	  addIpCityLine("202.198.240.0\"202.198.255.0\"66546");
	  addIpCityLine("202.199.32.0\"202.199.47.0\"50605");
	  addIpCityLine("202.199.56.0\"202.199.63.0\"10240");
	  addIpCityLine("202.199.96.0\"202.199.111.0\"50609");
	  addIpCityLine("202.199.112.0\"202.199.119.0\"50612");
	  addIpCityLine("202.199.128.0\"202.199.143.0\"7961");
	  addIpCityLine("202.199.224.0\"202.199.239.0\"33517");
	  addIpCityLine("202.199.248.0\"202.199.255.0\"1324");
  }
  
 
  
	 public static boolean isIp(String ipAddress){ 
	        String test = "([1-9]|[1-9]\\d|1\\d{2}|2[0-1]\\d|22[0-3])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}"; 
	        Pattern pattern = Pattern.compile(test); 
	        Matcher matcher = pattern.matcher(ipAddress); 
	        return matcher.matches(); 
	 }

  public Text evaluate(Text s) {
    if (s == null) {
      return null;
    }
    if(!isIp(s.toString())){
    	return null;
    }
     
    String str = s.toString();
    String cityID = getCityIdByIp(str);
     
    if(cityID.equals("-1")) {
      return null;
    }
    result.set(cityID);
    return result;
  }

  
  
  
  private void addIpCityLine(String ipCityLine) {
	    String[] ipCityStr = ipCityLine.split("\"");
	    if(ipCityStr.length < 3) {
	      return;
	    } else {
	      IpCityList ipl = new IpCityList();
	      ipl.setStartIP(ipCityStr[0]);
	      ipl.setEndIP(ipCityStr[1]);
	      ipl.setCityID(ipCityStr[2]);
	      ipCities.add(ipl);
	    }
  }
  
 
  
  
  private String getCityIdByIp(String ip) {
	  Iterator iter = ipCities.iterator(); 
	  while(iter.hasNext()){
		  IpCityList ipcity = (IpCityList)iter.next();
		  String startip = ipcity.getStartIP()+"";
		  String endip = ipcity.getEndIP()+"";
		  String schoolname = ipcity.getCityID();
		  UDFIsInIp isinip = new UDFIsInIp();
		  if(isinip.evaluate(ip, startip, endip).toString().equals("true")){
			  return schoolname;
		  }
		 
	  }
	  return "0"; 
  }
  
  
  
  
  
  
  
  
  

  static class IpCityList {
    public String getStartIP() {
		return startIP;
	}



	public void setStartIP(String startIP) {
		this.startIP = startIP;
	}



	public String getEndIP() {
		return endIP;
	}



	public void setEndIP(String endIP) {
		this.endIP = endIP;
	}

	private String startIP;
    private String endIP;
    public String getCityID() {
		return CityID;
	}



	public void setCityID(String cityID) {
		CityID = cityID;
	}

	private String CityID;
    
    

   

    
    

  
  } 
  
  public static void main(String args[]){
	  UDFGetIPSchool test = new UDFGetIPSchool();
	   
	  System.out.println(test.evaluate(new Text("202.112.64.0")));
	  System.out.println(test.evaluate(new Text("202.112.64.0")));
	  //System.out.println(test.evaluate(new Text("202.113.17.0")));
	  
	  
	  
  }
}