package com.taobao.mrsstd.hiveudf.calculate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.taobao.mrsstd.hiveudf.util.Constants;
import com.taobao.mrsstd.hiveudf.util.UDFUtil;

public class MathCalculator {

	private Map<String, String> mapFields;

	private String expression = null;

	public MathCalculator(Map<String, String> mapFields, String expression) {
		this.mapFields = mapFields;
		this.expression = expression;
	}

	/**
	 * 计算不带括号的数学表达式
	 * 
	 * @param subStr
	 * @return
	 */
	public String calculate() {
		List<String> suffix = createReversePolishExpr(expression);
		Stack<Double> stack = new Stack<Double>();
		for (int i = 0; i < suffix.size(); i++) {
			if (!Operator.isOperator(suffix.get(i), Constants.MODE_NUMBER)) {
				stack.push(Double.valueOf(suffix.get(i)));
			} else {
				//处理二元运算符
				if (Operator.getOperatorNum(suffix.get(i)) == 2) {
					Double current = stack.top();
					stack.pop();
					Double previous = null;
					if (stack.count != 0) {
						previous = stack.top();
						stack.pop();
					} else {
						previous = new Double(0);
					}
					Double result = calculate(suffix.get(i), previous, current);
					stack.push(result);
				} else if (Operator.getOperatorNum(suffix.get(i)) == 1) {
					Double current = stack.top();
					stack.pop();
					Double result = calculate(suffix.get(i), null, current);
					stack.push(result);
				}
			}
		}

		return UDFUtil.delZero(stack.top().toString());
	}

	/**
	 * 由中缀表达式生成逆波兰式(后缀表达式)
	 * 
	 * @param 中缀表达式
	 * @return 后缀表达式
	 */
	public List<String> createReversePolishExpr(String subStr) {
		// 替换所有字段名
		List<String> listString = new ArrayList<String>();
		String changeStr = UDFUtil.replaceFields(mapFields, subStr, listString);
		String[] fields = new String[listString.size()];
		for(int i = 0; i < listString.size(); i++)
			fields[i] = listString.get(i);
		
		// 替换所有数值
		String regexDigit = Constants.REGEX_NUMBER;
		changeStr = changeStr.replace('\'', ' ');
		String[] numbers = matcher(regexDigit, changeStr);
		changeStr = changeStr.replaceAll(regexDigit, "0").replaceAll("\\-\\s*\\-0",
				"-1").replaceAll("\\+\\s*\\-0", "+1").replaceAll("\\*\\s*\\-0", "*1")
				.replaceAll("\\/\\s*\\-0", "/1");

		char[] chars = changeStr.toCharArray();
		int idxNum = 0;
		int idxField = 0;
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < chars.length; i++) {
			if (Constants.POS_NUM == chars[i]) {
				// 正数
				list.add(numbers[idxNum++]);
			} else if (Constants.NEG_NUM == chars[i]) {
				// 负数
				list.add("-" + numbers[idxNum++]);
			} else if (Constants.FIELD == chars[i]) {
				list.add(mapFields.get(fields[idxField++]));
			} else if (Constants.SPACE == chars[i]) {
				// 空格跳过
				continue;
			} else if (i + 2 < chars.length
					&& Operator.isOperator(new char[] { chars[i], chars[i + 1],
							chars[i + 2] }, Constants.MODE_NUMBER)) {
				// 三字节长运算符
				list.add(changeStr.substring(i, i + 3));
				i = i + 2;
			} else if (i + 1 < chars.length
					&& Operator.isOperator(new char[] { chars[i], chars[i + 1] }, Constants.MODE_NUMBER)) {
				// 两字节长运算符
				list.add(changeStr.substring(i, i + 2));
				i = i + 1;
			} else if (Operator.isOperator(chars[i], Constants.MODE_NUMBER)) {
				// 一字节长运算符
				list.add(String.valueOf(chars[i]));
			} else {
				throw new RuntimeException("not supported format: " + changeStr.substring(i, changeStr.length()));
			}
		}

		List<String> suffix = new ArrayList<String>();
		Stack<String> operator = new Stack<String>();
		for (int i = 0; i < list.size(); i++) {
			String element = list.get(i);
			if (!Operator.isOperator(element, Constants.MODE_NUMBER)) {
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
			String tmp = matcher.group();
			if (tmp.startsWith(Constants.SINGLE_QUOTE)) {
				tmp = tmp.substring(1, tmp.length());
			}
			if (tmp.endsWith(Constants.SINGLE_QUOTE)) {
				tmp = tmp.substring(0, tmp.length() - 1);
			}
			list.add(tmp);
		}
		String[] result = new String[list.size()];
		return list.toArray(result);
	}

	private Double calculate(String op, Double previous, Double current) {
		if (String.valueOf(Constants.SIGN_ADD).equals(op)) {
			return previous + current;
		}
		
		if (String.valueOf(Constants.SIGN_SUBTRACT).equals(op)) {
			return previous - current;
		}
		
		if (String.valueOf(Constants.SIGN_MULTIPLICATION).equals(op)) {
			return previous * current;
		}
		
		if (String.valueOf(Constants.SIGN_DIVIDE).equals(op)) {
			return previous / current;
		}
		
		if (String.valueOf(Constants.LOGIC_EQUAL).equals(op)) {
			int result = previous.compareTo(current);
			if (result == 0)
				return Double.valueOf(1);
			else
				return Double.valueOf(0);
		}
		
		if (String.valueOf(Constants.LOGIC_GREAT).equals(op)) {
			int result = previous.compareTo(current);
			if (result > 0)
				return Double.valueOf(1);
			else
				return Double.valueOf(0);
		}
		
		if (String.valueOf(Constants.LOGIC_LESS).equals(op)) {
			int result = previous.compareTo(current);
			if (result < 0)
				return Double.valueOf(1);
			else
				return Double.valueOf(0);
		}
		
		if (String.valueOf(Constants.LOGIC_GREATEQUAL).equals(op)) {
			int result = previous.compareTo(current);
			if (result >= 0)
				return Double.valueOf(1);
			else
				return Double.valueOf(0);
		}
		
		if (String.valueOf(Constants.LOGIC_LESSEQUAL).equals(op)) {
			int result = previous.compareTo(current);
			if (result <= 0)
				return Double.valueOf(1);
			else
				return Double.valueOf(0);
		}
		
		if (String.valueOf(Constants.LOGIC_NOTEQUAL).equals(op)) {
			int result = previous.compareTo(current);
			if (result != 0)
				return Double.valueOf(1);
			else
				return Double.valueOf(0);
		}
		
		if (String.valueOf(Constants.LOGIC_OR).equals(op)) {
			if (previous != 0 || current != 0)
				return Double.valueOf(1);
			else
				return Double.valueOf(0);
		}
		
		if (String.valueOf(Constants.LOGIC_AND).equals(op)) {
			if (previous != 0 && current != 0)
				return Double.valueOf(1);
			else
				return Double.valueOf(0);
		}
		
		if (String.valueOf(Constants.LOGIC_NOT).equals(op)) {
			if (current != 0)
				return Double.valueOf(0);
			else
				return Double.valueOf(1);
		}

		return null;
	}
}
