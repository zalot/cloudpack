package com.taobao.hive.udf;


//import java.util.*;
import java.util.HashSet;

import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.Text;

public class QueryNormalization extends UDF {
	public Text res = new Text();
	private static final String MYAND = "AND";
	private static final String MYOR = "OR";
	public QueryNormalization()
	{
		
	}
	public Text evaluate(Text QueryString)
	{
		if (QueryString == null || QueryString.toString() == null )
		{
			return null;
		}
		String temp = QueryString.toString();
		temp = removeSpecialChar(temp);//��������
		temp = Sbc2Dbc(temp);// ȫ�Ǳ���
		temp = removeSpace(temp);// ȥ�ո�
		res.set(upper2Lower(temp));
		return res;
		
	}
	
	public static boolean isRemoveSpecialChar(char test) {
		HashSet<String> removeSpecHash = new HashSet<String>();
		removeSpecHash.add("��");
		removeSpecHash.add(";");
		removeSpecHash.add("��");
		removeSpecHash.add("?");
		removeSpecHash.add("��");
		String temp = "" + test;
		if (removeSpecHash.contains(temp)) {
			return true;
		} else {
			return false;
		}
	}
	
	public static String removeSpecialChar(String input)
	{
		int length = input.length();
		StringBuffer buffer = new StringBuffer(length);
		for (int i = 0; i < length; i++) {
			if (isRemoveSpecialChar(input.charAt(i))) {
				buffer.append(' ');
			} else {
				buffer.append(input.charAt(i));
			}
		}
		return new String(buffer);
		
	}
	
	public static String Sbc2Dbc(String inputStr) {/* ȫ�Ƿ��ת���ɰ�Ƿ�� */
		try {
			byte[] byInput = inputStr.getBytes("GBK");
			byte[] bytemp = new byte[byInput.length];
			int[] intValue = new int[2];
			int count = 0;
			for (int index = 0; index < byInput.length;) {
				intValue[0] = byInput[index] & 0xff;
				if (index + 1 < byInput.length) {
					intValue[1] = byInput[index + 1] & 0xff;
					if (intValue[0] == 0xA1 && intValue[1] == 0xA3) {// ���
						bytemp[count++] = (byte) 0x2E;
						index += 2;
					} else if (intValue[0] == 0xA1
							&& (intValue[1] == 0xB0 || intValue[1] == 0xB1)) {// ˫���
						bytemp[count++] = 34;// ���˫���
						index += 2;
					} else if (intValue[0] == 0xA1
							&& (intValue[1] == 0xAE || intValue[1] == 0xAF)) {// �����
						bytemp[count++] = 39;// ��ǵ����
						index += 2;
					} else if (IsChineseChar(intValue[0], intValue[1])) {// ���������ı��
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
	
	private static boolean IsChineseChar(int n1, int n2) {/* �ж��Ƿ�Ϊ���ı�� */
		return (n1 == 0xA3 && (n2 > 0xA0 && n2 < 0xFF));
	}
	
	public static String removeSpace(String strInput) {
		String input = strInput.trim();//ֻ��ȥ��ʼ���β���İ�ǿո�
		int length = input.length();
		StringBuffer buffer = new StringBuffer(length);
		char c1 = 0;
		char c2 = 0;
		for (int index = 0; index < length;) {
			int count = 0;
			int first = index;
			c1 = input.charAt(index);
			if (IsSpace(c1)) {
				if (index + 1 < length) {
					c2 = input.charAt(index + 1);
					while (IsSpace(c2) && (index < length - 1)) {
						index++;
						count++;
						c2 = input.charAt(index);
					}
					if (first != 0 && index != length -1) {//�м�ո�
						buffer.append(' ');
					}
					if (count == 0) {
						index++;
					}
				}
				else {//��β���пո�
					break;
				}
			} else {
				buffer.append(c1);
				index++;
			}
		}
		return new String(buffer);
	}
	
	private static boolean IsSpace(char c) {
		return (c == ' ') || (c == '\u3000') || (c == '\t')|| (c == '\n');
	}
	
	public static String upper2Lower(String strInput) {
		String strList[] = strInput.split("\\s+", -1);
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < strList.length; i++) {

			if (isSpecail(strList[i])) {//�����ַ����⴦��
				if (i == 0) {
					buffer.append(strList[i]);
				} else {
					buffer.append(" " + strList[i]);
				}
			} else {//����һ�㴦��
				if (i == 0) {
					buffer.append(strList[i].toLowerCase());
				} else {
					buffer.append(" " + strList[i].toLowerCase());
				}
			}
		}
		return new String(buffer);

	}
	
	public static boolean isSpecail(String input) {
		if (MYAND.equals(input) || MYOR.equals(input)) {
			return true;
		} else {
			return false;
		}
	}
	
	

}
