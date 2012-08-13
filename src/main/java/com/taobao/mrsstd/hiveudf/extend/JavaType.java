package com.taobao.mrsstd.hiveudf.extend;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class JavaType {

	public static boolean addTypeValue(Type type, List<Integer> listTypes) {
		boolean flagFound = false;
		
		if (Constants.TYPE_STRING.equalsIgnoreCase(type.toString())) {
			listTypes.add(Constants.VALUE_STRING);
			flagFound = true;
		} else if (Constants.TYPE_ARRAY_CHAR.equalsIgnoreCase(type.toString())) {
			listTypes.add(Constants.VALUE_ARRAY_CHAR);
			flagFound = true;
		} else if (Constants.TYPE_LOCALE.equalsIgnoreCase(type.toString())) {
			listTypes.add(Constants.VALUE_LOCALE);
			flagFound = true;
		} else if (Constants.TYPE_DOUBLE.equalsIgnoreCase(type.toString())) {
			listTypes.add(Constants.VALUE_DOUBLE);
			flagFound = true;
		} else if (Constants.TYPE_FLOAT.equalsIgnoreCase(type.toString())) {
			listTypes.add(Constants.VALUE_FLOAT);
			flagFound = true;
		} else if (Constants.TYPE_LONG.equalsIgnoreCase(type.toString())) {
			listTypes.add(Constants.VALUE_LONG);
			flagFound = true;
		} else if (Constants.TYPE_INT.equalsIgnoreCase(type.toString())) {
			listTypes.add(Constants.VALUE_INT);
			flagFound = true;
		} else if (Constants.TYPE_CHAR.equalsIgnoreCase(type.toString())) {
			listTypes.add(Constants.VALUE_CHAR);
			flagFound = true;
		} else if (Constants.TYPE_BOOLEAN.equalsIgnoreCase(type.toString())) {
			listTypes.add(Constants.VALUE_BOOLEAN);
			flagFound = true;
		} else if (Constants.TYPE_VOID.equalsIgnoreCase(type.toString())) {
			listTypes.add(Constants.VALUE_VOID);
			flagFound = true;
		}
		
		return flagFound;
	}
	
	public static boolean putTypeValue(Type type, Map<String, Integer> map, String key) {
		boolean flagFound = false;
		
		if (Constants.TYPE_STRING.equalsIgnoreCase(type.toString())) {
			map.put(key, Constants.VALUE_STRING);
			flagFound = true;
		} else if (Constants.TYPE_ARRAY_CHAR.equalsIgnoreCase(type.toString())) {
			map.put(key, Constants.VALUE_ARRAY_CHAR);
			flagFound = true;
		} else if (Constants.TYPE_LOCALE.equalsIgnoreCase(type.toString())) {
			map.put(key, Constants.VALUE_LOCALE);
			flagFound = true;
		} else if (Constants.TYPE_DOUBLE.equalsIgnoreCase(type.toString())) {
			map.put(key, Constants.VALUE_DOUBLE);
			flagFound = true;
		} else if (Constants.TYPE_FLOAT.equalsIgnoreCase(type.toString())) {
			map.put(key, Constants.VALUE_FLOAT);
			flagFound = true;
		} else if (Constants.TYPE_LONG.equalsIgnoreCase(type.toString())) {
			map.put(key, Constants.VALUE_LONG);
			flagFound = true;
		} else if (Constants.TYPE_INT.equalsIgnoreCase(type.toString())) {
			map.put(key, Constants.VALUE_INT);
			flagFound = true;
		} else if (Constants.TYPE_CHAR.equalsIgnoreCase(type.toString())) {
			map.put(key, Constants.VALUE_CHAR);
			flagFound = true;
		} else if (Constants.TYPE_BOOLEAN.equalsIgnoreCase(type.toString())) {
			map.put(key, Constants.VALUE_BOOLEAN);
			flagFound = true;
		} else if (Constants.TYPE_VOID.equalsIgnoreCase(type.toString())) {
			map.put(key, Constants.VALUE_VOID);
			flagFound = true;
		}
		
		return flagFound;
	}
	
	public static Object[] generateObjParams(List<Integer> listTypes, int index,
			String... values) {
		List<Object> listParams = new ArrayList<Object>();
		for (Integer type : listTypes) {
			switch (type) {
			case Constants.VALUE_STRING:
				listParams.add(values[index++]);
				break;
			case Constants.VALUE_ARRAY_CHAR:
				listParams.add(values[index++].toCharArray());
				break;
			case Constants.VALUE_LOCALE:
				listParams.add(new Locale(values[index++]));
				break;
			case Constants.VALUE_DOUBLE:
				listParams.add(Double.valueOf(values[index++]));
				break;
			case Constants.VALUE_FLOAT:
				listParams.add(Float.valueOf(values[index++]));
				break;
			case Constants.VALUE_LONG:
				listParams.add(Long.valueOf(values[index++]));
				break;
			case Constants.VALUE_INT:
				listParams.add(Integer.valueOf(values[index++]));
				break;
			case Constants.VALUE_CHAR:
				listParams.add(values[index++].charAt(0));
				break;
			case Constants.VALUE_BOOLEAN:
				listParams.add(Boolean.parseBoolean(values[index++]));
				break;
			case Constants.VALUE_VOID:
				break;
			default:
				System.out.println("not supported param type value: "
						+ type);
			}
		}

		Object[] objParams = new Object[listParams.size()];
		index = 0;
		for (Object param : listParams) {
			objParams[index++] = param;
		}
		return objParams;
	}
	
	public static void main(String[] args) {
		
	}

}
