package com.taobao.ad.data.search.udf;

import java.io.*;

/*
 * Table for GBK(Traditional) to GBK(Simplified).
 * C1: 0x81-0xFE, C2: 0x40-0xFE.
 * 2000-5-30 21:23.
 * 获取繁体简体对应表
 */
class Gbk2GbkSimplified {
	public int[] gbkTable;

	private Gbk2GbkSimplified() {
		gbkTable = new int[24066 * 2];
		BufferedReader buf = null;
		try {
			buf = new BufferedReader(new FileReader(new File(Gbk2GbkSimplified.class.getResource("").getPath()+"/gbk.txt")));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (buf == null)
			return;
		String line = null;
		try {
			int count = 0;
			while ((line = buf.readLine()) != null) {
				String tmp = line.substring(11, 20);
				String[] strlist = tmp.split(",");	
				gbkTable[count++] = Integer.valueOf(strlist[0].substring(2), 16);
				gbkTable[count++] = Integer.valueOf(strlist[1].substring(2), 16);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	private static Gbk2GbkSimplified instance = null;

	public static synchronized Gbk2GbkSimplified getInstance() {
		if (instance == null) {
			instance = new Gbk2GbkSimplified();
		}
		return instance;
	}
}