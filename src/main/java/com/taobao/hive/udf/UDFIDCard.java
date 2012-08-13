package com.taobao.hive.udf;

import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDF;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import org.apache.hadoop.io.Text;
import java.util.*;

/*
 * CREATE TEMPORARY FUNCTION get_idcard_info  AS 'com.taobao.hive.udf.UDFIDCard';
 * select get_idcard_info(id,'gender') as gender from dual;
 */
@Description(name = "get_idcard_info", value = "_FUNC_(x,y) - Returns the specific value from id number \n"
	+ "y can be 'gender','age','birthday'")
public class UDFIDCard extends UDF{
	public UDFIDCard(){}
	private Text result = new Text();
	private UDFDateDiff diff = new UDFDateDiff();
	
	public Text evaluate(String strId, String flag){
		if(strId == null){
			result.set("-1");
			return result;
		}
		if ("gender".equals(flag)){
			result.set(getSex(strId));
		} else if("age".equals(flag)) {
			result.set(String.valueOf(getAge(strId)));
		} else if("birthday".equals(flag)){
			result.set(getBirthDate(strId));
		} else {
			result.set("-1");
		}
		return result;
	}
	
	private boolean b15Vevify(String strId){
		String areaNo = strId.substring(0,6);
		if (!areaNo.matches("^[\\d]+$")){
			return false;
		}
		int y=Integer.parseInt(strId.substring(6,8));
		int m=Integer.parseInt(strId.substring(8,10));
		int d=Integer.parseInt(strId.substring(10,12));
		if (m<1 || m>12 || d<1 || d>31 || ((m==4 || m==6 || m==9 || m==11) && d>30) ||
		   (m==2 && (((y+1900) % 4>0 && d>28) || d>29))){
			return false;
		}
		Calendar cal = Calendar.getInstance();
		int ynow = cal.get(Calendar.YEAR);
		y += 1900;
		int age = ynow - y + 1;
		if(age < 0){
			return false;
		}
		return true;
	}
	
	private boolean b18Vevify(String strId){
		String areaNo = strId.substring(0,6);
		if (!areaNo.matches("^[\\d]+$")){
			return false;
		}
		int[] xx= {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2}; 
		String[] yy={"1","0","X","9","8","7","6","5","4","3","2"};
		int mm=0;
		for (int i=0; i<17; i++) {
			int ai = Integer.parseInt(strId.substring(i,i+1));
			int wi = xx[i];
			mm += ai * wi;
		}
		int number = mm%11;
		String check_number = yy[number];
		if(!strId.substring(17).equals(check_number)){				
			return false;
		}
		int y=Integer.parseInt(strId.substring(6,10));
		int m=Integer.parseInt(strId.substring(10,12));
		int d=Integer.parseInt(strId.substring(12,14));
		if (y<1900 || m<1 || m>12 || d<1 || d>31 || ((m==4 || m==6 || m==9 || m==11) && d>30) ||
				(m==2 && ((y % 4>0 && d>28) || d>29))){
			return false;
		}
		Calendar cal = Calendar.getInstance();
		int ynow = cal.get(Calendar.YEAR);
		int age = ynow - y + 1;
		if(age < 0){
			return false;
		}
		return true;
	}
	
	private String getSex(String strId){
		strId = strId.toUpperCase();
		int iLength = strId.length();
		if(iLength != 15 && iLength != 18) return "-1";
		try {
			if (b15Vevify(strId)) {
				int sex=Integer.parseInt(strId.substring(14,15));
				if(sex%2==0){
					 return "2";   ///female
				 } else{
					 return "1";   ///male
				 }
			} else if (b18Vevify(strId)) {
				int sex=Integer.parseInt(strId.substring(16,17));
				if(sex%2==0){
					 return "2";
				 } else{
					 return "1";
				 }
			}
		} catch(Exception e){
			return "-1";
		}
		return "-1";
		
	}
	
	private String getBirthDate(String strId) {
		strId = strId.toUpperCase();
		int iLength = strId.length();
		if(iLength != 15 && iLength != 18) return "-1";
		try {
			if (b15Vevify(strId)) {
				int y=Integer.parseInt(strId.substring(6,8));
				String m=strId.substring(8,10);
				String d=strId.substring(10,12);
				y += 1900;
				return String.valueOf(y) + m + d;
			} else if (b18Vevify(strId)) {
				return strId.substring(6,14);
			}
		} catch (Exception ex) {
            return "-1";
        }
		return "-1";
	}
	
	private int getAge(String strId){
		strId = strId.toUpperCase();
		int iLength = strId.length();
		if(iLength!=15 && iLength != 18) return -1;
		try{
			String date = getBirthDate(strId);
			if (!"".equals(date) && date.length() == 8) {
				String dateF = date.substring(0,4) + "-" + date.substring(4,6) + "-" + date.substring(6,8);
				SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd"); 
				String dateNow = sd.format(new Date());
				double year = diff.evaluate(new Text(dateNow), new Text(dateF)).get()/365; 
				return new Double(year).intValue() + 1;
			}
		}catch(Exception e){
			return -1;
		}
		return -1;
	}
	

}

