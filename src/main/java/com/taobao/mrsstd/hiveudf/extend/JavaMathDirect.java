package com.taobao.mrsstd.hiveudf.extend;

import com.taobao.mrsstd.hiveudf.util.UDFUtil;

public class JavaMathDirect extends Parent {

	@Override
	public String evaluate(String function, String... values) {		
		if ("abs".equalsIgnoreCase(function)) {
			Double result = Math.abs(Double.valueOf(values[0]));
			return UDFUtil.delZero(String.valueOf(result));
		} else if ("log".equalsIgnoreCase(function)) {
			Double result = Math.log(Double.valueOf(values[0]));
			return UDFUtil.delZero(String.valueOf(result));
		} else if ("log10".equalsIgnoreCase(function)) {
			Double result = Math.log10(Double.valueOf(values[0]));
			return UDFUtil.delZero(String.valueOf(result));
		} else if ("sin".equalsIgnoreCase(function)) {
			Double result = Math.sin(Double.valueOf(values[0]));
			return UDFUtil.delZero(String.valueOf(result));
		} else if ("round".equalsIgnoreCase(function)) {
			Long result = Math.round(Double.valueOf(values[0]));
			return UDFUtil.delZero(String.valueOf(result));
		} else if ("sqrt".equalsIgnoreCase(function)) {
			Double result = Math.sqrt(Double.valueOf(values[0]));
			return UDFUtil.delZero(String.valueOf(result));
		} else if ("pow".equalsIgnoreCase(function)) {
			Double result = Math.pow(Double.valueOf(values[0]), Double
					.valueOf(values[1]));
			return UDFUtil.delZero(String.valueOf(result));
		} else {
			throw new RuntimeException("not supported function: " + function);
		}
	}

	public static void main(String[] args) {
		JavaMathDirect math = new JavaMathDirect();
		long time1 = System.currentTimeMillis();
		for(int i = 0; i < 1000000; i++)
			math.evaluate("sin", "0.897");
		long time2 = System.currentTimeMillis();
		System.out.println("cost time: " + (time2 - time1));
	}
}
