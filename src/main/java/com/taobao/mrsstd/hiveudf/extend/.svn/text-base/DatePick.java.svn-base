package com.taobao.mrsstd.hiveudf.extend;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DatePick extends Parent {
	
	public String evaluate(String function, String... values) {
		String longDate = values[0];
		String format = values[1];
		Date date = new Date(Long.parseLong(longDate));
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		return formatter.format(date);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DatePick pick = new DatePick();
		System.out.println(pick.evaluate("" + System.currentTimeMillis(), "HH"));
	}

}