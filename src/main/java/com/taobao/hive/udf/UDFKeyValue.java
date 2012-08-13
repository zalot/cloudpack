package com.taobao.hive.udf;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.Text;


/**
 * 
 * CREATE TEMPORARY FUNCTION getkeyvalue  AS 'com.taobao.hive.udf.UDFKeyValue';
 * 
 * 
 * @author youliang
 *
 */
public class UDFKeyValue extends UDF{
	
	  static Map<String, String> mKeyValueache = new ConcurrentHashMap<String, String>(); //store all key:value pairs
	  
	  Text result = new Text();
	  
	  public UDFKeyValue() {}
	  
	  public Text evaluate(String str,String keyname) {
		  return evaluate(str,";",":",keyname); // default split1=; split2=:
	  }
	  
	  public Text evaluate(String str,String split1,String split2,String keyname) {
		  try{
			  if (str == null || "".equals(str)) return null;
			  String[] values1 = str.split(split1);
			  mKeyValueache.clear();
			  int i = 0;
			  while (i < values1.length) {
				  storeKeyValue(values1[i],split2);
				  i++;
			  }
			  String resultValue = getKeyValue(keyname);
			  if (resultValue == null) return null;
			  result.set(new Text(resultValue));
			  return result;
		  } catch (Exception e){
			  return null;
		  }
	  }
	  
	  private boolean storeKeyValue(String keyValues, String split){
		  if (keyValues == null || "".equals(keyValues)) return false;
		  if (mKeyValueache == null) mKeyValueache = new ConcurrentHashMap<String, String>();
		  String[] keyValueArr = keyValues.split(split);
		  if (keyValueArr.length == 2){
			  mKeyValueache.put(keyValueArr[0], keyValueArr[1]);
			  return true;
		  }
		  return false;
	  }
	  
	  private String getKeyValue(String keyName){
		  if (keyName == null || "".equals(keyName) || mKeyValueache == null || mKeyValueache.size() == 0) return null;
		  return mKeyValueache.get(keyName);
	  }

	  
//	  public Text evaluate(String str,String split1,String split2,String keyname) {
//		  //;decreaseStore:1;xcard:1;isB2C:1;tf:21910;cart:1;shipping:2;pf:0;market:shoes;instPayAmount:0;
//		  try{
//			  if(str==null){
//			       return null;
//			  }
//			  String[] strList = str.split(split1);
//			  for(int i=0;i<strList.length;i++){
//				  String keyvalue = strList[i];
//				  if(keyvalue.indexOf(keyname+split2)>=0){
//					  String[] strList1 = keyvalue.split(split2);
//					  if (strList1[0].equals(keyname)){
//						  String value = strList1[1];
//						  if(value.trim().equals("")){
//						      return null;
//						  }
//					      result.set(new Text(value));
//					      return result;
//					  }
//				  }
//			  }
//		      
//		      return null;
//		  }catch(Exception e){
//			  return null;
//		  }
//	  }
//	  
//	  public Text evaluate(String str,String split1,String split2,String keyname) {
//		  try{
//			if(str==null){
//			     return null;
//			}
//			String reg = "\\"+split1+"?\\b"+keyname+"\\"+split2+"(.*?)"+"\\"+split1;
//			Pattern parttern = Pattern.compile(reg);
//			Matcher m = parttern.matcher(str);
//			while (m.find()){
//				String value = m.group(1);
//				result.set(new Text(value));
//				return result;
//			}
//			return null;
//		}catch(Exception e){
//			return null;
//		}
//	  }
	  
	  public static void main(String args[]){
		  UDFKeyValue test = new UDFKeyValue();
		  System.out.println(test.evaluate(";decreaseStore:1;xcard:1;isB2C:1;tf:21910;cart:1;shipping:2;pf:0;market:shoes;instPayAmount:0;", ";",":","tf"));
	  }
}
