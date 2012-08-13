package com.taobao.hive.util;

import java.net.URL;

import com.taobao.hive.util.FastSearch.Mode;

public class UDFStringUtil {
	public static final int indexOf(String s, String p) {
		if (s == null || p == null) return -1;
	    byte[] sBytes = s.getBytes();
	    byte[] pBytes = p.getBytes();
	    return FastSearch.fastSearch(sBytes, sBytes.length, pBytes, pBytes.length, -1, Mode.FAST_SEARCH);
	}
	
	public static final int indexOf(String s, String p, int fromIndex) {
		if (s == null || p == null) return -1;
		s = s.substring(fromIndex);
	    byte[] sBytes = s.getBytes();
	    byte[] pBytes = p.getBytes();
	    return FastSearch.fastSearch(sBytes, sBytes.length, pBytes, pBytes.length, -1, Mode.FAST_SEARCH);
	}
	
	public static final String valueOf(String s, String p, String splitor, String asignS) {
		//p***
		//[splitor]p***
		if (asignS == null) return null;
		splitor = splitor == null ? "" : splitor;
		int index = indexOf(s,splitor + p + asignS);
		String substr = null;
		if (index > -1) {
			substr = s.substring(index + splitor.length() + p.length() + asignS.length());
		} else {
			index = indexOf(s,p + asignS);
			if (index != 0) return null;
			substr = s.substring(p.length() + asignS.length());
		}
		int endIdx = indexOf(substr, splitor);
		endIdx = endIdx == -1 ? substr.length() : endIdx;
		return substr.substring(0,endIdx);
	}
	
	public static final String valueOf(String s, String p, String splitor) {
		//p***
		//[splitor]p***
		splitor = splitor == null ? "" : splitor;
		int index = indexOf(s,splitor + p);
		String substr = null;
		if (index > -1) {
			substr = s.substring(index + splitor.length() + p.length());
		} else {
			index = indexOf(s,p);
			if (index != 0) return null;
			substr = s.substring(p.length());
		}
		int endIdx = indexOf(substr, splitor);
		endIdx = endIdx == -1 ? substr.length() : endIdx;
		return substr.substring(0,endIdx);
	}
	
	public static final String getURLValue(String s, String p, String splitor) {
		//?p[splitor]
		//[splitor]p[splitor]
		if (splitor == null || s == null || p == null || "".equals(p)) return null;
		int index = indexOf(s,splitor + p);
		int seq = splitor.length() + p.length();
		if (index == -1) {
			index = indexOf(s,"?" + p);
			if (index == -1) return null;
			seq = p.length() + 1;
		}
		String substr = s.substring(index + seq);
		int endIdx = indexOf(substr, splitor);
		endIdx = endIdx == -1 ? substr.length() : endIdx;
		return substr.substring(0,endIdx);
	}
	
	public static final String getURLValue(String url, String query) {
		if (url == null || query == null || "".equals(url) || "".equals(query)) return null;
		URL _url = null;
		try {
			_url = new URL(url);
	    } catch (Exception e) {
	        return null;
	    }
	    String _query = _url.getQuery();
	    int index = indexOf(_query,query);
		return null;
	}
	
	
}
