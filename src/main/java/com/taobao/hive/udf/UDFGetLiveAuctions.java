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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hive.ql.exec.UDF;

public class UDFGetLiveAuctions extends UDF {
  private static Log LOG = LogFactory.getLog(UDFGetLiveAuctions.class.getName());
  private SimpleDateFormat simpleFormatter = new SimpleDateFormat("yyyyMMdd");
  private String spliter = "";
  
  public UDFGetLiveAuctions() {
	  
  }

  public Date getAddDate(long deltaDay, Date baseDate){
	  long tmpDelta = baseDate.getTime() + deltaDay*1000*3600*24;
	  return new Date(tmpDelta);
  }
  
  public long getDeltaDate(Date endDate, Date startDate){
	  return (endDate.getTime() - startDate.getTime())/1000/3600/24;
  }
  
  private String getDeadPt(Date tmpDate, Date expectDate, Date initDate){
	  String tmpPt = new String();
	  Date tmpDate1 = new Date();
	  long deltaDate = getDeltaDate(expectDate, initDate);
	  for(; deltaDate >= 0; deltaDate --){
		  tmpPt += " " + spliter + " pt='";
		  spliter = "and";
		  tmpDate1 = getAddDate(deltaDate, initDate);
		  tmpPt += simpleFormatter.format(tmpDate1) + "-" + simpleFormatter.format(tmpDate);
		  tmpPt += "' ";
	  }
	  return tmpPt;
  }
  
  private String getLivePt(Date expectDate, Date initDate){
	  String tmpPt = new String();
	  Date tmpDate1 = new Date();
	  long deltaDate = getDeltaDate(expectDate, initDate);
	  for(; deltaDate >= 0; deltaDate --){
		  tmpPt += " " + spliter + " pt='";
		  spliter = "and";
		  tmpDate1 = getAddDate(deltaDate, initDate);
		  tmpPt += simpleFormatter.format(tmpDate1) + "-?";
		  tmpPt += "' ";
	  }
	  return tmpPt;
  }
  
//	input:
//  		expect:3.3
//  		init:3.1 
//  		last:3.5
//  output:
//          3.4/3.1-3.4 3.4/3.2-3.4 3.4/3.3-3.4
//          3.5/3.1-3.5 3.5/3.2-3.5 3.5/3.3-3.5
//          3.1-? 3.2-? 3.3-?
  public String evaluate(String expectDateString, String initDateString, String lastDateString){
	  String liveAuctions = new String();

	  Date expectDate = new Date();
	  Date lastDate = new Date();
	  Date initDate = new Date();
	  spliter = "";
	  
	  try {
		expectDate = simpleFormatter.parse(expectDateString);
		lastDate = simpleFormatter.parse(lastDateString);
		initDate = simpleFormatter.parse(initDateString);

		if(getDeltaDate(lastDate, expectDate) < 0 || getDeltaDate(expectDate, initDate) < 0 ){
			return "";
		}
		long deltaDay = getDeltaDate(lastDate, expectDate);
		
//		LOG.info(deltaDay);
		Date tmpDate = new Date();
		for(;deltaDay > 0; deltaDay --){
			tmpDate = getAddDate(deltaDay, expectDate);
			liveAuctions += getDeadPt(tmpDate, expectDate, initDate);
		}
		liveAuctions += getLivePt(expectDate, initDate);
	} catch (ParseException e) {
		LOG.error(e);
	}
	  return liveAuctions;
  }
  
  public static void main(String[] args) throws Exception {
	  UDFGetLiveAuctions df = new UDFGetLiveAuctions();
	  if(args.length != 3)
		  return;
      System.out.println(df.evaluate(args[0], args[1], args[2]));
  }
}