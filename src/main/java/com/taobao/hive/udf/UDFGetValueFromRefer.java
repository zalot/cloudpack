package com.taobao.hive.udf;

import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.Text;

public class UDFGetValueFromRefer extends UDF {
	public Text res = new Text();
	public UDFGetValueFromRefer() {
	}

	public Text evaluate(Text refer,Text split_char,Text index_String) {
		if (refer == null || split_char == null || index_String == null)
		{
			return null;
		}
		
		String refer_tostring = refer.toString();
		
		if("".equals(index_String.toString().trim())){
			return null;
		}
		
		
		String String_return = "";
		String[] str_split_string = refer_tostring.split(split_char.toString());
		for(int i=0;i<str_split_string.length;i++)
		{
			String canshu = str_split_string[i];
			 if(canshu.indexOf("?"+index_String)>=0){
				  int k = canshu.indexOf("?"+index_String);
				  String taomiValue = canshu.substring(k+index_String.toString().length()+1);  
				  res.set(taomiValue);
			      return res;
			 }
			 if(canshu.indexOf(index_String.toString())==0){
				   String taomiValue = canshu.substring(index_String.toString().length());
				   res.set(taomiValue);
			       return res;
			 }
		}
	  
		res.set(String_return);
		return res;
	}		

	
	public Text evaluate(Text refer,Text split_char,Text index_String,String split,String index) {
		if(split==null||"".equals(split)){
			return null;
		}
		
		
		String returnValue = "";
		String value = evaluate(refer,split_char,index_String).toString();
		if(value.indexOf(split)>=0){
			String[] strList = value.split(split);
			if(strList.length>Integer.parseInt(index)){
				returnValue = strList[Integer.parseInt(index)];//index´Ó0¿ªÊ¼
			}
		}
		res.set(returnValue);
		return res;
	}		
	
	public static void main(String[] args){
		UDFGetValueFromRefer test = new UDFGetValueFromRefer();
		System.out.println(test.evaluate(new Text("http://www.abc.com?src=aa&bb=4_2_5_6"), new Text("&"), new Text("bb="),"_","2"));
		
		
		
		
	}
}
