package com.taobao.mrsstd.hiveudf.calculate;

import java.util.Map;

import com.taobao.mrsstd.hiveudf.extend.Parent;
import com.taobao.mrsstd.hiveudf.util.Constants;

public class ExtendCalculator {

	/**
	 * values为一个个的参数，以逗号分隔
	 */
	private String values;
	
	/**
	 * 对应的扩展的类名及函数名，以点号分隔，如class.function
	 */
	private String extension;
	
	/**
	 * 字段map
	 */
	private Map<String, String> mapValues;
	
	public ExtendCalculator(String extension, Map<String, String> mapValues, String values) {
		this.values = values;
		this.mapValues = mapValues;
		this.extension = extension;
	}
	
	public String compute() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		String classname = null;
		String function = null;
		if (extension.indexOf(Constants.DOT) != -1) {
			String[] strs = extension.split(Constants.REGEX_DOT);
			classname = strs[0];
			function = strs[1];
		} else {
			classname = extension;
		}
		
		if (classname.equalsIgnoreCase("JavaMath")) {
			classname = "JavaMath";
		} else if (classname.equalsIgnoreCase("JavaString")) {
			classname = "JavaString";
		} else if (classname.equalsIgnoreCase("JavaMathDirect")) {
			classname = "JavaMathDirect";
		} else if (classname.equalsIgnoreCase("JavaCHGIdx")) {
			classname = "JavaCHGIdx";
		} else if (classname.equalsIgnoreCase("JavaCHGRatio")) {
			classname = "JavaCHGRatio";
		}
		Object object = Class.forName(Constants.EXTEND_PACKAGE + classname).newInstance();
		Parent parent = (Parent) object;
		
		String[] arrValue = values.split(Constants.COMMA);
		for(int i = 0; i < arrValue.length; i++) {
			String value = arrValue[i];
			
			String field = mapValues.get(value.toLowerCase().trim());
			if(field != null) {
				arrValue[i] = field;
			} else if (mapValues.containsKey(value.toLowerCase().trim())) {
				arrValue[i] = null;
			}
		}
		String result = parent.evaluate(function, arrValue);
		
		return result;
	}
}

