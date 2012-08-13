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

import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;

public class UDFGetHtmQianN extends UDF {

  Text result = new Text();

  public Text evaluate(Text urlText, IntWritable nW) {
    if (urlText == null || nW == null)
      return null;
    String url = urlText.toString();
    int n = nW.get();
    int index = url.indexOf(".htm");
    String a = url.substring(0, index);
    index = a.lastIndexOf("/");
    String b = a.substring(index + 1, a.length());
    String[] c = b.split("-");
    int len = c.length;
    if (len < n) {
      result.set("-");
      return result;
    }
    String d = c[len - n];
    result.set(d);
    return result;
  }
  
  public static void main(String[] args) {
    UDFGetHtmQianN h = new UDFGetHtmQianN();
    System.out.println(h.evaluate(new Text("http://www.dfasdfsd.com/dfsdfsd/dfas/22dsffsd/22-33-44-55-66-77-88-99-22.htm"), new IntWritable(3)));
  }

}
