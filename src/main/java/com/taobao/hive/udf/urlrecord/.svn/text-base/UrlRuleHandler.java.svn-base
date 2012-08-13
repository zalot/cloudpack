package com.taobao.hive.udf.urlrecord;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.io.Text;

import com.taobao.hive.udf.UDFGetValueFromRefer;
import com.taobao.hive.udf.UDFURLDecode;

public class UrlRuleHandler {
	private UDFGetValueFromRefer getValueFormRefer = new UDFGetValueFromRefer();
	private UDFURLDecode decode = new UDFURLDecode();
	private boolean initFlag = false;
	private Map<String,RuleURL> ruleURLs = new ConcurrentHashMap<String,RuleURL>();
	private Map<String,RuleRelation> ruleRelation = new ConcurrentHashMap<String,RuleRelation>();
	private Map<String,RuleBusiness> ruleBusiness = new ConcurrentHashMap<String,RuleBusiness>();
	private Map<String,RuleURLProperty> ruleURLProperty = new ConcurrentHashMap<String,RuleURLProperty>();
	
	public void init() throws IOException{
		if(!initFlag) { //没有初始化过，则进行初始化
			loadData(Constants.FILE_RULE_BUSINESS_VALUE,Constants.FILE_RULE_BUSINESS);
			loadData(Constants.FILE_RULE_RELATION_VALUE,Constants.FILE_RULE_RELATION);
			loadData(Constants.FILE_RULE_URL_VALUE,Constants.FILE_RULE_URL);
			loadData(Constants.FILE_RULE_URL_PROPERTY_VALUE,Constants.FILE_RULE_URL_PROPERTY);
			initFlag = true;
		}
	}
	
	public void loadData(int flag,String file) throws IOException{
		BufferedReader reader = null;
		String line = null;
		reader = new BufferedReader(new InputStreamReader(new FileInputStream(file),"GBK"));
		while (((line = reader.readLine()) != null)) {
			try{
				String[] strList = line.split("\"");
				switch (flag){
					case Constants.FILE_RULE_URL_VALUE:
						assembleRuleURL(strList);
						break;
					case Constants.FILE_RULE_BUSINESS_VALUE:
						assembleRuleBusiness(strList);
						break;
					case Constants.FILE_RULE_RELATION_VALUE:
						assembleRuleRelation(strList);
						break;
					case Constants.FILE_RULE_URL_PROPERTY_VALUE:
						assembleRuleURLProperty(strList);
						break;
					default: continue; 
				}
		    }catch(Exception e){
			    continue;
		    }
		}
	}
	
	private void assembleRuleURL(String[] strList) throws Exception{
		if (strList.length<7) return;
		RuleURL rule = new RuleURL();
		rule.setId(strList[0]);
		rule.setRuleName(strList[1]);
		String ruleValue = strList[2];
		if (!ruleValue.equals(".*")) {
			if (ruleValue.startsWith(".*")) ruleValue = ruleValue.substring(2);
			if (ruleValue.endsWith(".*")) ruleValue = ruleValue.substring(0,ruleValue.length()-2);
		}
		rule.setRuleValue(ruleValue);
		rule.setPath(strList[3]);
		rule.setStatus(strList[4]);
		rule.setStartDate(strList[5].replace("-", ""));
		rule.setEndDate(strList[6].replace("-", ""));
		rule.setRuleMatcher(Pattern.compile(strList[2]).matcher(""));
		if("0".equals(rule.getStatus())){
			ruleURLs.put(rule.getId(), rule);
        }
	}
	
	private void assembleRuleRelation(String[] strList) throws Exception{
		if (strList.length<4) return;
		RuleRelation rule = new RuleRelation();
		rule.setId(strList[0]);
		rule.setUrlId(strList[1]);
		rule.setReferId(strList[2]);
		rule.setStatus(strList[3]);
		if("0".equals(rule.getStatus())){
			ruleRelation.put(rule.getId(), rule);
        }
	}
	
	private void assembleRuleBusiness(String[] strList) throws Exception{
		if (strList.length<7) return;
		RuleBusiness rule = new RuleBusiness();
		rule.setId(strList[0]);
		rule.setStatus(strList[3]);
		rule.setRelationIds(Arrays.asList(strList[6].split(",")));
		if("0".equals(rule.getStatus())){
			ruleBusiness.put(rule.getId(), rule);
        }
	}
	
	private void assembleRuleURLProperty(String[] strList) throws Exception{
		if (strList.length<13) return;
		RuleURLProperty rule = new RuleURLProperty();
		rule.setId(strList[0]);
		rule.setUrlId(strList[1]);
		rule.setPropertyId(strList[2]);
		rule.setStatus(strList[3]);
		rule.setPropertyName(strList[6]);
		rule.setAtPath(strList[7]);
		rule.setPath(strList[8]);
		rule.setIsDefault(strList[9]);
		rule.setStartPath(strList[10]);
		rule.setEndPath(strList[11]);
		rule.setAscDesc(strList[12]);
		if("0".equals(rule.getStatus())){
			ruleURLProperty.put(strList[1] + "," + strList[2], rule);
        }
	}
	
	public RuleURL getRuleURL(String ruleId) {
		return ruleURLs.get(ruleId);
	}
	
	public RuleRelation getRuleRelation(String relationId) {
		return ruleRelation.get(relationId);
	}
	
	public RuleBusiness getRuleBusiness(String bizId) {
		return ruleBusiness.get(bizId);
	}
	
	public RuleURLProperty getRuleURLProperty(String propertyId) {
		return ruleURLProperty.get(propertyId);
	}
	
	public Map<String,RuleURL> getRuleURL() {
		return ruleURLs;
	}
	
	public Map<String,RuleRelation> getRuleRelation() {
		return ruleRelation;
	}
	
	public Map<String,RuleBusiness> getRuleBusiness() {
		return ruleBusiness;
	}
	
	public Map<String,RuleURLProperty> getRuleURLProperty() {
		return ruleURLProperty;
	}
	/*
	 * 
	 * validate url
	 */
	public String validate(String ruleId,int type,String time,String url,String prefix){
		RuleURL rule = getRuleURL(ruleId);
		String logTime = time.substring(0,8);
		if (rule!=null && Integer.parseInt(logTime)>=Integer.parseInt(rule.getStartDate()) 
				&& Integer.parseInt(logTime)<=Integer.parseInt(rule.getEndDate())){
			Matcher matcher = rule.getRuleMatcher();
			if (matcher == null) {
				matcher = Pattern.compile(rule.getRuleValue()).matcher(url);
				rule.setRuleMatcher(matcher);
			}
			switch (type) {
			case Constants.VALIDATE_TYPE_URL:
				if("0".equals(rule.getPath()) || "2".equals(rule.getPath())) {
					if(matcher !=null && matcher.reset(url).find()){
    				    return "true";
    		        }
				}
			case Constants.VALIDATE_TYPE_REFER:
				if("1".equals(rule.getPath()) || "2".equals(rule.getPath())){//作用于refer
         			String pre = "";
         			if(url!=null){
         	   			pre = getValueFormRefer.evaluate(new Text(url),new Text("&"),new Text(prefix)).toString();
         	   			pre = decode.evaluate(new Text(pre)).toString();//decode操作
         			}
         			if(matcher !=null && matcher.reset(url).find()){
					    return "true";
			        }
				}
			}
		}
		return "false";
	}
	
	public boolean validateBiz(String time,String url,String refer,String bizId,String prefix) {
		if (bizId == null || "".equals(bizId)) return false;
		RuleBusiness bizObj = getRuleBusiness(bizId);
		if (bizObj==null || !"0".equals(bizObj.getStatus())) return false; //判断业务规则状态是否可用
		List<String> relationIds = bizObj.getRelationIds();
		if (relationIds == null || relationIds.size()==0) return false;
		for (String id : relationIds) {
			RuleRelation obj = getRuleRelation(id);
			if (obj == null || !"0".equals(bizObj.getStatus())) continue;
			RuleURL urlObj = getRuleURL(obj.getUrlId());
			RuleURL referObj = getRuleURL(obj.getReferId());
			if (urlObj == null && referObj == null) continue;
			if (urlObj == null && referObj != null) {
				String result = validate(referObj.getId(),Constants.VALIDATE_TYPE_REFER,time,refer,prefix);
				if ("true".equals(result)) return true;
			} else if (urlObj != null && referObj == null) {
				String result = validate(urlObj.getId(),Constants.VALIDATE_TYPE_URL,time,url,prefix);
				if ("true".equals(result)) return true;
			} else if (urlObj != null && referObj !=null) {
				String result1 = validate(referObj.getId(),Constants.VALIDATE_TYPE_REFER,time,refer,prefix);
				String result2 = validate(urlObj.getId(),Constants.VALIDATE_TYPE_URL,time,url,prefix);
				if ("true".equals(result1) && "true".equals(result2)) return true;
			}
		}
		return false;
	}
	
	public String getPropValue(String time,String url,String refer,String bizId, String propid,String flag, String prefix) {
		if (bizId == null || "".equals(bizId)) return null;
		RuleBusiness bizObj = getRuleBusiness(bizId);
		if (bizObj==null || !"0".equals(bizObj.getStatus())) return null; //判断业务规则状态是否可用
		List<String> relationIds = bizObj.getRelationIds();
		if (relationIds == null || relationIds.size()==0) return null;
		for (String id : relationIds) {
			RuleRelation obj = getRuleRelation(id);
			if (obj == null || !"0".equals(bizObj.getStatus())) continue;
			RuleURL urlObj = getRuleURL(obj.getUrlId());
			RuleURL referObj = getRuleURL(obj.getReferId());
			if (urlObj == null && referObj == null) continue;
			if (urlObj == null && referObj != null && "refer".equals(flag)) {
				String result = validate(referObj.getId(),Constants.VALIDATE_TYPE_REFER,time,refer,prefix);
				if ("true".equals(result)){
					return getValue(referObj.getId(),Constants.VALIDATE_TYPE_REFER,propid,refer,prefix);
				}
			} else if (urlObj != null && referObj == null && "url".equals(flag)) {
				String result = validate(urlObj.getId(),Constants.VALIDATE_TYPE_URL,time,url,prefix);
				if ("true".equals(result)){
					return getValue(urlObj.getId(),Constants.VALIDATE_TYPE_URL,propid,refer,prefix);
				}
			} else if (urlObj != null && referObj !=null) {
				String result1 = validate(referObj.getId(),Constants.VALIDATE_TYPE_REFER,time,refer,prefix);
				String result2 = validate(urlObj.getId(),Constants.VALIDATE_TYPE_URL,time,url,prefix);
				if ("true".equals(result1) && "true".equals(result2)){
					if ("refer".equals(flag)) {
						return getValue(referObj.getId(),Constants.VALIDATE_TYPE_REFER,propid,refer,prefix);
					} else if ("url".equals(flag)) {
						return getValue(urlObj.getId(),Constants.VALIDATE_TYPE_URL,propid,refer,prefix);
					}
				}
			}
		}
		return null;
	}
	
	public String getPropValue(String time,String url,String refer,String bizId, String propid,String prefix) {
		if (bizId == null || "".equals(bizId)) return null;
		RuleBusiness bizObj = getRuleBusiness(bizId);
		if ("562".equals(bizId)) {
			  System.out.println(1);
		  }
		if (bizObj==null || !"0".equals(bizObj.getStatus())) return null; //判断业务规则状态是否可用
		List<String> relationIds = bizObj.getRelationIds();
		if (relationIds == null || relationIds.size()==0) return null;
		for (String id : relationIds) {
			RuleRelation obj = getRuleRelation(id);
			if (obj == null || !"0".equals(bizObj.getStatus())) continue;
			RuleURL urlObj = getRuleURL(obj.getUrlId());
			RuleURL referObj = getRuleURL(obj.getReferId());
			if (urlObj == null && referObj == null) continue;
			if (urlObj == null && referObj != null) {
				String result = validate(referObj.getId(),Constants.VALIDATE_TYPE_REFER,time,refer,prefix);
				if ("true".equals(result)){
					return getValue(referObj.getId(),Constants.VALIDATE_TYPE_REFER,propid,refer,prefix);
				}
			} else if (urlObj != null && referObj == null) {
				String result = validate(urlObj.getId(),Constants.VALIDATE_TYPE_URL,time,url,prefix);
				if ("true".equals(result)){
					return getValue(urlObj.getId(),Constants.VALIDATE_TYPE_URL,propid,refer,prefix);
				}
			} else if (urlObj != null && referObj !=null) {
				String result1 = validate(referObj.getId(),Constants.VALIDATE_TYPE_REFER,time,refer,prefix);
				String result2 = validate(urlObj.getId(),Constants.VALIDATE_TYPE_URL,time,url,prefix);
				if ("true".equals(result1) && "true".equals(result2)){
					String value1 = getValue(referObj.getId(),Constants.VALIDATE_TYPE_REFER,propid,refer,prefix);
					String value2 = getValue(urlObj.getId(),Constants.VALIDATE_TYPE_URL,propid,refer,prefix);
					value1 = value1 == null ? "" : value1;
					value2 = value2 == null ? "" : value2;
					return value1+","+value2;
				}
			}
		}
		return null;
	}
	
	//source的值是url或者refer
	private String getValueByName(String source,String name){
		if(source!=null&&source.indexOf("&"+name)<0&&source.indexOf("?"+name)<0){
			return null;
		}
		String value = getValueFormRefer.evaluate(new Text(source), new Text("&"), new Text(name)).toString();
		return value;
	}
	
	private String between(String value,String begin,String end){
		if("".equals(begin)&&"".equals(end)){
			return value;
		}
		int index1 = value.lastIndexOf(begin);
		if("".equals(begin)){
			index1 = 0;
		}
		if(!end.equals("end")){
			int index2 = value.indexOf(end);
			if("".equals(end) || index2 == -1){
				index2 =  value.length();
			}
			value = value.substring(index1+begin.length(), index2);
			return value;
		}else{
			value = value.substring(index1+begin.length());
			return value;
		}
	}
	
	private String getLocationDefault(String source,String at_path,String path){
		   String urlC = between(source,"/",".htm");
		   if("|X|X|".equals(at_path)){
			   return urlC;
		   }
		   if(urlC.indexOf(at_path)<0){
			   return null;
		   }
		   if("".equals(at_path)){
			   return null;
		   }
		   String[] strList = urlC.split(at_path);
		   String value = strList[strList.length-Integer.parseInt(path)]; 
	       return value;
	  }
	  
	  private String getLocationValue(String source,String at_path,String path,String start_path,String end_path,String asc_desc){
		   String urlC = between(source,start_path,end_path);
		   if("|X|X|".equals(at_path)){
			   return urlC;
		   }
		   if(urlC.indexOf(at_path)<0){//url中没有这个分隔符就返回null
			   return null;
		   }
		   if("".equals(at_path)){
			   return null;
		   }
		   String[] strList = urlC.split(at_path);
		   if("0".equals(asc_desc)){//倒数
    		 String value = strList[strList.length-Integer.parseInt(path)];
			     return value;
		   }else{//顺数
    		 String value = strList[Integer.parseInt(path)-1];
			     return value;
		   }  
	  }
	  
	  private String getValue(String ruleId, int rule_type,String property_id,String url,String prefix){
		  RuleURLProperty prop = getRuleURLProperty(ruleId + "," + property_id);
		  if (prop == null) return null;
		  switch (rule_type) {
		  	case Constants.VALIDATE_TYPE_REFER:
		  		if(prop.getPropertyName()!=null&&!prop.getPropertyName().trim().equals("")){
					return getValueByName(url,prop.getPropertyName());
	            }else{
		        	if("0".equals(prop.getIsDefault())){
		        		return getLocationDefault(url,prop.getAtPath(),prop.getPath());
		        	}else{
		           		return getLocationValue(url,prop.getAtPath(),prop.getPath(),prop.getStartPath(),prop.getEndPath(),prop.getAscDesc()); 
		        	}
	        	}
		  	case Constants.VALIDATE_TYPE_URL:
		  		if(prop.getPropertyName()!=null&&!prop.getPropertyName().trim().equals("")){
	    			return getValueByName(url,prop.getPropertyName());
	            }else{
	        		String pre = "";
	        		if(url!=null){
	        			pre = getValueFormRefer.evaluate(new Text(url),new Text("&"),new Text(prefix)).toString();
	           			pre = decode.evaluate(new Text(pre), "flag1", "flag2").toString();//decode操作
	        		}
		        	if("0".equals(prop.getIsDefault())){
		    			return getLocationDefault(pre,prop.getAtPath(),prop.getPath());
		          	}else{
		           		return getLocationValue(pre,prop.getAtPath(),prop.getPath(),prop.getStartPath(),prop.getEndPath(),prop.getAscDesc()); 
		           	}
	            }
		  }
          return null;
	  }
	  
	  public String getValidateRel(String time,String url,String refer,String bizId,String prefix) {
		  String bizRel = "";
		  if (bizId == null || "".equals(bizId)) return "";
		  RuleBusiness bizObj = getRuleBusiness(bizId);
		  if (bizObj==null || !"0".equals(bizObj.getStatus())) return ""; //判断业务规则状态是否可用
		  List<String> relationIds = bizObj.getRelationIds();
		  if (relationIds == null || relationIds.size()==0) return "";
		  for (String id : relationIds) {
				RuleRelation obj = getRuleRelation(id);
				if (obj == null || !"0".equals(bizObj.getStatus())) continue;
				RuleURL urlObj = getRuleURL(obj.getUrlId());
				RuleURL referObj = getRuleURL(obj.getReferId());
				if (urlObj == null && referObj == null) continue;
				if (urlObj == null && referObj != null) {
					String result = validate(referObj.getId(),Constants.VALIDATE_TYPE_REFER,time,refer,prefix);
					if ("true".equals(result)){
						bizRel += bizId+":"+id+";";
					}
				} else if (urlObj != null && referObj == null) {
					String result = validate(urlObj.getId(),Constants.VALIDATE_TYPE_URL,time,url,prefix);
					if ("true".equals(result)){
						bizRel += bizId+":"+id+";";
					}
				} else if (urlObj != null && referObj !=null) {
					String result1 = validate(referObj.getId(),Constants.VALIDATE_TYPE_REFER,time,refer,prefix);
					String result2 = validate(urlObj.getId(),Constants.VALIDATE_TYPE_URL,time,url,prefix);
					if ("true".equals(result1) && "true".equals(result2)){
						bizRel += bizId+":"+id+";";
					}
				}
			}
			return bizRel;
		}
	  
	  public String getRelValue(String time,String url,String refer,String bizId, String propid, String prefix) {
		    String bizRel = "";
			if (bizId == null || "".equals(bizId)) return "";
			RuleBusiness bizObj = getRuleBusiness(bizId);
			if (bizObj==null || !"0".equals(bizObj.getStatus())) return ""; //判断业务规则状态是否可用
			List<String> relationIds = bizObj.getRelationIds();
			if (relationIds == null || relationIds.size()==0) return "";
			for (String id : relationIds) {
				RuleRelation obj = getRuleRelation(id);
				if (obj == null || !"0".equals(bizObj.getStatus())) continue;
				RuleURL urlObj = getRuleURL(obj.getUrlId());
				RuleURL referObj = getRuleURL(obj.getReferId());
				if (urlObj == null && referObj == null) continue;
				if (urlObj == null && referObj != null) {
					String result = validate(referObj.getId(),Constants.VALIDATE_TYPE_REFER,time,refer,prefix);
					if ("true".equals(result)){
						String value = getValue(referObj.getId(),Constants.VALIDATE_TYPE_REFER,propid,refer,prefix);
						if (value != null && !"".equals(value)) bizRel += bizId + ":" + id + ":"  + value+";";
					}
				} else if (urlObj != null && referObj == null) {
					String result = validate(urlObj.getId(),Constants.VALIDATE_TYPE_URL,time,url,prefix);
					if ("true".equals(result)){
						String value =  getValue(urlObj.getId(),Constants.VALIDATE_TYPE_URL,propid,refer,prefix);
						if (value != null && !"".equals(value)) bizRel += bizId + ":" + id + ":"  + value+";";
					}
				} else if (urlObj != null && referObj !=null) {
					String result1 = validate(referObj.getId(),Constants.VALIDATE_TYPE_REFER,time,refer,prefix);
					String result2 = validate(urlObj.getId(),Constants.VALIDATE_TYPE_URL,time,url,prefix);
					if ("true".equals(result1) && "true".equals(result2)){
						String value1 = getValue(referObj.getId(),Constants.VALIDATE_TYPE_REFER,propid,refer,prefix);
						String value2 = getValue(urlObj.getId(),Constants.VALIDATE_TYPE_URL,propid,refer,prefix);
						value1 = value1 == null ? "" : value1;
						value2 = value2 == null ? "" : value2;
						bizRel += bizId + ":" + id + ":"  + value1 + "," + value2 +";";
					}
				}
			}
			return bizRel;
		}

}
