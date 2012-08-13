package com.taobao.hive.udf.urlrecord;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.Text;

import com.taobao.hive.udf.UDFGetValueFromRefer2;
import com.taobao.hive.udf.UDFURLDecode;

public class UDFGetURLRecord extends UDF{
	
//	private Map<String,URLRecord> urlRecords = null;
	private Map<String,List<URLRecord>> urlRecords2 = null;
	private List<RuleURLProperty> ruleURLProperty = null;
	private UDFGetValueFromRefer2 getValueFormRefer = new UDFGetValueFromRefer2();
	private UDFURLDecode decode = new UDFURLDecode();
	private boolean combineFlag = false;
	private UrlRuleHandler handler = new UrlRuleHandler();
	private StringBuilder urlBuilder = new StringBuilder();
	private Text result = new Text();
	private Pattern pattern = Pattern.compile("^[\\d]{8,}$");
	
	public Text evaluate(String time,String url,String refer,String typeIds) throws IOException{
		return evaluate(time,url,refer,typeIds,"pre=");
	}
	
	public Text evaluate(String time,String url,String refer,String typeIds, String prefix) throws IOException{
		if (time == null || url == null || refer == null || typeIds == null || prefix == null || "".equals(prefix)
				|| "".equals(time) || "".equals(url) || "".equals(refer) || "".equals(typeIds)) {
			result.set("false");
			return result;
		}
		if (!validateParam(time)) {
			result.set("false");
			return result;
		}
		handler.init();
		if (!combineFlag) combineFlag=combineRegExp(time,typeIds);
		String[] typeId = typeIds.split(",");
		for (int i=0; i<typeId.length; i++) {
			if (validate(url,refer,typeId[i],prefix)){
				result.set("true");
				return result;
			}
		}
		result.set("false");
		return result;
	}
	
	public Text evaluate(String time,String url,String refer,String typeIds,String propertyId,String flag) throws IOException{
		return evaluate(time,url,refer,typeIds,propertyId,flag,"pre=");
	}
	
	public Text evaluate(String time,String url,String refer,String typeIds,String propertyId,String flag,String prefix) throws IOException{
		if (time == null || url== null || refer == null || typeIds == null ||
				propertyId == null || flag == null || (!"refer".equals(flag) && !"url".equals(flag))) return null;
		if (!validateParam(time)) return null;
		handler.init();
		if (ruleURLProperty == null) findURLPropertPattern(typeIds,propertyId,flag);
		if (ruleURLProperty.size() == 0) return null;
		String logTime = time.substring(0,8);
		for (RuleURLProperty property : ruleURLProperty) {
			if (Integer.parseInt(logTime)>=Integer.parseInt(property.getStartDate()) 
					&& Integer.parseInt(logTime)<=Integer.parseInt(property.getEndDate())) {
				if ("true".equals(evaluate(time,url,refer,typeIds,prefix).toString())) {
					if ("refer".equals(flag) && property.getUrlPattern() != null) {
						String dRefer = decode.evaluate(new Text(refer)).toString();//decode操作
						Text pre = getValueFormRefer.evaluate(new Text(refer),new Text("&"),new Text(prefix));
						if (pre == null) continue;
				   		String preStr = decode.evaluate(new Text(pre)).toString();//decode操作
						Matcher m = property.getUrlPattern().matcher(preStr);
						if (m.find()) {
							String value = getValue(pre.toString(),dRefer,property,flag);
							if (value != null && !"".equals(value)) {
								result.set(value);
								return result;
							}
						}
					} else if ("url".equals(flag) && property.getUrlPattern() != null){
						Matcher m = property.getUrlPattern().matcher(url);
						if (m.find()) {
							String value = getValue(url,url,property,flag);
							if (value != null && !"".equals(value)) {
								result.set(value);
								return result;
							}
						}
					}
				}
			}
		}
		return null;
	}
	
	private void findURLPropertPattern(String typeIds, String propertyId, String flag) {
		String[] typeId = typeIds.split(",");
		ruleURLProperty = new ArrayList<RuleURLProperty>();
		for (int i=0; i<typeId.length; i++) {
			RuleBusiness busi = handler.getRuleBusiness(typeId[i]);
			if (busi != null) {
				for (String relId : busi.getRelationIds()) {
					RuleRelation relation = handler.getRuleRelation(relId);
					if (relation == null) continue;
					String urlProperty = "";
					if ("refer".equals(flag)) {
						urlProperty = relation.getReferId() + "," + propertyId;
					} else {
						urlProperty = relation.getUrlId() + "," + propertyId;
					}
					RuleURLProperty propertyObj = handler.getRuleURLProperty(urlProperty);
					if (propertyObj != null) {
						RuleURL urlObj = handler.getRuleURL(urlProperty.split(",")[0]);
						Pattern urlPattern = Pattern.compile(urlObj.getRuleValue());
						propertyObj.setUrlPattern(urlPattern);
						propertyObj.setStartDate(urlObj.getStartDate());
						propertyObj.setEndDate(urlObj.getEndDate());
						ruleURLProperty.add(propertyObj);
					}
				}
			}
		}
	}
	
	private String getValue(String url,String refer, RuleURLProperty property, String flag) {
		//通过name提取对应的值
		//getValueByLocation
		 if(property.getPropertyName()!=null&&!property.getPropertyName().trim().equals("")){
			 Text value = getValueFormRefer.evaluate(new Text(refer),new Text("&"),new Text(property.getPropertyName()));
			 if (value == null) return null;
			 return value.toString();
        }else{
         	 if("0".equals(property.getIsDefault())){
         		 return getValueByLocation(url,property.getAtPath(),property.getPath(),"/",".htm","0"); 
         	 }else{
         		 return getValueByLocation(url,property.getAtPath(),property.getPath(),property.getStartPath(),
         				   property.getEndPath(),property.getAscDesc()); 
         	 }
       }
	}
	
	private String getValueByLocation(String source,String at_path,String path,String start_path,String end_path,String asc_desc){
		String urlC = between(source,start_path,end_path);
		if("|X|X|".equals(at_path)){
			return urlC;
		}
		if("".equals(at_path) || urlC.indexOf(at_path)<0){//url中没有这个分隔符就返回null
			return null;
		}
		String[] strList = urlC.split(at_path);
		if("0".equals(asc_desc)){//倒数
			return strList[strList.length-Integer.parseInt(path)];
		}else{//顺数
			return strList[Integer.parseInt(path)-1];
		}  
	}
	
	private String between(String value,String begin,String end){
		if("".equals(begin)&&"".equals(end)){
			return value;
		}
		int index1 = 0;
		if(!"".equals(begin)){
			index1 = value.lastIndexOf(begin);
		}
		if(!end.equals("end")){
			int index2 = value.indexOf(end);
			if("".equals(end) || index2==-1){
				index2 =  value.length();
			}
			if (index2>=index1+begin.length())
				value = value.substring(index1+begin.length(), index2);
			return value;
		}else{
			value = value.substring(index1+begin.length());
			return value;
		}
	}
	
	private boolean validate(String url, String refer, String typeId, String prefix) {
		List<URLRecord> records = urlRecords2.get(typeId);
		if (records == null || records.size() == 0) return false;
		for (URLRecord record : records) {
			if (record.getUrlPattern() == null && record.getReferPattern() == null) continue;
			Pattern urlPattern = record.getUrlPattern();
			Pattern referPattern = record.getReferPattern();
			if (urlPattern != null && referPattern == null) {
				// validate url
				Matcher m = urlPattern.matcher(url);
				if (m.find()) return true;
			} else if (urlPattern == null && referPattern != null) {
				// validate refer
				Text pre = getValueFormRefer.evaluate(new Text(refer),new Text("&"),new Text(prefix));
				if (pre == null) return false;
				String preStr = decode.evaluate(pre).toString();//decode操作
				Matcher m = referPattern.matcher(preStr);
				if (m.find()) return true;
			} else {
				Text pre = getValueFormRefer.evaluate(new Text(refer),new Text("&"),new Text(prefix));
				if (pre == null) return false;
				String preStr = decode.evaluate(pre).toString();//decode操作
				Matcher mRefer = referPattern.matcher(preStr);
				Matcher mUrl = urlPattern.matcher(url);
				// validate url and refer
				if (mUrl.find() && mRefer.find()) return true;
			}
		}
		return false;
	}
	
//	public boolean combineRegExp(String time) {
//		StringBuilder builder1 = new StringBuilder();
//		StringBuilder builder2 = new StringBuilder();
//		String logTime = time.substring(0,8);
//		urlRecords = new ConcurrentHashMap<String,URLRecord>();
//		for (Map.Entry<String, RuleBusiness> entry: handler.getRuleBusiness().entrySet()) {
//			try {
//				RuleBusiness busi = entry.getValue();
//				builder1.delete(0, builder1.length());
//				builder2.delete(0, builder2.length());
//				for (int i=0; i<busi.getRelationIds().size(); i++) {
//					RuleRelation ruleReation = handler.getRuleRelation(busi.getRelationIds().get(i));
//					RuleURL urlObj = handler.getRuleURL(ruleReation.getUrlId());
//					if (urlObj != null && Integer.parseInt(logTime)>=Integer.parseInt(urlObj.getStartDate()) 
//							&& Integer.parseInt(logTime)<=Integer.parseInt(urlObj.getEndDate())) builder1.append(urlObj.getRuleValue()).append(",");
//					RuleURL referObj = handler.getRuleURL(ruleReation.getReferId());
//					if (referObj != null && Integer.parseInt(logTime)>=Integer.parseInt(referObj.getStartDate()) 
//							&& Integer.parseInt(logTime)<=Integer.parseInt(referObj.getEndDate())) builder2.append(referObj.getRuleValue()).append(",");
//				}
//				String url = convert2Exp(builder1.toString());
//				String refer = convert2Exp(builder2.toString());
////				printConvertMsg(builder1,builder2,url,refer);
//				URLRecord record = new URLRecord();
//				record.setBizId(busi.getId());
//				if (!"-".equals(refer)) record.setReferPattern(Pattern.compile(refer));
//				if (!"-".equals(url)) record.setUrlPattern(Pattern.compile(url));
//				urlRecords.put(busi.getId(), record);
//			} catch (Exception e) {
//				continue;
//			}
//		}
//		return true;
//	}
	
	public boolean combineRegExp(String time,String typeIds) {
		String logTime = time.substring(0,8);
		urlRecords2 = new ConcurrentHashMap<String,List<URLRecord>>();
		String[] typeId = typeIds.split(",");
		for (int k=0; k<typeId.length; k++) {
			List<URLRecord> tmp = new ArrayList<URLRecord>();
			RuleBusiness busi = handler.getRuleBusiness(typeId[k]);
			try {
				for (int i=0; i<busi.getRelationIds().size(); i++) {
					URLRecord urlR = new URLRecord();
					RuleRelation ruleReation = handler.getRuleRelation(busi.getRelationIds().get(i));
					RuleURL urlObj = handler.getRuleURL(ruleReation.getUrlId());
					if (urlObj != null && Integer.parseInt(logTime)>=Integer.parseInt(urlObj.getStartDate()) 
							&& Integer.parseInt(logTime)<=Integer.parseInt(urlObj.getEndDate()) 
							&& !"-".equals(urlObj.getRuleValue()) 
							&& ("8".equals(urlObj.getPath()) || "10".equals(urlObj.getPath()))) {//path=10意味着改规则只能作用在url上,path=8可以作用在url和refer上
						if(urlObj.getRuleValue() !=null && !"".equals(urlObj.getRuleValue()))
							urlR.setUrlPattern(Pattern.compile(urlObj.getRuleValue()));
					}
					RuleURL referObj = handler.getRuleURL(ruleReation.getReferId());
					if (referObj != null && Integer.parseInt(logTime)>=Integer.parseInt(referObj.getStartDate()) 
							&& Integer.parseInt(logTime)<=Integer.parseInt(referObj.getEndDate())
							&& !"-".equals(referObj.getRuleValue())
							&& ("8".equals(referObj.getPath()) || "9".equals(referObj.getPath()))) {//path=9意味着改规则只能作用在refer上,path=8可以作用在url和refer上
						if(referObj.getRuleValue() !=null && !"".equals(referObj.getRuleValue()))
							urlR.setReferPattern(Pattern.compile(referObj.getRuleValue()));
					}
					if (urlR.getUrlPattern() != null || urlR.getReferPattern() !=null)
						tmp.add(urlR);
				}
				urlRecords2.put(typeId[k], tmp);
			} catch (Exception e) {
				continue;
			}
		}
		return true;
	}
	
	public void printConvertMsg(StringBuilder builder1, StringBuilder builder2, String url, String refer) {
		System.out.println("URL:");
		System.out.println("Befor Convert: " + builder1.toString());
		System.out.println("After Convert: " + url);
		System.out.println("Refer:");
		System.out.println("Befor Convert: " + builder2.toString());
		System.out.println("After Convert: " + refer);
	}
	
	public String convert2Exp(String exps) {
		if (exps == null || "".equals(exps.trim())) return "-";
		String []regExp = exps.split(",");
		if(regExp.length == 1) return regExp[0];
		urlBuilder.delete(0, urlBuilder.length());
		urlBuilder.append("(");
		for (int i=0; i<regExp.length; i++) {
			if (".*".equals(regExp[i])) return ".*"; //通用规则，包含所有内容
			if ("".equals(regExp[i].trim())) continue;
			urlBuilder.append(regExp[i]);
			if (i != regExp.length-1) urlBuilder.append("|");
		}
		urlBuilder.append(")");
		return urlBuilder.toString();
	}
	
	private boolean validateParam(String time) {
        return pattern.matcher(time).matches();
	}
	
	public static void main(String[] args) {
		UDFGetURLRecord udf = new UDFGetURLRecord();
		String refer = "/1.gif?cache=4124541&pre=http%3A//s.taobao.com/search%3Fq%3D%25BB%25E1%25BC%25C6%25B0%25EC%25B9%25AB%25D3%25C3%25C6%25B7%26sort%3Dcredit-desc&scr=1024x768&category=item_50012708&userid=&tid=6cef52db816997800b6a46238327ff86&channel=112&at_isb=0&at_autype=5_33517315&ad_id= 5451729810408116229";
		String url = "http://item.taobao.com/item.htm?id=8455365";
		String typeids = "185";
		String time = "20120717155240";
		
		String url1 = "http://detail.bendi.taobao.com/store/detail--id-f70b029d3f574cf28ea12af5b882916a";
		String refer1 = "/1.gif?cache=3134367&pre=http%3A//www.baidu.com/s%3Fwd%3D%25D0%25C7%25BF%25D5%25BD%25A1%25C9%25ED%25BB%25E1%25CB%25F9%26opt-webpage%3Don%26ie%3Dgbk&scr=1366x768&category=&userid=&channel=112&ad_id=";
		String typeids1 = "1794";//.*
		String time1 = "20120717155240";
		try {
//			System.out.println(udf.evaluate(time1, url1, refer1, typeids1).toString());
			udf = new UDFGetURLRecord();
			System.out.println(udf.evaluate(time1, url1, refer1, typeids1,"564","url"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
