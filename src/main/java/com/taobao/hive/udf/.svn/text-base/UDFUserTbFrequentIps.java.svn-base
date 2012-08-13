package com.taobao.hive.udf;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.hadoop.hive.ql.exec.UDF;

/**
 * TODO
 * 
 * @author hongzhen.lm E-mail:hongzhen.lm@taobao.com
 * @since 2011-4-13 ����10:25:30
 * @version 1.0
 */
public class UDFUserTbFrequentIps extends UDF {
    public static final Object IPCount = null;
    private static String DEFAULTSPILT = ",";
    private static String DEFAULTCON = ".";

    public String evaluate(String logintrace) {
        StringBuffer IP = new StringBuffer("");
        if (logintrace == null) {
            return IP.toString();
        }
        try {
            String[] login_trace = logintrace.split(DEFAULTSPILT);
            /**
             * TODO ��ΪIP�������IP����ܴ� ����ArrayList indexOf����ʵ�ּ�ʹ����ȫList
             * ��Ч���Ͽ�����̫��Ӱ�죬Ŀǰ�������
             * */
            List<IPCount> ipList = new ArrayList();
            for (int i = login_trace.length - 1; i >= 0; i--) {
                if (login_trace[i].length() < 8) {
                    continue;
                }
                String ip = login_trace[i].substring(0, 8);

                String ip1 = ip.substring(0, 2);
                String ip2 = ip.substring(2, 4);
                String ip3 = ip.substring(4, 6);
                String ip4 = ip.substring(6, 8);
                String Ip = Integer.valueOf(ip1, 16) + DEFAULTCON + Integer.valueOf(ip2, 16) + DEFAULTCON + Integer.valueOf(ip3, 16) + DEFAULTCON + Integer.valueOf(ip4, 16);
                IPCount count = new IPCount();
                count.setIp(Ip);
                if (ipList.contains(count)) {
                    int index = ipList.indexOf(count);
                    IPCount count1 = ipList.get(index);
                    count1.setCount(count1.getCount().intValue() + 1);
                } else {

                    count.setCount(1);
                    ipList.add(count);

                }
                // IP.append(b)

            }
            // �ɸߵ�������
            Collections.sort(ipList, new Comparator<IPCount>() {

                @Override
                public int compare(IPCount o1, IPCount o2) {
                    if (o1.getCount().intValue() > o2.getCount().intValue()) {
                        return -1;
                    } else if (o1.getCount().intValue() < o2.getCount().intValue()) {
                        return 1;
                    } else
                        return 0;
                }

            });
            if (ipList.size() >= 3) {
                IPCount ip1 = ipList.get(0);
                IPCount ip2 = ipList.get(1);
                IPCount ip3 = ipList.get(2);
                return ip1.getIp() + DEFAULTSPILT + ip2.getIp() + DEFAULTSPILT + ip3.getIp();
            } else if (3 > ipList.size() && ipList.size() > 1) {
                IPCount ip1 = ipList.get(0);
                IPCount ip2 = ipList.get(1);
                return ip1.getIp() + DEFAULTSPILT + ip2.getIp();
            } else if (2 > ipList.size() && ipList.size() > 0) {
                IPCount ip1 = ipList.get(0);
                return ip1.getIp();
            } else {
                return "";
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "";

        }
    }
    
    class IPCount {
        private String ip;
        private Integer count;

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public Integer getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }

        public boolean equals(Object obj) {

            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (!(obj instanceof IPCount)) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            IPCount obj1 = (IPCount) obj;
            if (this.ip.equals(obj1.getIp())) {
                return true;

            }
            return false;

        }

        @Override
        public int hashCode() {
            return this.ip.hashCode();
        }

    }

    public static void main(String[] args) throws ParseException {
        String ipss = "da02f42e1259159066";
        String ip = "da02f42e";
        UDFUserTbFrequentIps ips = new UDFUserTbFrequentIps();

        System.out.println("Result = >" + ips.evaluate(ipss));

        Long timestamp = Long.parseLong("1302533654") * 1000;
        String str = new java.text.SimpleDateFormat("yyyyMMdd").format(new java.util.Date(timestamp));
        System.out.println("�����Ľ���������" + str);

    }
}
