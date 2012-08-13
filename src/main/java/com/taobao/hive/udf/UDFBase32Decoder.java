package com.taobao.hive.udf;

import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.Text;
/**
 * ��g,xc2ltrjaygwnfqwixeqmrvi���ָ�ʽ���ַ����base32����
 * �÷�:
 * CREATE TEMPORARY FUNCTION base32Decoder  AS 'com.taobao.hive.udf.UDFBase32Decoder';
 * taoMiDecoder(valueOfTaomi)
 *  
 * @author youliang
 *
 */
public class UDFBase32Decoder extends UDF{

	  Text result = new Text();
	  public UDFBase32Decoder() {
		  
	  }

	   
	  public Text evaluate(String value) {
		  Base32 test = new Base32();
		  String returnResult = test.decodeString(value);

		  result.set(returnResult);
		  return result;

	  }
	  
	  public static void main(String[] args){
		  UDFBase32Decoder test = new UDFBase32Decoder();
		  System.out.println(test.evaluate("g,xtg6r46pupd6du5pz3plvw6d64qnbr6juk35w"));
	  }
	  
	  
}
