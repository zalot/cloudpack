package com.taobao.mrsstd.hiveudf.calculate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.taobao.mrsstd.hiveudf.util.Constants;
import com.taobao.mrsstd.hiveudf.util.UDFUtil;

public class StringCalculator {

	private Map<String, String> mapFields;

	private String expression = null;

	public StringCalculator(Map<String, String> mapFields, String expression) {
		this.mapFields = mapFields;
		this.expression = expression;
	}

	/**
	 * 计算不带括号的字符串表达式
	 * 
	 * @param subStr
	 * @return
	 */
	public String calculate() {
		List<String> suffix = createReversePolishExpr(expression);
		Stack<String> stack = new Stack<String>();
		for (int i = 0; i < suffix.size(); i++) {
			if (!Operator.isOperator(suffix.get(i), Constants.MODE_STRING)) {
				stack.push(suffix.get(i));
			} else {
				//处理二元运算符
				if (Operator.getOperatorNum(suffix.get(i)) == 2) {
					String current = stack.top();
					stack.pop();
					String previous = null;
					if (stack.count != 0) {
						previous = stack.top();
						stack.pop();
					}
					String result = calculate(suffix.get(i), previous, current);
					stack.push(result);
				} else if (Operator.getOperatorNum(suffix.get(i)) == 1) {
					String current = stack.top();
					stack.pop();
					String result = calculate(suffix.get(i), null, current);
					stack.push(result);
				}
			}
		}

		return stack.top().toString();
	}

	/**
	 * 由中缀表达式生成逆波兰式(后缀表达式)
	 * 
	 * @param 中缀表达式
	 * @return 后缀表达式
	 */
	public List<String> createReversePolishExpr(String subStr) {
		// 替换所有字符串
		String regexStr = Constants.REGEX_STRING;
		String[] strings = matcher(regexStr, subStr);
		String changeStr = subStr.replaceAll(regexStr, String.valueOf(Constants.STRING));

		// 替换所有字段名
		List<String> listString = new ArrayList<String>();
		changeStr = UDFUtil.replaceFields(mapFields, changeStr, listString);
		String[] fields = new String[listString.size()];
		for(int i = 0; i < listString.size(); i++)
			fields[i] = listString.get(i);

		char[] chars = changeStr.toCharArray();
		int idxStr = 0;
		int idxField = 0;
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < chars.length; i++) {
			if (Constants.STRING == chars[i]) {
				// 字符串
				list.add(strings[idxStr++]);
			} else if (Constants.FIELD == chars[i]) {
				list.add(mapFields.get(fields[idxField++]));
			} else if (Constants.SPACE == chars[i]) {
				// 空格跳过
				continue;
			} else if (i + 2 < chars.length
					&& Operator.isOperator(new char[] { chars[i], chars[i + 1],
							chars[i + 2] }, Constants.MODE_STRING)) {
				// 三位运算符
				list.add(changeStr.substring(i, i + 3));
				i = i + 2;
			} else if (i + 1 < chars.length
					&& Operator.isOperator(new char[] { chars[i], chars[i + 1]}, Constants.MODE_STRING)) {
				// 两位运算符
				list.add(changeStr.substring(i, i + 2));
				i = i + 1;
			} else if (Operator.isOperator(chars[i], Constants.MODE_STRING)) {
				// 一位运算符
				list.add(String.valueOf(chars[i]));
			} else {
				throw new RuntimeException("not supported format: " + changeStr.substring(i, changeStr.length()));
			}
		}

		List<String> suffix = new ArrayList<String>();
		Stack<String> operator = new Stack<String>();
		for (int i = 0; i < list.size(); i++) {
			String element = list.get(i);
			if (!Operator.isOperator(element, Constants.MODE_STRING)) {
				suffix.add(element);
			} else {
				if (operator.count == 0) {
					operator.push(element);
				} else {
					while (operator.count != 0
							&& Operator.compare(operator.top(), element) >= 0) {
						String top = operator.top();
						operator.pop();
						suffix.add(top);
					}
					operator.push(element);
				}
			}
		}

		while (operator.count != 0) {
			suffix.add(operator.top());
			operator.pop();
		}

		return suffix;
	}

	private String[] matcher(String regex, String str) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(str);
		List<String> list = new ArrayList<String>();
		while (matcher.find()) {
			//剥去前后的单引号
			String temp = matcher.group();
			temp = temp.substring(1, temp.length() - 1);
			list.add(temp);
		}
		String[] result = new String[list.size()];
		return list.toArray(result);
	}

	private String calculate(String op, String previous, String current) {
		if (String.valueOf(Constants.SIGN_ADD).equals(op)) {
			return previous + current;
		}
		
		if (String.valueOf(Constants.LOGIC_EQUAL).equals(op)) {
			int result = previous.compareTo(current);
			if (result == 0)
				return Constants.TRUE;
			else
				return Constants.FALSE;
		}
		
		if (String.valueOf(Constants.LOGIC_GREAT).equals(op)) {
			int result = previous.compareTo(current);
			if (result > 0)
				return Constants.TRUE;
			else
				return Constants.FALSE;
		}
		
		if (String.valueOf(Constants.LOGIC_LESS).equals(op)) {
			int result = previous.compareTo(current);
			if (result < 0)
				return Constants.TRUE;
			else
				return Constants.FALSE;
		}
		
		if (String.valueOf(Constants.LOGIC_GREATEQUAL).equals(op)) {
			int result = previous.compareTo(current);
			if (result >= 0)
				return Constants.TRUE;
			else
				return Constants.FALSE;
		}
		
		if (String.valueOf(Constants.LOGIC_LESSEQUAL).equals(op)) {
			int result = previous.compareTo(current);
			if (result <= 0)
				return Constants.TRUE;
			else
				return Constants.FALSE;
		}
		
		if (String.valueOf(Constants.LOGIC_NOTEQUAL).equals(op)) {
			int result = previous.compareTo(current);
			if (result != 0)
				return Constants.TRUE;
			else
				return Constants.FALSE;
		}
		
		if (String.valueOf(Constants.LOGIC_OR).equals(op)) {
			if (previous.equals(Constants.FALSE) && current.equals(Constants.FALSE))
				return Constants.FALSE;
			else
				return Constants.TRUE;
		}
		
		if (String.valueOf(Constants.LOGIC_AND).equals(op)) {
			if (previous.equals(Constants.FALSE) || current.equals(Constants.FALSE))
				return Constants.FALSE;
			else
				return Constants.TRUE;
		}
		
		if (String.valueOf(Constants.LOGIC_NOT).equals(op)) {
			if (current.equals(Constants.FALSE))
				return Constants.TRUE;
			else
				return Constants.FALSE;
		}

		return null;
	}
}
