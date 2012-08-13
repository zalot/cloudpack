package com.taobao.mrsstd.hiveudf.extend;

public class Constants {

	public static final String EMPTY = "";
	public static final String REGEX_DELZERO = "\\.0+$";
	
	/*
	 * 类型
	 */
	public static final String TYPE_STRING = "class java.lang.String";
	public static final String TYPE_ARRAY_CHAR = "class [C";
	public static final String TYPE_LOCALE = "class java.util.Locale";
	public static final String TYPE_DOUBLE = "double";
	public static final String TYPE_FLOAT = "float";
	public static final String TYPE_LONG = "long";
	public static final String TYPE_INT = "int";
	public static final String TYPE_CHAR = "char";
	public static final String TYPE_BOOLEAN = "boolean";
	public static final String TYPE_VOID = "void";
	
	/*
	 * 类型复杂度 
	 */
	public static final int VALUE_STRING = 9;
	public static final int VALUE_ARRAY_CHAR = 8;
	public static final int VALUE_LOCALE = 7;
	public static final int VALUE_DOUBLE = 6;
	public static final int VALUE_FLOAT = 5;
	public static final int VALUE_LONG = 4;
	public static final int VALUE_INT = 3;
	public static final int VALUE_CHAR = 2;
	public static final int VALUE_BOOLEAN = 1;
	public static final int VALUE_VOID = 0;
}