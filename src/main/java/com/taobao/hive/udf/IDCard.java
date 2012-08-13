package com.taobao.hive.udf;

import org.apache.hadoop.hive.ql.exec.UDF;



import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import org.apache.hadoop.io.Text;
import java.util.*;

public class IDCard extends UDF{
	private Text res = new Text();
	public boolean b15Vevify(String strId){
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
	
	public boolean b18Vevify(String strId){
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
	
	public String getSex(String strId){
		strId = strId.toUpperCase();
		int iLength = strId.length();
		if(iLength != 15 && iLength != 18){
			return "-1";
		}
			if(iLength == 15){
				try{
					 if(!b15Vevify(strId)){
						 return "-1";
					 }
					 int sex=Integer.parseInt(strId.substring(14,15));
					 if(sex%2==0){
						 return "2";   ///Å®
					 }
					 else{
						 return "1";   ///ÄÐ
					 }
				}
				catch(Exception e){
					return "-1";
				}
			}
			else if(iLength == 18){
				try{
					if(!b18Vevify(strId)){
						 return "-1";
					 }
					
					int sex=Integer.parseInt(strId.substring(16,17));
					
					if(sex%2==0){
						 return "2";
					 }
					 else{
						 return "1";
					 }
				}catch(Exception e){
					return "-1";
				}
			}
			else{
				return "-1";
			}
		
	}
	public static Date getStrDate(String strX) {
	      Date date1 = new Date();
	      if (!strX.equals("")) {
	         try {
	            date1 = (DateFormat.getDateInstance()).parse(strX);
	         } catch (Exception ex) {
	            // System.out.println(ex.toString());
	         }
	      } else {
	         GregorianCalendar gcNow = new GregorianCalendar();
	         date1 = gcNow.getTime();
	      }

	      return date1;
	   }
	
	public static int dateDiff(java.util.Date a, java.util.Date b) {
		int tempDifference = 0;
		int difference = 0;
		Calendar earlier = Calendar.getInstance();
		Calendar later = Calendar.getInstance();

		if (a.compareTo(b) < 0) {
			earlier.setTime(a);
			later.setTime(b);
		} else {
			earlier.setTime(b);
			later.setTime(a);
		}

		while (earlier.get(Calendar.YEAR) != later.get(Calendar.YEAR)) {
			tempDifference = 365 * (later.get(Calendar.YEAR) - earlier
					.get(Calendar.YEAR));
			difference += tempDifference;

			earlier.add(Calendar.DAY_OF_YEAR, tempDifference);
		}

		if (earlier.get(Calendar.DAY_OF_YEAR) != later
				.get(Calendar.DAY_OF_YEAR)) {
			tempDifference = later.get(Calendar.DAY_OF_YEAR)
					- earlier.get(Calendar.DAY_OF_YEAR);
			difference += tempDifference;

			earlier.add(Calendar.DAY_OF_YEAR, tempDifference);
		}

		return difference;
	}
	
	@SuppressWarnings("deprecation")
	public int getAge(String strId){
		strId = strId.toUpperCase();
		int iLength = strId.length();
		if(iLength!=15 && iLength != 18){
			return -1;
		}
		int iSex = Integer.parseInt(getSex(strId));
		if(iSex == -1){
			return -1;
		}
		if(iLength == 15){
			try{
				if(!b15Vevify(strId)){
					 return -1;
				 }
				
				SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd"); 
				Calendar cal = Calendar.getInstance();
				Date daysnow =cal.getTime();
				
				//Calendar cal = Calendar.getInstance();
				//int ynow = cal.get(cal.YEAR);
				String birthdatetmp = strId.substring(6, 8);
				String birthdateString = "19"+birthdatetmp+"-"+strId.substring(8, 10)+"-"+strId.substring(10, 12);
				java.util.Date birthday = sd.parse(birthdateString);
				//long l = birthday.getTime() - daysnow.getTime();
				int days = dateDiff(daysnow,birthday)/360;
				
				//int age = ynow - y + 1;			
				return days;
			}catch(Exception e){
				return -1;
			}
		}
		else if(iLength == 18){
			try{
				if(!b18Vevify(strId)){
					 return -1;
				 }
				SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd"); 
				

				java.util.Date daysnow = new Date();
				//SimpleDateFormat format = new SimpleDateFormat("yyyymmdd");
				//Date birthdate = null;
				//birthdate = sd.parse(strId.substring(6,15));
				System.out.println(strId.substring(6,14));
				java.util.Date birthdate = sd.parse(strId.substring(6,10)+"-"+strId.substring(10,12)+"-"+strId.substring(12,14));
				//Date today=(Date) Calendar.getInstance(Locale.CHINA).getTime();
				//daysnow.set(Calendar.HOUR_OF_DAY, 23); 
				///daysnow.set(Calendar.MINUTE, 59); 
				//daysnow.set(Calendar.SECOND, 59); 
				//daysnow.set(Calendar.MILLISECOND, 59); 
                 
				int days = dateDiff(daysnow,birthdate)/360;
				//int x = (int)((l/(1000*60*60*24)));
				//int x = (int)(l/(1000*60*60*24));
				//int days = (int)(x/360);
				//int y = Integer.parseInt(strId.substring(11,15));
				//Date birth = new Date();
				//int age = ynow - y + 1;				
				return days;
			}catch(Exception e){
				return -1;
			}
		}
		else{
			return -1;
		}
	}
	
	public int getAgeLevel(String strId){
		strId = strId.toUpperCase();
		int iAge = getAge(strId);
		
		if(iAge >=-10000  && iAge < 0){
			return 8;
		}
		else if(iAge >= 0 && iAge < 18){
			return 1;
		}
		else if(iAge >= 18 && iAge < 25){
			return 2;
		}
		else if(iAge >= 25 && iAge < 30){
			return 31;
		}
		else if(iAge >= 30 && iAge < 35){
			return 32;
		}
		else if(iAge >= 35 && iAge < 40){
			return 41;
		}
		else if(iAge >= 40 && iAge < 50){
			return 42;
		}
		else if(iAge >= 50 && iAge < 60){
			return 5;
		}
		else if(iAge >= 60 && iAge < 101){
			return 6;
		}
		else if(iAge >= 101 && iAge < 10000){
			return 7;
		}
		else{
			return -1;
		}
		
	}
	
	public Text evaluate(Text strId, Text iTag){
		String strId1 = strId.toString();
		int iTag1 = Integer.parseInt(iTag.toString());
		if(strId1==null || strId1.equals("null")){
			//return res.s"-1";
		}
		if(iTag1 == 1){
			//0 female, 1 male
			//res.set(new String(getSex(strId1));
			//return getSex(strId1);
			String setStr = new String(getSex(strId1));
			res.set(setStr);
			return res;
			
		}
		else if(iTag1 == 2){
			//return getAge(strId1);
			String AgeValue = String.valueOf(getAge(strId1));
			res.set(AgeValue);
			return res;
			
		}
		//else if(iTag1 == 3){
		//	return getAgeLevel(strId1);
		//}
		else{
			res.set("-1");
			return res;
		}
	}

}

