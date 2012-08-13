package com.taobao.hive.udf.notcommon;

import org.apache.hadoop.hive.ql.exec.UDF;

public class UDFChangList extends UDF {

        public String evaluate(String url) {
                int radix = Character.MAX_RADIX;
                String searchVmName = "/search_auction.htm";
                String SEPARATOR = "-";
                String ADD = "+";
                if (url != null) {
                        String newSep = SEPARATOR + ADD;
                        StringBuffer renderedUrl = null;
                        StringBuffer tmpUrl = null;
                        renderedUrl = new StringBuffer(url);
                        tmpUrl = new StringBuffer();
                        if (url.indexOf(searchVmName) != -1) {

                                return url;
                        }
                        int index = renderedUrl.indexOf(newSep);
                        if (index == -1) {
                                tmpUrl = renderedUrl;
                        }
                        while (index != -1) {
                                String before = renderedUrl.substring(0, index);
                                tmpUrl.append(before);
                                String left = renderedUrl.substring(index + newSep.length());
                                int firstSep = left.indexOf(SEPARATOR);
                                if (firstSep != -1) {
                                        String sepCount = left.substring(0, firstSep);
                                        left = left.substring(firstSep);
                                        if (sepCount != null) {
                                                int count = 0;
                                                try {
                                                        count = Integer.parseInt(sepCount, radix);
                                                } catch (Exception e) {
                                                }
                                                for (int i = 0; i < count - 1; i++) {
                                                        tmpUrl.append(SEPARATOR);
                                                }
                                        }
                                }
                                index = left.indexOf(newSep);
                                if (index == -1) {
                                        tmpUrl.append(left);
                                }
                                renderedUrl = new StringBuffer(left);
                        }
                        return tmpUrl.toString();
                } else {
                        return url;
                }
        }
}