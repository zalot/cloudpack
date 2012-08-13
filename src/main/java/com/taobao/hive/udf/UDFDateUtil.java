package com.taobao.hive.udf;

import java.text.ParseException;
import java.util.Date;

import org.apache.hadoop.hive.ql.exec.UDF;

import com.taobao.mrsstd.hiveudf.util.FormatTool;


/**
 * TODO
 * 
 * @author hongzhen.lm E-mail:hongzhen.lm@taobao.com
 * @since 2011-4-19 ����01:20:11
 * @version 1.0
 */
public class UDFDateUtil extends UDF {
    public String evaluate(String time, int month) {
        String result = null;
        try {
            Date date = FormatTool.formatStr2Date(time);
            result = FormatTool.getDateBeforeMonth(date, month);
        } catch (ParseException e) {
            try {
                result = FormatTool.getDateBeforeMonth(new Date(), month);
            } catch (ParseException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
        return result;
    }

    public static void main(String[] args) {
        UDFDateUtil util = new UDFDateUtil();
        System.out.println("DateUtil==>" + util.evaluate("20110419", -8));

    }
}
