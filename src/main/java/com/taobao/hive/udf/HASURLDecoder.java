package com.taobao.hive.udf;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.net.URLDecoder;
/**
* <p>Description:  </p>
* <p>Copyright: flashman.com.cn Copyright (c) 2005</p>
* <p>Company: flashman.com.cn </p>
* @author: jeffzhu
* @version 1.0
*/
public class HASURLDecoder {

    static int  base32Lookup[] = {
        0xFF, 0xFF, 0x1A, 0x1B, 0x1C, 0x1D, 0x1E, 0x1F, // '0', '1', '2', '3', '4', '5', '6', '7'
        0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, // '8', '9', ':', ';', '<', '=', '>', '?'
        0xFF, 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, // '@', 'A', 'B', 'C', 'D', 'E', 'F', 'G'
        0x07, 0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, // 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O'
        0x0F, 0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16, // 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W'
        0x17, 0x18, 0x19, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, // 'X', 'Y', 'Z', '[', '\', ']', '^', '_'
        0xFF, 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, // '`', 'a', 'b', 'c', 'd', 'e', 'f', 'g'
        0x07, 0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, // 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o'
        0x0F, 0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16, // 'p', 'q', 'r', 's', 't', 'u', 'v', 'w'
        0x17, 0x18, 0x19, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF  // 'x', 'y', 'z', '{', '|', '}', '~', 'DEL'
    };
    /**
    * 转换编码 ISO-8859-1到GB2312
    * @param text
    * @return
    */
    public static String ISO2GB(String text) {
        String result = "";
        try {
            result = new String(text.getBytes("ISO-8859-1"), "GB2312");
        }
        catch (UnsupportedEncodingException ex) {
            result = ex.toString();
        }
        return result;
    }

    /**
    * 转换编码 GB2312到ISO-8859-1
    * @param text
    * @return
    */
    public static String GB2ISO(String text) {
        String result = "";
        try {
            result = new String(text.getBytes("GB2312"), "ISO-8859-1");
        }
        catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }
        return result;
    }

    /**
    * Utf8URL编码
    * @param s
    * @return
    */
    public static String Utf8URLencode(String text) {
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c >= 0 && c <= 255) {
                result.append(c);
            }
            else {
                byte[] b = new byte[0];
                try {
                    b = Character.toString(c).getBytes("UTF-8");
                }catch (Exception ex) {
                }
                for (int j = 0; j < b.length; j++) {
                    int k = b[j];
                    if (k < 0) k += 256;
                    result.append("%" + Integer.toHexString(k).toUpperCase());
                }
            }
        }
        return result.toString();
    }

    /**
    * Utf8URL解码
    * @param text
    * @return
    */
    public static String Utf8URLdecode(String text) {
        String result = "";
        int p = 0;
        if (text!=null && text.length()>0){
            text = text.toLowerCase();
            p = text.indexOf("%e");
            if (p == -1)
                return text;
            while (p != -1) {
                result += text.substring(0, p);
                text = text.substring(p, text.length());
                if (text == "" || text.length() < 9)
                    return result;
                result += CodeToWord(text.substring(0, 9));
                text = text.substring(9, text.length());
                p = text.indexOf("%e");
            }
        }
        return result + text;
    }

    /**
    * @param text
    * @return
    */
    private static String CodeToWord(String text) {
        String result;
        if (Utf8codeCheck(text)) {
            byte[] code = new byte[3];
            code[0] = (byte) (Integer.parseInt(text.substring(1, 3), 16) - 256);
            code[1] = (byte) (Integer.parseInt(text.substring(4, 6), 16) - 256);
            code[2] = (byte) (Integer.parseInt(text.substring(7, 9), 16) - 256);
            try {
                result = new String(code, "UTF-8");
            }catch (UnsupportedEncodingException ex) {
                result = null;
            }
        }
        else {
            result = text;
        }
        return result;
    }

    /** * 编码是否有效 * @param text * @return */
    private static boolean Utf8codeCheck(String text){
        String sign = "";
        if (text.startsWith("%e"))
        for (int i = 0, p = 0; p != -1; i++) {
            p = text.indexOf("%", p);
            if (p != -1)
            p++;
            sign += p;
        }
        return sign.equals("147-1");
    }

    /**
    * 是否Utf8Url编码
    * @param text
    * @return
    */
    public static boolean isUtf8Url(String text) {
        text = text.toLowerCase();
        int p = text.indexOf("%");
        if (p != -1 && text.length() - p > 9) {
            text = text.substring(p, p + 9);
        }
        return Utf8codeCheck(text);
    }

    public static String decode1(String url){
        int i = 0;
        int index = 0;
        int len = (url.getBytes().length)*5/8+1;
        int offset = 0;
        byte[] b = new byte[len];
        for(i=0,index=0,offset=0;i<url.length();i++){
            int k = url.charAt(i) - 0x30;
            if((k < 0) || (k>= base32Lookup.length)){
                continue;
            }
            int digit = base32Lookup[k];
            if(digit == 0xFF){
                continue;
            }

            if(index <= 3){
                index = (index + 5) % 8;
                if (index == 0){
                    b[offset] |= digit;
                    offset ++;
                    if (offset >= len){
                        break;
                    }
                }
                else{
                    b[offset] |= (digit << (8 - index));
                }
            }
            else{
                index = (index + 5) % 8;
                b[offset] |= (digit >> index);
                offset ++;
                if (offset >= len){
                    break;
                }
                b[offset] |= (digit << (8 - index));
            }
        }
        String str = new String();
        try{
            str = new String(b, "gbk");
        }catch(UnsupportedEncodingException e){}
            return str;
    }

    /**
    * 测试
    * @param args
    */

    public static void main(String[] args) {
        String strUrl;
        //strUrl = "http://www.google.com/search?hl=zh-CN&newwindow=1&q=%E4%B8%AD%E5%9B%BD%E5%A4%A7%E7%99%BE%E7%A7%91%E5%9C%A8%E7%BA%BF%E5%85%A8%E6%96%87%E6%A3%80%E7%B4%A2&btnG=%E6%90%9C%E7%B4%A2&lr=";
        try{
            strUrl = "http%3A//www.google.com/search%3Fhl%3Dzh-CN%26q%3D%25E6%25B3%2595%25E5%259B%25BD%25E5%25AE%25A2%25E9%2582%25AE%25E7%25A5%25A8%26btnG%3DGoogle+%25E6%2590%259C%25E7%25B4%25A2%26lr%3D";
            strUrl = URLDecoder.decode(strUrl,"UTF-8");
            //System.out.println(Utf8URLdecode(strUrl));
            if(isUtf8Url(strUrl)){
                System.out.println(Utf8URLdecode(strUrl));
            }
            else{
                System.out.println(URLDecoder.decode(strUrl,"UTF-8"));
            }
            //strUrl = "http://www.baidu.com/baidu?word=%D6%D0%B9%FA%B4%F3%B0%D9%BF%C6%D4%DA%CF%DF%C8%AB%CE%C4%BC%EC%CB%F7&tn=myie2dg";
            strUrl = "http%3A//www.baidu.com/s%3Fwd%3D%25CC%25D4%25B1%25A6%26lm%3D0%26si%3D%26rn%3D10%26tn%3Dindex88_pg%26ie%3Dgb2312%26ct%3D0%26cl%3D3%26f%3D12%26oq%3D%25CC%25CD%25B1%25A6";
            strUrl = URLDecoder.decode(strUrl,"UTF-8");
            if(isUtf8Url(strUrl)){
                System.out.println(Utf8URLdecode(strUrl));
            }
            else{
                System.out.println(URLDecoder.decode(strUrl,"UTF-8"));
            }

            //strUrl = "http%3A//www.sogou.com/sohu%3Fquery%3D%25B7%25B6%25D0%25C2%25D3%25EE%26name%3D%26rturl%3D%26ds%3D%26provider%3D%26md%3D%26dtype%3D%26key%3D%26page%3D65%26p%3D01040100%26dp%3D1%26w%3D01999999%26dr%3D1";
            strUrl = "http%3A//www.soso.com/q%3Fw%3D%u6DD8%u5B9D%26cid%3Dt.s";
            strUrl = URLDecoder.decode(strUrl,"UTF-8");
            if(isUtf8Url(strUrl)){
                System.out.println(Utf8URLdecode(strUrl));
            }
            else{
                System.out.println(URLDecoder.decode(strUrl,"UTF-8"));
            }
		}
		catch(UnsupportedEncodingException e){
		    System.out.println("UnsupportedEncodingException");
		}
		catch(Exception e){
		    System.out.println("error");
		}
    }

}
