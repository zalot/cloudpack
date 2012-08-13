package com.taobao.hive.udf;

import org.apache.hadoop.hive.ql.exec.UDF;

/**
 * TODO
 * 
 * @author hongzhen.lm E-mail:hongzhen.lm@taobao.com
 * @since 2011-4-18 ����10:51:32
 * @version 1.0
 */
public class UDFIpRangeInput extends UDF {
    private static final String DEFAULTRETURN = "";

    public String evaluate(String lastIps) {

        if (lastIps == null) {
            return DEFAULTRETURN;
        }

        if (("").equals(lastIps.trim())) {
            return DEFAULTRETURN;
        }
        String[] ips = lastIps.split(",");
        // System.out.println("ips"+ips.length);
        String result = "";
        if (ips.length >= 1) {
            return ips[0];
        } else {
            return DEFAULTRETURN;
        }
    }

    public String evaluate(String lastIps, int count) {
        if (lastIps == null) {
            return DEFAULTRETURN;
        }

        if ((DEFAULTRETURN).equals(lastIps.trim())) {
            return DEFAULTRETURN;
        }
        String[] ips = lastIps.split(",");
        int size = ips.length;
        if (size == 1) {
            if (count == 1) {
                return ips[0];
            }

        } else if (size == 2) {
            if (count == 1) {
                return ips[0];
            } else if (count == 2) {
                return ips[1];
            }

        } else if (size == 3) {
            if (count == 1) {
                return ips[0];
            } else if (count == 2) {
                return ips[1];
            } else if (count == 3) {
                return ips[2];
            }

        }

        return DEFAULTRETURN;

    }

    public static void main(String[] args) {

        String ipss = "1.52.255.255,1.52.1.254";
        UDFIpRangeInput input = new UDFIpRangeInput();
        System.out.println("evaluate()===>"+input.evaluate(ipss));
        System.out.println("evaluate===>"+input.evaluate(ipss,3));
    }
}
