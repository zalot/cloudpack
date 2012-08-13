/**
 * 
 */
package com.taobao.ad.data.search.udf;

/**
 * @author xuanran 包含兩個接口：繁體中文到簡體中文的轉換和全角符號到半角符號
 */
public class GBKConvertor {
	public static String Gbk2GbkSimple(String inputStr) {/* 繁体中文到简体中文 */
		try {
			byte[] byInput = inputStr.getBytes("GBK");
			byte[] byoutput = new byte[byInput.length];
			int[] intValue = new int[2];
			int offset = 0;
			int count = 0;
			Gbk2GbkSimplified gbk2Gbk = Gbk2GbkSimplified.getInstance();
			for (int index = 0; index < byInput.length;) {
				intValue[0] = byInput[index] & 0xff;
				if (intValue[0] >= 0x81 && intValue[0] <= 0xfe) {// 在汉字的高字节编码区间
					if (index + 1 < byInput.length) {
						intValue[1] = byInput[index + 1] & 0xff;
						if (intValue[1] >= 0x40 && intValue[1] <= 0xfe
								&& intValue[1] != 0x7f) {// 在汉字的低字节编码区间
							offset = (((int) intValue[0] - 0x81)
									* (0xFE - 0x40 + 1) + ((int) intValue[1] - 0x40));
							byoutput[count++] = (byte) (gbk2Gbk.gbkTable[offset * 2]);
							byoutput[count++] = (byte) (gbk2Gbk.gbkTable[offset * 2 + 1]);
							index += 2;
						} else {
							byoutput[count++] = byInput[index++];
						}
					} else {
						byoutput[count++] = byInput[index++];
					}
				} else {
					byoutput[count++] = byInput[index++];
				}
			}
			String output = new String(byoutput, 0, count, "GBK");
			return output;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	public static String Sbc2Dbc(String inputStr) {/* 全角符号转化成半角符号 */
		try {
			byte[] byInput = inputStr.getBytes("GBK");
			byte[] bytemp = new byte[byInput.length];
			int[] intValue = new int[2];
			int count = 0;
			for (int index = 0; index < byInput.length;) {
				intValue[0] = byInput[index] & 0xff;
				if (index + 1 < byInput.length) {
					intValue[1] = byInput[index + 1] & 0xff;
					if (intValue[0] == 0xA1 && intValue[1] == 0xA3) {// 句号
						bytemp[count++] = (byte) 0x2E;
						index += 2;
					} else if (intValue[0] == 0xA1
							&& (intValue[1] == 0xB0 || intValue[1] == 0xB1)) {// 双引号
						bytemp[count++] = 34;// 半角双引号
						index += 2;
					} else if (intValue[0] == 0xA1
							&& (intValue[1] == 0xAE || intValue[1] == 0xAF)) {// 单引号
						bytemp[count++] = 39;// 半角单引号
						index += 2;
					} else if (IsChineseChar(intValue[0], intValue[1])) {// 句号外的中文标点
						bytemp[count++] = (byte) (intValue[1] - 0x80);
						index += 2;
					} else if (intValue[0] >= 128) {// modified by xuanran at
						// 090707
						bytemp[count++] = (byte) intValue[0];
						bytemp[count++] = (byte) intValue[1];
						index += 2;
					} else {
						bytemp[count++] = (byte) intValue[0];
						index++;
					}
				} else {
					bytemp[count++] = (byte) intValue[0];
					index++;
				}
			}
			return new String(bytemp, 0, count, "GBK");
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	private static boolean IsChineseChar(int n1, int n2) {/* 判断是否为中文标点 */
		return (n1 == 0xA3 && (n2 > 0xA0 && n2 < 0xFF));
	}
}
