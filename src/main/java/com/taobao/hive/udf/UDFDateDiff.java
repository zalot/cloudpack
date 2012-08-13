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
import java.util.TimeZone;

import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.hive.serde2.io.DoubleWritable;
import org.apache.hadoop.io.Text;

@Description(
    name = "datediff",
    value = "_FUNC_(date1, date2) - Returns the number of days between date1 " +
                "and date2",
    extended = "date1 and date2 are strings in the format " +
                "'yyyy-MM-dd HH:mm:ss' or 'yyyy-MM-dd'. The time parts are not ignored." +
                "If date1 is earlier than date2, the result is negative.\n" +
        "Example:\n " +
        "  > SELECT _FUNC_('2009-30-07', '2009-31-07') FROM src LIMIT 1;\n" +
        "  1"
    )
public class UDFDateDiff extends UDF {
  private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  private SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd");

  DoubleWritable result = new DoubleWritable();

  public UDFDateDiff() {
    formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
    formatter1.setTimeZone(TimeZone.getTimeZone("UTC"));
  }

  public DoubleWritable evaluate(Text dateString1, Text dateString2) {

    if (dateString1 == null || dateString2 == null) {
      return null;
    }

    long dateMS1 = 0;
    try {
      dateMS1 = formatter.parse(dateString1.toString()).getTime();
    } catch (ParseException e) {
      try {
        dateMS1 = formatter1.parse(dateString1.toString()).getTime();
      } catch (ParseException ex) {
        return null;
      }
    }

    long dateMS2 = 0;
    try {
      dateMS2 = formatter.parse(dateString2.toString()).getTime();
    } catch (ParseException e) {
      try {
        dateMS2 = formatter1.parse(dateString2.toString()).getTime();
      } catch (ParseException ex) {
        return null;
      }
    }

    long diffInMilliSeconds = dateMS1 - dateMS2;
    // 86400 is the number of seconds in a day
    result.set((double) diffInMilliSeconds / (86400 * 1000));
    return result;

  }
}
