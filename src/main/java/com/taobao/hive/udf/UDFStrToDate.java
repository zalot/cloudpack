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
import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.Text;


@Description(
    name = "str_to_date",
    value = "_FUNC_([str[, pattern]]) - Returns the date corresponding to the pattern",
    extended = "Converts the first string the date corresponding to the pattern "
    )
public class UDFStrToDate extends UDF {

  private static Log LOG = LogFactory.getLog(UDFStrToDate.class.getName());

  private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  private SimpleDateFormat internalFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  Text t = new Text();
  public UDFStrToDate() {
  }

  /**
   * Convert time string to date.
   * @param dateText Time string in format yyyy-MM-dd HH:mm:ss
   * @return the date corresponding to default pattern.
   */
  public Text evaluate(Text dateText)  {
    if (dateText == null) {
      return null;
    }

    try {
      Date date = formatter.parse(dateText.toString());
      t.set(internalFormatter.format(date));
      return t;
    } catch (ParseException e) {
      return null;
    }
  }

  Text lastPatternText = new Text();
  /**
   * Convert time string to date with user defined pattern.
   * @param dateText Time string in format patternstring
   * @param patternText Time patterns string supported by SimpleDateFormat
   * @return the date corresponding to the pattern.
   */
  public Text evaluate(Text dateText, Text patternText)  {
    if (dateText == null || patternText == null) {
      return null;
    }
    try {
      if (!patternText.equals(lastPatternText)) {
        formatter.applyPattern(patternText.toString());
        lastPatternText.set(patternText);
      }      
    } catch (Exception e) {
      return null;
    }

    return evaluate(dateText);
  }
  
}
