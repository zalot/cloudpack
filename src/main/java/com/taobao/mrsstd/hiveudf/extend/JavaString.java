package com.taobao.mrsstd.hiveudf.extend;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.taobao.mrsstd.hiveudf.util.UDFUtil;

public class JavaString extends Parent {

	private static Map<String, List<Integer>> mapTypes = new HashMap<String, List<Integer>>();
	private static Map<String, Method> mapMethods = new HashMap<String, Method>();
	private static Map<String, Integer> mapReturns = new HashMap<String, Integer>();

	static {
		try {
			Class<?> string = Class.forName(String.class.getName());
			Method[] methods = string.getDeclaredMethods();
			for (Method method : methods) {
				String name = method.getName();
				List<Integer> listTypes = new ArrayList<Integer>();

				boolean flagSupport = true;
				Type[] types = method.getGenericParameterTypes();
				for (Type type : types) {
					flagSupport = JavaType.addTypeValue(type, listTypes);
					if (!flagSupport) {
						System.out.println("method: " + name + " missing param type: " + type.toString());
						flagSupport = false;
						break;
					}
				}
				
				if (!flagSupport)
					continue;
				
				
				String namekey = name + "_" + listTypes.size();
				namekey = namekey.toLowerCase();
				if (mapTypes.get(namekey) != null) {
					List<Integer> listTypesold = mapTypes.get(namekey);
					int valueComplexity = 0;
					for (Integer complexity : listTypesold)
						valueComplexity += complexity;
					for (Integer complexity : listTypes)
						valueComplexity -= complexity;
					if (valueComplexity >= 0)
						continue;
				}

				Type type = method.getGenericReturnType();
				flagSupport = JavaType.putTypeValue(type, mapReturns, namekey);
				if (!flagSupport) {
					System.out.println("method: " + name + " missing return type: " + type.toString());
					continue;
				}
				
				mapTypes.put(namekey, listTypes);
				mapMethods.put(namekey, method);
			}

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
	}

	public String evaluate(String function, String... values) {
		try {
			String functionkey = function + "_" + (values.length - 1);
			List<Integer> listTypes = mapTypes.get(functionkey);
			if (listTypes == null) {
				System.out.println("function key not found: " + functionkey);
				return null;
			} else {
				int index = 1;
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
						return null;
					}
				}

				Object[] objParams = new Object[listParams.size()];
				index = 0;
				for (Object param : listParams) {
					objParams[index++] = param;
				}
				Method method = mapMethods.get(functionkey);
				Object result = method.invoke(values[0], objParams);
				return UDFUtil.delZero(String.valueOf(result));
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static void main(String[] args) {
		JavaString reflect = new JavaString();
		long time1 = System.currentTimeMillis();
		for (int i = 0; i < 1000000; i++) {
			// System.out.println(reflect.evaluate("pow", "2", "3"));
			// reflect.evaluate("sin", "0.897");
			reflect.evaluate("indexOf", "123", "23", "0");
			//System.out.println(reflect.evaluate("indexOf", "123", "23", "0"));
		}
		long time2 = System.currentTimeMillis();
		System.out.println("cost time: " + (time2 - time1));
	}

}
