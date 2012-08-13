package com.taobao.hive.udf;

import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.Text;

/**
 * 
 * @author pangui
 * CREATE TEMPORARY FUNCTION get_pinyin AS 'com.taobao.hive.udf.UDFGetPinYin';
 */
public class UDFGetPinYin extends UDF {
	
	//expresstion 
	//table of the constant list 
	// 'A'; //45217..45252 
	// 'B'; //45253..45760 
	// 'C'; //45761..46317 
	// 'D'; //46318..46825 
	// 'E'; //46826..47009 
	// 'F'; //47010..47296 
	// 'G'; //47297..47613 

	// 'H'; //47614..48118 
	// 'J'; //48119..49061 
	// 'K'; //49062..49323 
	// 'L'; //49324..49895 
	// 'M'; //49896..50370 
	// 'N'; //50371..50613 
	// 'O'; //50614..50621 
	// 'P'; //50622..50905 
	// 'Q'; //50906..51386 

	// 'R'; //51387..51445 
	// 'S'; //51446..52217 
	// 'T'; //52218..52697 
	//没有U,V 
	// 'W'; //52698..52979 
	// 'X'; //52980..53640 
	// 'Y'; //53689..54480 
	// 'Z'; //54481..55289 

	
	private Text result = null;
	
	private static String DEFALT_ENCODE = "UTF-8";
	
	private int[][] constantsArray;
	
	public UDFGetPinYin() {
		result = new Text();
		constantsArray = new int[123][2];
		initConstantsArray();
	}
	
	private void initConstantsArray() {
		constantsArray[97] = new int[]{45217,45252};  // a
		constantsArray[98] = new int[]{45253,45760}; //b
		constantsArray[99] = new int[]{45761,46317}; //c
		constantsArray[100] = new int[]{46318,46825};//d
		constantsArray[101] = new int[]{46826,47009};//e
		constantsArray[102] = new int[]{47010,47296};//f
		constantsArray[103] = new int[]{47297,47613};//g
		constantsArray[104] = new int[]{47614,48118};//h
		constantsArray[105] = new int[]{48118,48118};//i
		
		constantsArray[106] = new int[]{48119,49061};//j
		constantsArray[107] = new int[]{49062,49323};//k
		constantsArray[108] = new int[]{49324,49895};//l
		constantsArray[109] = new int[]{49896,50370};//m
		constantsArray[110] = new int[]{50371,50613};//n
		constantsArray[111] = new int[]{50614,50621};//o
		constantsArray[112] = new int[]{50622,50905};//p
		constantsArray[113] = new int[]{50906,51386};//q
		constantsArray[114] = new int[]{51387,51445};//r
		constantsArray[115] = new int[]{51446,52217};//s
		constantsArray[116] = new int[]{52218,52697};//t
		
		constantsArray[117] = new int[]{52697,52697}; // u
		constantsArray[118] = new int[]{52697,52697}; // v
		constantsArray[119] = new int[]{52698,52979};
		constantsArray[120] = new int[]{52980,53640};
		constantsArray[121] = new int[]{53689,54480};
		constantsArray[122] = new int[]{54481,55289};
	}
	
	private int getIndex(int b) {
		if(constantsArray.length==0)
		      return 0;
		int begin = 97;
		int end = constantsArray.length-1;
		while(begin <= end) {
		    int mid = (end + begin) / 2;
		    if( b>=constantsArray[mid][0] && b<=constantsArray[mid][1]) {
		          return mid;
		    } else if(b < constantsArray[mid][0]) {
		    	  end=mid-1;
		    } else {
		          begin = mid+1;
		    }
		}
		return 0;
	}
	
	public Text evaluate(String str) {
		return evaluate(str,"UTF-8");
	}
	
	public Text evaluate(String str,String encode) {
		if (str == null || "".equals(str)) return null;
		if (encode == null || "".equals(encode)) encode = DEFALT_ENCODE ;
		String c = getFirstCharacter(str,encode);
		if ("".equals(c)) return null;
		result.set(c);
		return result;
	}
	
	private String getFirstCharacter(String str,String encode) {
		try {
			str = str.toLowerCase();
			String res = new String(str.getBytes(), 0, str.getBytes().length, encode);
			byte[] b = res.getBytes("GBK");
			if (b.length == 1) return str.toUpperCase();
			else if (b.length > 1) {
				int idx = 0;
				if(b[0]>=97 && b[0]<=122) {
					idx = b[0];
				} else {
					int i1 = (b[0]) & 0xff; 
					int i2 = (b[1]) & 0xff; 
					idx = getIndex(i1*256+i2);
				}
				if (idx >=97 && idx <=122) {
					return String.valueOf(((char)idx)).toUpperCase();
				}
			}
			return "";
		} catch (Exception e) {
			return "";
		}
	}
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		String str = "z";
//		System.out.println(str.length() + "_" + str.substring(0,1) + "_" +str.getBytes().length);
//		
//		System.out.println(String.valueOf((char)str.getBytes()[0]));
//		
//		System.out.println(CodeUtil.isGBK(str));
//		
//		System.out.println(Charset.defaultCharset());
//		
		UDFGetPinYin udf =  new UDFGetPinYin();
		
		System.out.println(Integer.valueOf("D4D1",16).toString());
		
		System.out.println(udf.evaluate("完美世界","UTF-8").toString());
		System.out.println(udf.evaluate("W","GBK").toString());
		System.out.println(udf.evaluate("T-中文","UTF-8").toString());
		System.out.println(udf.evaluate("linling4325").toString());
		System.out.println(udf.evaluate("狠毒de蝎").toString());
		System.out.println(udf.evaluate("婷婷").toString());
	}

}
