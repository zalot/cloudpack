package com.taobao.mrsstd.hiveudf.calculate;

import java.util.Map;

import com.taobao.mrsstd.hiveudf.util.Constants;
import com.taobao.mrsstd.hiveudf.util.UDFUtil;

public class Calculator {
	
	private Map<String, String> mapValues;
	
	private String expression;
	
	public Calculator(Map<String, String> mapValues, String expression) {
		this.mapValues = mapValues;
		this.expression = expression;
	}
	
	public String calculate() throws Exception{
		if(expression == null || expression.trim().length() == 0)
			return "1";
		
		expression = expression.toLowerCase();
		char[] chars = expression.toCharArray();
		Stack<String> stack = new Stack<String>();
		String function = null;
		StringBuffer bufPrefix = new StringBuffer();
		boolean flagParenthesis = false;
		
		for (int i = 0; i < chars.length; i++) {
			String stackStr = String.valueOf(chars[i]);
			stack.push(stackStr);
			
			bufPrefix.append(stackStr);
			if (stackStr.equals(Constants.PARENTHESIS_LEFT)) {
				Stack<Character> stackFunc = new Stack<Character>();
				int index = bufPrefix.length() - 2;
				while (index >= 0) {
					boolean flagFound = false;
					for(int j = 0; j < Constants.CHARS.length; j++) {
						if (bufPrefix.charAt(index) == Constants.CHARS[j]) {
							flagFound = true;
							break;
						}
					}
					if (!flagFound) {
						stackFunc.push(bufPrefix.charAt(index));
					} else {
						break;
					}
					index--;
				}
				if (stackFunc.count > 0) {
					function = stackFunc.toString();
					function = UDFUtil.reverse(function);
					flagParenthesis = true;
				} else {
					flagParenthesis = false;
				}
			}
			
			if (Constants.PARENTHESIS_RIGHT.equals(stack.top())) {
				String subStr = null;
				while (!Constants.PARENTHESIS_LEFT.equals(stack.top())) {
					stack.pop();
					if (!Constants.PARENTHESIS_LEFT.equals(stack.top())) {
						subStr = addEnd(subStr, stack.top());
					}
				}
				
				String pushStr = "";
				if (function == null ) {
					pushStr = new StringCalculator(mapValues, subStr).calculate();
				} else if (function.equalsIgnoreCase(Constants.FUNCTION_STRING)) {
					pushStr = new StringCalculator(mapValues, subStr).calculate();
				} else if (function.equalsIgnoreCase(Constants.FUNCTION_NUMBER)) {
					pushStr = new MathCalculator(mapValues, subStr).calculate();
				} else {
					pushStr = new ExtendCalculator(function, mapValues, subStr).compute();
				}
				
				stack.pop();
				for(int k = 0; flagParenthesis && k < function.length(); k++)
					stack.pop();
				stack.push(Constants.SINGLE_QUOTE + pushStr + Constants.SINGLE_QUOTE);
				
				//把剩余的字符移入stack中
				for (int l = i + 1; l < chars.length; l++) {
					stackStr = String.valueOf(chars[l]);
					stack.push(stackStr);
				}
				bufPrefix = new StringBuffer();
				function = null;
				i = -1; 
				chars = stack.toString().toCharArray();
				flagParenthesis = false;
			}
		}
		String resultStr = null;
		while (stack.count != 0) {
			resultStr = new StringCalculator(mapValues, stack.toString()).calculate();
		}
		
		return resultStr;
	}

	public String addEnd(String str, String a) {
		StringBuffer buf = new StringBuffer();
		buf.append(a);
		if (str != null) {
			buf.append(str);
		}
		return buf.toString();
	}

	public static void main(String[] args) {
		/*String[] strs = new String[] { "4-(4.0-3.2*5+(2*4)+100)/10" };
		
		long time = System.currentTimeMillis();
		//for (int i = 0; i < 2000000; i++) {
			BigDecimal result = new Calculator().calculateStr(strs[0]);
		//}
		long time2 = System.currentTimeMillis();
		System.out.println(time2 - time + ":" + result);*/
		
		//String temp = "N(1+2*(3+3))";
		
	}

}
