package com.taobao.mrsstd.hiveudf.util;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class UDFUtil {

	public static Map<String, String> getValueMap(String columns,
			String[] values) {
		Map<String, String> map = new HashMap<String, String>();

		if (columns == null || columns.trim().length() == 0)
			return map;

		String[] arrFields = columns.split(",");
		for (int i = 0; i < arrFields.length; i++) {
			map.put(arrFields[i].trim().toLowerCase(), values[i]);
		}

		return map;
	}

	public static String replaceFields(Map<String, String> mapFields,
			String changeStr, List<String> listString) {
		//按长度排序，先替换长的字段名
		Object[] keys = mapFields.keySet().toArray();
		Comparator<Object> comparator = new Comparator<Object>() {
			public int compare(Object o1, Object o2) {
				int length1 = ((String) o1).length();
				int length2 = ((String) o2).length();;
				if (length1 > length2)
					return -1;
				if (length1 == length2)
					return 0;
				else
					return 1;
			}
		};
		Arrays.sort(keys, comparator);

		while (true) {
			int minIndex = 10000;
			String field = null;
			for (Object obj : keys) {
				String name = (String) obj;
				int index = changeStr.toLowerCase().indexOf(name);
				if (index != -1 && index < minIndex) {
					minIndex = index;
					field = name;
				}
			}

			if (field != null) {
				changeStr = changeStr.replaceFirst(field, "F");
				listString.add(field);
			} else
				break;
		}
		return changeStr;
	}

	public static String reverse(String string) {
		StringBuffer buf = new StringBuffer();
		for (int i = string.length() - 1; i >= 0; i--) {
			buf.append(string.charAt(i));
		}
		return buf.toString();
	}

	public static String delZero(String str) {
		Double.parseDouble(str);

		str = str.replaceFirst(Constants.REGEX_DELZERO, Constants.EMPTY);
		boolean flagDot = false;
		boolean flagZero = true;
		int zerolength = 0;
		for (int i = str.length() - 1; i >= 0; i--) {
			if (flagZero && str.charAt(i) == '0') {
				zerolength++;
			} else if (str.charAt(i) == '.') {
				flagDot = true;
				flagZero = false;
			} else {
				flagZero = false;
			}
		}

		if (flagDot && zerolength > 0) {
			return str.substring(0, str.length() - zerolength);
		}

		return str;
	}
}
