package com.taobao.mrsstd.hiveudf.util;

public class CodeUtil {
	
	public static boolean isGBK(String str) {
		
		byte[] b = str.getBytes();
	    if (b.length >= 2)
	    {
	    	int b1 = b[0] & 0xff;
	    	int b2 = b[1] & 0xff;
	        if (b1>=129 && b1<=254 && b2>=64 && b2<=254)
	            return true;
	        else return false;
	    }
	    else return false;
	}

}
