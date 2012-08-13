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

/**
 *
 *===============================================================================
 *
 *          FILE:  UDFTrunc
 *
 *         USAGE:  Change Date String From One Format Into ANOther
 *
 *   DESCRIPTION:
 *
 *       OPTIONS:  ---
 *  REQUIREMENTS:  ---
 *          BUGS:  ---
 *         NOTES:  ---
 *        AUTHOR: ·ǧ ��luqian@taobao.com��
 *       COMPANY: �Ա�-�����з���-���ƽ̨���Ʒ-��ݿ���-BI
 *       CREATED: 2010��05��07�� 14ʱ54��06�� CST
 *      REVISION:  ---
 *===============================================================================
 *
 **/

package com.taobao.hive.udf;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;

@Description(name = "Trunc", value = "_FUNC_([inDateString,inDatePattern  [, outDatePattern, days, isDebugOn]]) - Returns the date corresponding to the pattern", extended = "Converts the first string the date corresponding to the pattern ")

public class UDFTrunc extends UDF {

    private static Log LOG = LogFactory.getLog(UDFTrunc.class.getName());

    Text result = new Text();

    public UDFTrunc() {
    }

    public Text evaluate(Text inDateString ) {
        return evaluate(inDateString, new Text("yyyyMMdd"), new Text("yyyy-MM-dd"),
                new IntWritable(0), new Text("N"));
    }

    public Text evaluate(Text inDateString ,IntWritable days) {
        return evaluate(inDateString, new Text("yyyyMMdd"), new Text("yyyy-MM-dd"),
                days, new Text("N"));
    }

    public Text evaluate(IntWritable days) {
        Date dateNow=new Date();
        SimpleDateFormat  dateFormat=new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
        String dateNowStr=dateFormat.format(dateNow);
//        System.out.println( );
        return evaluate(new Text(dateNowStr), new Text("yyyy-MM-dd HH:mm:ss"), new Text("yyyy-MM-dd"),
                days, new Text("N"));
    }

    public Text evaluate(Text inDateString, Text inDatePattern) {
        return evaluate(inDateString, inDatePattern, new Text("yyyy-MM-dd HH:mm:ss"),
                new IntWritable(0), new Text("N"));
    }

    public Text evaluate(Text inDateString, Text inDatePattern,
            Text outDatePattern) {
        return evaluate(inDateString, inDatePattern, outDatePattern,
                new IntWritable(0), new Text("N"));
    }

    public Text evaluate(Text inDateString, Text inDatePattern,
            Text outDatePattern, IntWritable days) {
        return evaluate(inDateString, inDatePattern, outDatePattern, days,
                new Text("N"));
    }

    public Text evaluate(Text inDateString, Text inDatePattern,
            Text outDatePattern, IntWritable days, Text isDebugOn) {

        if (inDateString == null || inDatePattern == null || inDateString.toString() == "NULL") {
            return null;
        }

        if (outDatePattern == null) {
            outDatePattern.set("yyyy-MM-dd HH:mm:ss");
        }

        if (days == null) {
            days.set(0);
        }

        if (isDebugOn == null) {
            isDebugOn.set("N");
        }

        SimpleDateFormat inDateFormat = new SimpleDateFormat(inDatePattern
                .toString(), Locale.ENGLISH);
        SimpleDateFormat outDateFormat = new SimpleDateFormat(outDatePattern
                .toString(), Locale.ENGLISH);

        try {
            Date inDate = inDateFormat.parse(inDateString.toString());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(inDate);
            calendar.add(Calendar.DAY_OF_MONTH, days.get());
            result.set(outDateFormat.format(calendar.getTime()));
        } catch (Exception e) {
            if (isDebugOn.toString() == "Y") {
                e.printStackTrace();
            }
            return null;
        }

        return result;
    }

    public static void main(String[] args) throws Exception {
        UDFTrunc trunc = new UDFTrunc();
        System.out.println(trunc.evaluate( new Text("20100611"), new IntWritable(1) ));
    }
}

