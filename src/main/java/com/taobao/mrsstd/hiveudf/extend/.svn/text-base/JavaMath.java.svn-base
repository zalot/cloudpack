package com.taobao.mrsstd.hiveudf.extend;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.taobao.mrsstd.hiveudf.util.UDFUtil;

public class JavaMath extends Parent {

	private static Map<String, List<Integer>> mapTypes = new HashMap<String, List<Integer>>();
	private static Map<String, Method> mapMethods = new HashMap<String, Method>();
	private static Map<String, Integer> mapReturns = new HashMap<String, Integer>();

	static {
		try {
			Class<?> math = Class.forName(Math.class.getName());
			Method[] methods = math.getDeclaredMethods();
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

				mapTypes.put(namekey, listTypes);
				mapMethods.put(namekey, method);

				Type type = method.getGenericReturnType();
				flagSupport = JavaType.putTypeValue(type, mapReturns, namekey);
				if (!flagSupport) {
					System.out.println("method: " + name + " missing return type: " + type.toString());
					continue;
				}
			}

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
	}

	public String evaluate(String function, String... values) {
		try {
			String functionkey = function + "_" + values.length;
			List<Integer> listTypes = mapTypes.get(functionkey);
			if (listTypes == null) {
				System.out.println("function key not found: " + functionkey);
				return null;
			} else {
				int index = 0;
				Object[] objParams = JavaType.generateObjParams(listTypes, index, values);
				Method method = mapMethods.get(functionkey);
				Object result = method.invoke(null, objParams);
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
		JavaMath reflect = new JavaMath();
		long time1 = System.currentTimeMillis();
		for (int i = 0; i < 1000000; i++) {
			// System.out.println(reflect.evaluate("pow", "2", "3"));
			// reflect.evaluate("sin", "0.897");
			reflect.evaluate("abs", "23");
		}
		long time2 = System.currentTimeMillis();
		System.out.println("cost time: " + (time2 - time1));
	}

}
