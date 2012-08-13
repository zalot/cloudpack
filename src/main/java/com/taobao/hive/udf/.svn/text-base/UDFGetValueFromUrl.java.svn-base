package com.taobao.hive.udf;

import java.util.regex.Pattern;

import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.Text;

import com.taobao.hive.udf.search.GetValueFromUrl;
/**
 * ��url��ȡָ���Ĳ���ֵ
 * �÷�:
 * CREATE TEMPORARY FUNCTION getValueFromUrl  AS 'com.taobao.hive.udf.UDFGetValueFromUrl';
 * getValueFromUrl(url,"taomi")  д������    =����д
 *  
 * @author youliang
 *
 */
public class UDFGetValueFromUrl extends UDF{

	  private GetValueFromUrl search = new GetValueFromUrl();
	  Text result = new Text();
	  
	  public UDFGetValueFromUrl() {
	  }

	  public Text evaluate(String url, String param) {
		  if (url == null || param == null || "".equals(url) || "".equals(param)) return null;
		  if (param.charAt(param.length()-1)=='=') {
			  return search.evaluate(new Text(url), new Text(param));
		  } else {
			  return evaluateOrg(url,param);
		  }
	  }
	  
	  private Text evaluateOrg(String url,String param) {
		  try{
				 String[] strList = url.split("&");
				 for(int i=0;i<strList.length;i++){
					 String canshu = strList[i];
					 if(canshu.indexOf("?"+param+"=")>=0){
						  int k = canshu.indexOf("?"+param+"=");
						  String taomiValue = canshu.substring(k+param.length()+2);  
					      result.set(taomiValue);
					      return result;
					 }
					 if(canshu.indexOf(param+"=")==0){
						   String taomiValue = canshu.substring(param.length()+1);
					       result.set(taomiValue);
					       return result;
					 }
				 }
		       result.set("");
		       return result;
		  }catch(Exception e){
		       result.set("err");
		       return result;
		  }

	  }
	  
	  public static void main(String args[]){
		  UDFGetValueFromUrl test = new UDFGetValueFromUrl();
		  System.out.println(test.evaluate("http://item.daily.taobao.net/auction/item_detail-1-408a3e2d05ef2d261036776ad132c1dc.jhtml?ecrmPromotionId=111111&ecrmSellerId=222222&ecrmBuyerId=333333","ecrmBuyerId"));
		  System.out.println(test.evaluate(null,null));
		  
		  String param = "ecrmBuyerId=";
		  System.out.println(param.charAt(param.length()-1));
	  }

	  
}