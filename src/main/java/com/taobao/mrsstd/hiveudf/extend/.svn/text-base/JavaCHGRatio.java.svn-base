package com.taobao.mrsstd.hiveudf.extend;

import java.math.BigDecimal;

import com.taobao.mrsstd.hiveudf.util.UDFUtil;

public class JavaCHGRatio extends Parent {

	@Override
	public String evaluate(String function, String... values) {
		String strIdx = values[0];
		String strIdxLast = values[1];
		
		Double result = null;
		if (strIdx == null || strIdx.trim().length() == 0) {
			throw new RuntimeException("IDX is null");
		} else {
			if(strIdxLast == null || strIdxLast.trim().length() == 0) {
				if (values.length > 2) {
					result = Double.valueOf(values[2]);
				} else {
					result = 0d;
				}
			} else {
				Double doubleIdx = Double.valueOf(strIdx);
				Double doubleIdxLast = Double.valueOf(strIdxLast);
				if (doubleIdxLast == 0d) {
					if (values.length > 2) {
						result = Double.valueOf(values[3]);
					} else {
						result = 0d;
					}
				} else {
					result = (doubleIdx - doubleIdxLast) / (doubleIdxLast);
				}
			}
		}
		
		BigDecimal formatResult = new BigDecimal(String.valueOf(result)); 
		formatResult = formatResult.setScale(4, BigDecimal.ROUND_HALF_UP);
		
		return UDFUtil.delZero(formatResult.toPlainString());
	}

}
