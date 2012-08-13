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
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;

public class UDFDateSub2 extends UDF {


  private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  private SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd");
  private Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

  Text result = new Text();
  public UDFDateSub2() {
  }

  public Text evaluate(Text dateString1, IntWritable days)  {
    
    if (dateString1 == null || days == null) {
      return null;
    }
    
    try {
      calendar.setTime(formatter.parse(dateString1.toString()));
      calendar.add(Calendar.DAY_OF_MONTH, -days.get());
      Date newDate = calendar.getTime();
      result.set(formatter.format(newDate));
      return result;
    } catch (ParseException e) {
      try {
        calendar.setTime(formatter2.parse(dateString1.toString()));
        calendar.add(Calendar.DAY_OF_MONTH, -days.get());
        Date newDate = calendar.getTime();
        result.set(formatter2.format(newDate));
        return result;
      } catch (ParseException ex) {
        return null;
      }
    }
  }
  
}