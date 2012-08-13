package com.taobao.hive.udf;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.io.Text;

import com.taobao.hive.util.UDFStringUtil;

public class UDFGetValueFromRefer2 {
	
	private Pattern pattern = null;
	
	private String lastIndexString = "";
	
	public Text evaluate(Text refer,Text split_char,Text index_String) {
		if (refer == null || split_char == null || index_String == null || "".equals(index_String.toString().trim())) return null;
		String indexStr = index_String.toString();
		String referStr = refer.toString();
		if (!indexStr.equals(lastIndexString)) {
			pattern = Pattern.compile("("+ split_char + "|\\?)" + index_String + "([^"+ split_char +"]*)");
		}
		lastIndexString = indexStr;
	    Matcher m = pattern.matcher(referStr);
	    if (m.find()) {
	      return new Text(m.group(2));
	    }
	    return new Text("");
	}
	
	private Text result = new Text();
	
	public Text evaluate2(Text refer,Text split_char,Text index_String) {
		if (refer == null || split_char == null || index_String == null || "".equals(index_String.toString().trim())) return null;
		String indexStr = index_String.toString();
		String referStr = refer.toString();
		String res = UDFStringUtil.getURLValue(referStr, indexStr, split_char.toString());
		res = res == null ? "":res;
		result.set(res);
		return result;
	}
	
	public Text evaluate(Text refer,Text split_char,Text index_String,String split,String index) {
		Text result = evaluate(refer,split_char,index_String);
		if (result == null) return null;
		String returnValue = "";
		String value = result.toString();
		if(value.indexOf(split)>=0){
			String[] strList = value.split(split);
			if(strList.length>Integer.parseInt(index)){
				returnValue = strList[Integer.parseInt(index)];//index��0��ʼ
			}
		}
		result.set(returnValue);
		return result;
	}
}
