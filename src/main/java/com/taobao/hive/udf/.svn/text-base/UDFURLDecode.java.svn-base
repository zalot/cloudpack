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

import java.net.URLDecoder;

import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.Text;

public class UDFURLDecode extends UDF {
  Text dstURL;

  public UDFURLDecode() {
    this.dstURL = new Text();
  }

  public Text evaluate(Text urlText) {
    if(urlText == null) {
      return null;
    }
    String url = urlText.toString();
    if (url != null) {
      try {
        url = url.trim();
        if (isUtf8Url(url)) {
          url = URLDecoder.decode(url, "UTF-8");
        } else {
          url = URLDecoder.decode(url, "GBK");
        }
      } catch (Exception e) {
    	  try{
    		  url = evaluate(urlText, new Text("jsescape")).toString();
    	  }catch(Exception ee){
    	      dstURL.set(url);
    	      return dstURL;
    	  }
      }
    }
    dstURL.set(url);
    return dstURL;

  }
  
  public Text evaluate(Text urlText,String flag1,String flag2) {
	    if(urlText == null) {
	      return null;
	    }
	    String url = urlText.toString();
	    if (url != null) {
	      try {
	        url = url.trim();
	        if (isgbk(url)) {
				url = URLDecoder.decode(url, "GBK");
			} else {
				url = URLDecoder.decode(url, "UTF-8");
			}
	      } catch (Exception e) {
	    	  try{
	    		  url = evaluate(urlText, new Text("jsescape")).toString();
	    	  }catch(Exception ee){
	    	      dstURL.set(url);
	    	      return dstURL;
	    	  }
	      }
	    }
	    dstURL.set(url);
	    return dstURL;

	  }

  public boolean isUtf8Url(String text) {
    text = text.toLowerCase();
    int p = text.indexOf("%");
    if (p != -1 && text.length() - p > 9) {
      text = text.substring(p, p + 9);
    }
    return Utf8codeCheck(text);
  }

  
  
  static private boolean isgbk(String str){
		String substr = get4str(str);
		if(substr.length() < 4){
			return false;
		}
		String str1 = substr.trim().substring(0,2);
		String str2 = substr.trim().substring(2,4);
		
		int value1 = Integer.decode("0x" + str1).intValue();
		int value2 = Integer.decode("0x" + str2).intValue();
		
		if(value1 >= 129 && value1 <= 254){
			if(value2 >= 64 && value2 <= 254)
				return true;
		}
		return false;
	}
  static private String get4str(String str){
      String substr = new String();
      String substr1 = new String();
      if(str.length()<2)
         return "";
      int index = str.indexOf("%");
      
      substr = get4substr(str.trim(), index);
      if(substr.length() < 5)
         return "";

      if(substr.substring(2,3).equals("%") == true){
         substr1 = substr.substring(0,2)+substr.substring(3);
      }
      else{
         return "";
      }
      return substr1;
   }
   
   static private String get4substr(String str, int index){
      if(index == -1)
         return "";
      if(str.substring(index).length() <= 1)
         return "";
      
      String substr = new String();
      int i = 0;
      while(index+2 < str.length()){
         substr = str.substring(index);
         if(substr.startsWith("%") == true){
            substr = str.substring(index + 1);
            index ++;
         }
         if(Integer.decode("0x" + substr.substring(0,2)).intValue() >= 128)
            break;
         i = substr.indexOf("%");
         if(i == -1)
            break;
         index += i;
      }
      return substr;
   }


  
  
  
  private boolean Utf8codeCheck(String text) {
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

  public Text evaluate(Text srcURL, Text enc) {
    if (srcURL == null)
      return null;
    if (enc == null)
      enc = new Text("UTF8");
    String srcURLString = srcURL.toString();
    String encString = enc.toString();
    dstURL.clear();
    if (encString.toLowerCase().equals("jsescape")
        || encString.toLowerCase().equals("js_escape")) {
      dstURL.set(Escape.unescape(srcURLString));
      return dstURL;
    }
    try {
      dstURL.set(URLDecoder.decode(srcURLString, encString));
    } catch (Exception e) {
      dstURL.set(srcURL);
    }
    return dstURL;
  }

  private static class Escape {
    private final static String[] hex = { "00", "01", "02", "03", "04", "05",
        "06", "07", "08", "09", "0A", "0B", "0C", "0D", "0E", "0F", "10", "11",
        "12", "13", "14", "15", "16", "17", "18", "19", "1A", "1B", "1C", "1D",
        "1E", "1F", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29",
        "2A", "2B", "2C", "2D", "2E", "2F", "30", "31", "32", "33", "34", "35",
        "36", "37", "38", "39", "3A", "3B", "3C", "3D", "3E", "3F", "40", "41",
        "42", "43", "44", "45", "46", "47", "48", "49", "4A", "4B", "4C", "4D",
        "4E", "4F", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59",
        "5A", "5B", "5C", "5D", "5E", "5F", "60", "61", "62", "63", "64", "65",
        "66", "67", "68", "69", "6A", "6B", "6C", "6D", "6E", "6F", "70", "71",
        "72", "73", "74", "75", "76", "77", "78", "79", "7A", "7B", "7C", "7D",
        "7E", "7F", "80", "81", "82", "83", "84", "85", "86", "87", "88", "89",
        "8A", "8B", "8C", "8D", "8E", "8F", "90", "91", "92", "93", "94", "95",
        "96", "97", "98", "99", "9A", "9B", "9C", "9D", "9E", "9F", "A0", "A1",
        "A2", "A3", "A4", "A5", "A6", "A7", "A8", "A9", "AA", "AB", "AC", "AD",
        "AE", "AF", "B0", "B1", "B2", "B3", "B4", "B5", "B6", "B7", "B8", "B9",
        "BA", "BB", "BC", "BD", "BE", "BF", "C0", "C1", "C2", "C3", "C4", "C5",
        "C6", "C7", "C8", "C9", "CA", "CB", "CC", "CD", "CE", "CF", "D0", "D1",
        "D2", "D3", "D4", "D5", "D6", "D7", "D8", "D9", "DA", "DB", "DC", "DD",
        "DE", "DF", "E0", "E1", "E2", "E3", "E4", "E5", "E6", "E7", "E8", "E9",
        "EA", "EB", "EC", "ED", "EE", "EF", "F0", "F1", "F2", "F3", "F4", "F5",
        "F6", "F7", "F8", "F9", "FA", "FB", "FC", "FD", "FE", "FF" };

    private final static byte[] val = { 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
        0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
        0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
        0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
        0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x00, 0x01, 0x02, 0x03, 0x04, 0x05,
        0x06, 0x07, 0x08, 0x09, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x0A,
        0x0B, 0x0C, 0x0D, 0x0E, 0x0F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
        0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
        0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E,
        0x0F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
        0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
        0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
        0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
        0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
        0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
        0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
        0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
        0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
        0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
        0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
        0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
        0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F };

    public static String escape(String s) {
      StringBuffer sbuf = new StringBuffer();
      int len = s.length();
      for (int i = 0; i < len; i++) {
        int ch = s.charAt(i);
        if ('A' <= ch && ch <= 'Z') {
          sbuf.append((char) ch);
        } else if ('a' <= ch && ch <= 'z') {
          sbuf.append((char) ch);
        } else if ('0' <= ch && ch <= '9') {
          sbuf.append((char) ch);
        } else if (ch == '-' || ch == '_' || ch == '.' || ch == '!'
            || ch == '~' || ch == '*' || ch == '\'' || ch == '(' || ch == ')') {
          sbuf.append((char) ch);
        } else if (ch <= 0x007F) {
          sbuf.append('%');
          sbuf.append(hex[ch]);
        } else {
          sbuf.append('%');
          sbuf.append('u');
          sbuf.append(hex[(ch >>> 8)]);
          sbuf.append(hex[(0x00FF & ch)]);
        }
      }
      return sbuf.toString();
    }

    public static String unescape(String s) {
      StringBuffer sbuf = new StringBuffer();
      int i = 0;
      int len = s.length();
      while (i < len) {
        int ch = s.charAt(i);
        if ('A' <= ch && ch <= 'Z') {
          sbuf.append((char) ch);
        } else if ('a' <= ch && ch <= 'z') {
          sbuf.append((char) ch);
        } else if ('0' <= ch && ch <= '9') {
          sbuf.append((char) ch);
        } else if (ch == '-' || ch == '_' || ch == '.' || ch == '!'
            || ch == '~' || ch == '*' || ch == '\'' || ch == '(' || ch == ')') {
          sbuf.append((char) ch);
        } else if (ch == '%') {
          int cint = 0;
          if ('u' != s.charAt(i + 1)) {
            cint = (cint << 4) | val[s.charAt(i + 1)];
            cint = (cint << 4) | val[s.charAt(i + 2)];
            i += 2;
          } else {
            cint = (cint << 4) | val[s.charAt(i + 2)];
            cint = (cint << 4) | val[s.charAt(i + 3)];
            cint = (cint << 4) | val[s.charAt(i + 4)];
            cint = (cint << 4) | val[s.charAt(i + 5)];
            i += 5;
          }
          sbuf.append((char) cint);
        } else {
          sbuf.append((char) ch);
        }
        i++;
      }
      return sbuf.toString();
    }
  }
  
  
  public static void main(String[] args){
	  UDFURLDecode test = new UDFURLDecode();
	  System.out.println(test.evaluate(new Text("sdfawerwesdf%%%%3Bl23"), "1", "2"));
	  
	  
	  System.out.println(test.evaluate(new Text("/1.gif?cache=493957&pre=http%3A//shop20840091.taobao.com/%3Fsearch%3Dy%26scid%3D150356409%26queryType%3Dcat%26categoryName%3D%u79CB%u51AC%u5B55%u5987%u5916%u5957%20%u536B%u8863&q=%uC5%uAE%uD0%uAC+%uB5%uA5%uD0%uAC&category=item_50012360&userid=&channel=112&at_isb=0&at_autype=5_20840091&ad_id="),"",""));
	  //System.out.println(test.evaluate(new Text("/1.gif?cache=493957&pre=http%3A//shop20840091.taobao.com/%3Fsearch%3Dy%26scid%3D150356409%26queryType%3Dcat%26categoryName%3D%u79CB%u51AC%u5B55%u5987%u5916%u5957%20%u536B%u8863&scr=1024x768&category=item_50012360&userid=&channel=112&at_isb=0&at_autype=5_20840091&ad_id="),new Text("jsescape")));  
	  System.out.println(test.evaluate(new Text("http://s.taobao.com/search?q=%C5%AE%D0%AC+%B5%A5%D0%AC"),"",""));
	  
	  
	  
	  
	  String url = "/1.gif?cache=2197012&pre=http%3A//s8.taobao.com/search%3Fspm%3D161.87171.128118.8%26sort%3Dsale-desc%26q%3D%u58C1%u753B+%u58C1%u7EB8%26pid%3Dmm_15834631_0_0&scr=1024x768&category=item_50013323&userid=112388373&tid=464396d887776792c182887035347120&channel=112&at_isb=1&at_autype=&b2c_pgtype=2&b2c_prod=124238262&b2c_brand=65340754&b2c_auction=8547067393&ad_id=5615768288093690479 http://item.tmall.com/item.htm?id=8547067393&ref=http://s8.taobao.com/search?spm=161.87171.128118.8&sort=sale-desc&q=\\xb1\\xda\\xbb\\xad+\\xb1\\xda\\xd6\\xbd&pid=mm_15834631_0_0&prc=1&ali_trackid=2:mm_15834631_0_0,0:1313466161_m13_783975";
	  System.out.println(test.evaluate(new Text(url)));
	  
	  
	  
	  
  }

}
