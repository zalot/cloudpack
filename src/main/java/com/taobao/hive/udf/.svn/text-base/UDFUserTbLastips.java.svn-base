package com.taobao.hive.udf;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.hadoop.hive.ql.exec.UDF;

/**
 * TODO
 * 
 * @author hongzhen.lm E-mail:hongzhen.lm@taobao.com
 * @since 2011-4-11 ����10:38:15
 * @version 1.0
 */
public class UDFUserTbLastips extends UDF {
    private static String DEFAULTSPILT = ",";
    private static String DEFAULTCON = ".";
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

    public static Date getDateSubDay(Date time, int day) throws ParseException {
        Date date = formateDateToDate(time);
        Calendar caledar = Calendar.getInstance();
        caledar.setTime(date);
        caledar.add(Calendar.DATE, day);
        return caledar.getTime();

    }

    public String evaluate(String lastIps) {
        StringBuffer IP = new StringBuffer("");
        if (lastIps == null) {
            return IP.toString();
        }
        try {
            if (lastIps.length() < 8) {
                return null;
            }
            String ip1 = lastIps.substring(0, 2);
            String ip2 = lastIps.substring(2, 4);
            String ip3 = lastIps.substring(4, 6);
            String ip4 = lastIps.substring(6, 8);
            String Ip = Integer.valueOf(ip1, 16) + DEFAULTCON + Integer.valueOf(ip2, 16) + DEFAULTCON + Integer.valueOf(ip3, 16) + DEFAULTCON + Integer.valueOf(ip4, 16);
            IP.append(Ip);
            return IP.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return null;

        }

    }

    public String evaluate(String lastIps, String startTime) {
        StringBuffer IP = new StringBuffer("");
        if (lastIps == null) {
            return IP.toString();
        }
        try {
            Date date = formateDateToDate(new Date());
            Date currentDate = formatStr2Date(startTime);
            String[] login_trace = lastIps.split(DEFAULTSPILT);
            int count = 0;
            for (int i = login_trace.length - 1; i >= 0; i--) {
                if (login_trace[i].length() < 18) {
                    continue;
                }
                String ip = login_trace[i].substring(0, 8);
                String time = login_trace[i].substring(8, 18);
                Long timestamp = Long.parseLong(time) * 1000;
                date.setTime(timestamp);

                if (date.before(currentDate) || count > 5) {
                    break;
                }
                String ip1 = ip.substring(0, 2);
                String ip2 = ip.substring(2, 4);
                String ip3 = ip.substring(4, 6);
                String ip4 = ip.substring(6, 8);
                String Ip = Integer.valueOf(ip1, 16) + DEFAULTCON + Integer.valueOf(ip2, 16) + DEFAULTCON + Integer.valueOf(ip3, 16) + DEFAULTCON + Integer.valueOf(ip4, 16);
                IP.append(Ip);
                IP.append(DEFAULTSPILT);
                count++;
                // IP.append(b)

            }

            return IP.substring(0, IP.length() - 1);

        } catch (Exception e) {
            e.printStackTrace();
            return null;

        }

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

    public static void main(String[] args) throws ParseException {
        String ipss = "7a4c07fa1302533654,da02f42e1259218448";
        String ip = "3b24b78c";
        UDFUserTbLastips ips = new UDFUserTbLastips();
        Date date = formateDateToDate(new Date());
        Date currentDate = getDateSubDay(new Date(), -7);

        System.out.println("currentTiem = >" + currentDate.getTime());
        System.out.println("Result = >" + ips.evaluate(ipss, "20091125"));

        Long timestamp = Long.parseLong("1302533654") * 1000;
        String str = new java.text.SimpleDateFormat("yyyyMMdd").format(new java.util.Date(timestamp));
        System.out.println("�����Ľ���������" + str);

    }
}
