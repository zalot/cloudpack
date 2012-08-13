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
import com.taobao.hive.udf.UDFGetLiveAuctions;

public class UDFAddPartition extends UDF {
  private static Log LOG = LogFactory.getLog(UDFAddPartition.class.getName());
  private SimpleDateFormat simpleFormatter = new SimpleDateFormat("yyyyMMdd");
  private String table = "";
  private String deadPath = "";
  private String livePath = "";
  private String addSql = "ALTER TABLE %s ADD PARTITION (pt='%s') LOCATION '%s';";
  
  public UDFAddPartition() {
	  
  }
  
  private String addDeadPartitions(long deltaDate, Date tmpDate, Date lastDate){
	  String lastDateString = simpleFormatter.format(lastDate);
	  String tmpDateString = simpleFormatter.format(tmpDate);
	  String deadPartitions = String.format(addSql, table, tmpDateString+"-"+lastDateString,
			  deadPath+lastDateString+"/"+tmpDateString+"-"+lastDateString);
	  return deadPartitions;
  }
  
  private String addLivePartitions(Date lastDate){
	  String deadPartitions = new String();
	  String lastDateString = simpleFormatter.format(lastDate);
	  deadPartitions = String.format(addSql, table, lastDateString+"-?",
			  livePath+lastDateString+"-?");
	  return deadPartitions;
  }
  
  public String evaluate(String table, String deadPath, String livePath, String initDateString, String lastDateString){
	  this.table = table;
	  this.deadPath = deadPath;
	  this.livePath = livePath;
	  
	  String addPartitionString = new String();
	  Date lastDate = new Date();
	  Date initDate = new Date();
	  Date tmpDate = new Date();
	  UDFGetLiveAuctions df = new UDFGetLiveAuctions();
	  try {
		lastDate = simpleFormatter.parse(lastDateString);
		initDate = simpleFormatter.parse(initDateString);
		long deltaDate = df.getDeltaDate(lastDate, initDate);
		
		if(deltaDate < 0)
			return "";

		for(; deltaDate > 0; deltaDate--){
			tmpDate = df.getAddDate(deltaDate-1, initDate);
			addPartitionString += addDeadPartitions(deltaDate, tmpDate, lastDate);
		}
		addPartitionString += addLivePartitions(lastDate);
	} catch (ParseException e) {
		LOG.error(e);
	}
	  return addPartitionString;
  }
  
  public static void main(String[] args) throws Exception {
	  if(args.length != 5)
		  return;
	  UDFAddPartition df = new UDFAddPartition();
      System.out.println(df.evaluate(args[0],args[1], args[2], args[3], args[4]));
  }
}