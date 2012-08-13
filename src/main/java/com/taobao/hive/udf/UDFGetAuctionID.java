package com.taobao.hive.udf;

import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.hive.ql.udf.UDFRegExpExtract;
import org.apache.hadoop.io.Text;

public class UDFGetAuctionID extends UDF {
	
  private static UDFGetValueFromUrl getvaluefromurl = new UDFGetValueFromUrl();

  public UDFGetAuctionID() {

  }

  public Text evaluate(Text urlText) {
	  try{
		    String url = urlText.toString();
		    if(null == url || "".equals(url))
		           return null;
		    Text aucidText = new Text();
		    aucidText.clear();
		    String aucid = null;
		    int start = 0;
		    int end = 0;
		    
		    //add by pangui,for 无名良品
		    if (url.indexOf("http://item.lp.taobao.com")>=0) {
		    	url = url.replaceFirst("http://item.lp.taobao.com", "http://item.taobao.com");
		    }
		    if (url.indexOf("http://item.wmlp.com")>=0) {
		    	url = url.replaceFirst("http://item.wmlp.com", "http://item.taobao.com");
		    }
		    
		    //add by youliang for �̳������л�
		    if(url.indexOf("http://item.tmall.com")>=0){
		    	url = url.replaceFirst("http://item.tmall.com", "http://item.taobao.com");
		    }
		    if(url.indexOf("http://spu.tmall.com")>=0){
		    	url = url.replaceFirst("http://spu.tmall.com", "http://item.taobao.com");
		    }
		    
		    if(url.indexOf("http://item.taobao.com/")!=0){
		        return null;
		    }

		       url = url.replaceAll("//","/").replace("http:/","http://");
		       url = url.toLowerCase();
		       url = url.replaceAll(" ","");
		    if(url.indexOf("http://item.taobao.com/auction/item_detail.jhtml?item_id=") >= 0) {
		    	 
		        try {
		        	aucid = getvaluefromurl.evaluate(url,"item_id").toString();
		        } catch (Exception e) {
		            return null;
		        }
		    }

		    else if (url.indexOf("item_id=") >= 0 && url.indexOf("http://item.taobao.com/auction") == 0) {
		            start = url.indexOf("item_id=");
		            start += 8;
		            end = url.indexOf("&", start);
		            if (end == -1)
		                    end = url.length();
		            aucid = url.substring(start, end);
		    }else if(url.indexOf("item_num_id=")>=0 && url.indexOf("http://item.taobao.com/auction") == 0){
		            start = url.indexOf("item_num_id=");
		            start += 12;
		            end=url.indexOf("&", start);
		            if (end == -1){
		                    end = url.length();
		            }
		            aucid = url.substring(start, end);
		    }else if (url.indexOf("itemid=") >= 0 && url.indexOf("http://item.taobao.com/auction") == 0) {
		            start = url.indexOf("itemid=");
		            start += 7;
		            end = url.indexOf("&", start);
		            if (end == -1)
		                    end = url.length();
		            aucid = url.substring(start, end);
		    }else if(url.indexOf("http://item.taobao.com/item.htm?id=") == 0){
		            start = url.indexOf("id=");
		            start += 3;
		            end=url.indexOf("&", start);
		            if (end == -1){
		                    end = url.length();
		            }
		            aucid = url.substring(start, end);
		    }

		    else if(url.indexOf("http://item.taobao.com/item.htm?id=") == 0){
		        start = url.indexOf("id=");
		        start += 3;
		        end=url.indexOf("&", start);
		        if (end == -1){
		                end = url.length();
		        }
		        aucid = url.substring(start, end);
		    }
		    else if(url.indexOf("http://item.taobao.com/item.htm?item_id=") == 0){
		            try {
		            	aucid = getvaluefromurl.evaluate(url,"item_id").toString();
		            } catch (Exception e) {
		                return null;
		            }
		    	
		    }
		    else if(url.indexOf("http://item.taobao.com/item.htm?itemid=") == 0){
	            try {
	            	aucid = getvaluefromurl.evaluate(url,"itemid").toString();
	            } catch (Exception e) {
	                return null;
	            }
	    	
	        }
		    else if(url.indexOf("http://item.taobao.com/item.htm") == 0&&url.indexOf("id=")>=0){
		            try {
		            	aucid = getvaluefromurl.evaluate(url,"id").toString();
		            } catch (Exception e) {
		                return null;
		            }
		    	
		    }

		    //add by youliang
		    else if(url.indexOf("http://item.taobao.com/auction/item_detail.htm") >= 0) {
		    	if(url.indexOf("item_num_id")>=0){
		            try {
		            	aucid = getvaluefromurl.evaluate(url,"item_num_id").toString();
		            } catch (Exception e) {
		                return null;
		            }
		    	}
		    	if(url.indexOf("itemid")>=0){
		            try {
		            	aucid = getvaluefromurl.evaluate(url,"itemid").toString();
		            } catch (Exception e) {
		                return null;
		            }
		    	}
		    	if(url.indexOf("item_num id")>=0){
		            try {
		            	aucid = getvaluefromurl.evaluate(url,"item_num id").toString();
		            } catch (Exception e) {
		                return null;
		            }
		    	}
		    }
		    
		    
		    else if(url.indexOf("http://item.taobao.com/auction") == 0) {// item find 1
		            start = url.indexOf("-");
		            if (start == -1)
		                    return null;
		            start++;
		            start = url.indexOf("-", start);
		            if (start == -1)
		                    return null;
		            start++;
		            end = url.indexOf(".", start);

		            try {
		                    aucid = url.substring(start, end);
		            } catch (Exception e) {
		                    return null;
		            }
		    }
		    //add by youliang
		    else if(url.indexOf("http://item.taobao.com/item_detail.jhtml?item_id=") >= 0) {
		        try {
		        	aucid = getvaluefromurl.evaluate(url,"item_id").toString();
		        } catch (Exception e) {
		            return null;
		        }
		    }
		    else if(url.indexOf("http://item.taobao.com/item_detail.htm?item_num_id=") >= 0) {
		        try {
		        	aucid = getvaluefromurl.evaluate(url,"item_num_id").toString();
		        } catch (Exception e) {
		            return null;
		        }
		    }
		    else if(url.indexOf("http://item.taobao.com/item_detail-") >= 0) {
		    	 url = url.replace("http://item.taobao.com/item_detail-", "");
		    	 int dot = url.indexOf(".");
		    	 url = url.substring(0,dot);
		         try {
		        	 aucid = url.split("-")[1];
		         } catch (Exception e) {
		             return null;
		         }
		    	
		        
		    }
		    else if(url.indexOf("http://item.taobao.com/item_detail.jhtml") >= 0&&url.indexOf("item_id")>=0) {
		        try {
		        	aucid = getvaluefromurl.evaluate(url,"item_id").toString();
		        } catch (Exception e) {
		            return null;
		        }
		    }
		    else if(url.indexOf("http://item.taobao.com/item_detail.jhtml") >= 0&&url.indexOf("itemid")>=0) {
		        try {
		        	aucid = getvaluefromurl.evaluate(url,"itemid").toString();
		        } catch (Exception e) {
		            return null;
		        }
		    }
		    if(aucid==null){
		    	aucid = "";
		    }
		    aucidText.set(aucid);
		    return aucidText;
	  }catch(Exception e){
		  return null;
	  }

  }

  public static void main(String[] args){
	  UDFGetAuctionID test = new UDFGetAuctionID();
	  Text aucidText = new Text();
	  aucidText.set("http://spu.tmall.com/item.htm?itemId=184b86976131ffc3b9fc5092e8b99c23");
	  System.out.println(test.evaluate(aucidText));
  }


}

