package com.taobao.mrsstd.hiveudf.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * TODO
 * 
 * @author hongzhen.lm E-mail:hongzhen.lm@taobao.com
 * @since 2011-4-13 ����10:28:31
 * @version 1.0
 */
public class FormatTool {
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
    private static SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");

    public static Date getDateSubDay(Date time, int day) throws ParseException {
        Date date = formateDateToDate(time);
        Calendar caledar = Calendar.getInstance();
        caledar.setTime(date);
        caledar.add(Calendar.DATE, day);
        return caledar.getTime();

    }

    public static Date formatStr2Date(String dateStr) throws ParseException {
        if (dateStr == null || "".equals(dateStr)) {
            return formateDateToDate(new Date());
        } else {
            return dateFormat.parse(dateStr);
        }

    }

    public static Date formateDateToDate(Date date) throws ParseException {
        if (date == null) {
            return dateFormat.parse(dateFormat.format(new Date()));
        } else {
            return dateFormat.parse(dateFormat.format(date));
        }

    }

    public static String getDateBeforeMonth(Date date, int monthCount) throws ParseException {

        Calendar caledar = Calendar.getInstance();
        caledar.setTime(date);
        caledar.set(Calendar.DATE, caledar.getActualMinimum(Calendar.DAY_OF_MONTH));
        caledar.set(Calendar.HOUR_OF_DAY, 0);
        caledar.set(Calendar.MINUTE, 0);
        caledar.set(Calendar.SECOND, 0);
        caledar.add(Calendar.MONTH, monthCount);

        return dateFormat1.format(caledar.getTime());
    }

    public static void main(String[] args) {

        try {
            System.out.println("getDateBeforeMonth:" + getDateBeforeMonth(new Date(), -8));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
