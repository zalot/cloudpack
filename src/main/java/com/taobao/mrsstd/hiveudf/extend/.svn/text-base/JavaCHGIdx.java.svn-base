package com.taobao.mrsstd.hiveudf.extend;

import java.math.BigDecimal;

import com.taobao.mrsstd.hiveudf.util.UDFUtil;

public class JavaCHGIdx extends Parent {

	@Override
	public String evaluate(String function, String... values) {
		String strIdx = values[0];
		String strIdxLast = values[1];

		if (strIdx == null || strIdx.trim().length() == 0) {
			throw new RuntimeException("IDX is null");
		}
		Double idx = Double.valueOf(strIdx);

		Double result;
		if (strIdxLast == null || strIdxLast.trim().length() == 0) {
			result = idx;
		} else {
			Double idxLast = Double.valueOf(strIdxLast);

			if (idxLast < 0) {
				result = idx;
			} else {
				result = idx - idxLast;
			}
		}

		BigDecimal formatResult = new BigDecimal(String.valueOf(result));
		formatResult = formatResult.setScale(4, BigDecimal.ROUND_HALF_UP);

		return UDFUtil.delZero(formatResult.toPlainString());
	}

}
