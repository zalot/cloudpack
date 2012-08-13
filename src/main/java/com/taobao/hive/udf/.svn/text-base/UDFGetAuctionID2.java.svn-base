package com.taobao.hive.udf;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.Text;

import com.taobao.hive.util.UDFStringUtil;

@Description(name = "get_auctionid",
	    value = "_FUNC_(url) - get auction_id from a URL \n" +
	    		"_FUNC_(url,refer,flag) - get auction_id from a URL (flag=1,means need to validate url by item rule,flag=0 means no)",
	    extended = "URL examples: \n http://item.taobao.com/item.htm?id=1234 \n"
	    + "http://item.tmall.com/item.htm?id=1234 \n"
	    + "http://item.lp.taobao.com/item.htm?id=1234 \n"
	    + "http://item.wmlp.com/item.htm?id=1234 \n"
	    + "http://spu.tmall.com/spu-[]")
	    
public class UDFGetAuctionID2 extends UDF {
	
	private URL url = null;
	private Pattern pattern = null;
	private String lastUrl = null;
	private Text result = null;
	private Pattern spuPattern = null;
	private UDFGetValueFromRefer2 getvaluefromrefer = null;
	private UDFGetValueFromUrl getValueFromURL = null;
	private Text SPLITOR = null;
	private Text SPU_KEY1 = null;
	private Text SPU_KEY2 = null;
	private Text SPU_KEY_AT_ITEMID = null;
	private Pattern itemPattern = null;
	
	public UDFGetAuctionID2(){
		result = new Text();
		getvaluefromrefer = new UDFGetValueFromRefer2();
		getValueFromURL = new UDFGetValueFromUrl();
		SPLITOR = new Text("&");
		SPU_KEY1 = new Text("b2c_auction=");
		SPU_KEY2 = new Text("at_vtcspu=");
		SPU_KEY_AT_ITEMID = new Text("at_itemid=");
		initPattern();
//		initPatternLocal();
		if (spuPattern == null)
			spuPattern = Pattern.compile("^http://(detail\\.tmall\\.com/venus/spu_detail\\.htm" +
					"|list\\.3c\\.tmall\\.com/spu|spu\\.tmall\\.com)",Pattern.CASE_INSENSITIVE);
		if (itemPattern == null)
			itemPattern = Pattern.compile("^http://(item\\.tmall\\.com/|spu\\.tmall\\.com/|item\\.taobao\\.com/|" +
					"item\\.lp\\.taobao\\.com/|list\\.xie\\.tmall\\.com/spu_detail\\.htm|chaoshi\\.tmall\\.com/detail/view_detail\\.htm|" +
					"list\\.3c\\.tmall\\.com/spu|item\\.wmlp\\.com|detail\\.tmall\\.com|item\\.beta\\.taobao\\.com|" +
					"mall\\.hitao\\.com/item_detail\\.htm|baoxian\\.taobao\\.com/item\\.html|item\\.hitao\\.com/item" +
					"|ju\\.taobao\\.com/tg/home\\.htm|ju\\.taobao\\.com/tg/life_home\\.htm)",Pattern.CASE_INSENSITIVE);
	}
	
	private void initPattern(){
		try {
			Path dimPath = new Path("/group/taobao/taobao/dw/dim/");
			FileSystem fs = dimPath.getFileSystem(new Configuration());
			Path regFile = new Path("/group/taobao/taobao/dw/dim/get_auction_id_rule.txt");
			if (!fs.exists(regFile))
		        throw new IOException("file not found:/group/taobao/taobao/dw/dim/get_auction_id_rule.txt");
			FSDataInputStream in = fs.open(regFile);
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String line = null;
			while ((line = reader.readLine())!=null) {
				String[] patterns = line.split("<--->");
				if (patterns.length != 2) {
					continue;
				}
				if ("item".equals(patterns[0])) {
					itemPattern = Pattern.compile(patterns[1],Pattern.CASE_INSENSITIVE);
					System.out.println("init item pattern: " + patterns[1]);
				}
				if ("spu".equals(patterns[0])) {
					spuPattern = Pattern.compile(patterns[1],Pattern.CASE_INSENSITIVE);
					System.out.println("init spu pattern: " + patterns[1]);
				}
			}
		} catch(Exception e) {
			itemPattern = null;
			spuPattern = null;
			e.printStackTrace();
		}
	}
	
	private void initPatternLocal(){
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("D:\\reg_files\\get_auction_id_rule.txt")));
			String line = null;
			while ((line = reader.readLine())!=null) {
				String[] patterns = line.split("<--->");
				if (patterns.length != 2) {
					continue;
				}
				if ("item".equals(patterns[0])) itemPattern = Pattern.compile(patterns[1],Pattern.CASE_INSENSITIVE);
				if ("spu".equals(patterns[0])) spuPattern = Pattern.compile(patterns[1],Pattern.CASE_INSENSITIVE);
			}
		} catch (Exception e) {
			itemPattern = null;
			spuPattern = null;
			e.printStackTrace();
		}
	}
	
	public Text evaluate(Text sUrl) {
		return evaluate(sUrl,null,"1");
	}
	
	//flag=0:no need to check url,flag=1:need to check url
	//item_id=,id=,item_num_id=,item_num
	public Text evaluate(Text sUrl,Text refer, String flag) {
		if (sUrl == null || "".equals(sUrl.toString())) {
			result.set("");
			return result;
		}
		String urlstr = sUrl.toString().trim().toLowerCase();
		Text returnValue = null;
		if ("1".equals(flag)) { // check url is a item url
			Matcher m = itemPattern.matcher(urlstr);
			if (m.find()) {
				returnValue = getAuctionId(urlstr,refer);
			}
		} else {
			returnValue = getAuctionId(urlstr,refer);
		}
		if (returnValue == null) {
			result.set("");
			return result;
		}
		return returnValue;
	}
	
	private Text getAuctionId(String urlstr, Text refer) {
		Matcher m = spuPattern.matcher(urlstr);
		if (m.find()) {
			return getSPUAcutionId(urlstr,refer) ;
		} else {
			Text value = getURLAuctionId(urlstr);
			if (value == null) {
				if(urlstr.indexOf("http://item.taobao.com/item_detail-") >= 0) {
					String url = urlstr.replace("http://item.taobao.com/item_detail-", "");
			    	int dot = url.indexOf(".");
			    	url = url.substring(0,dot);
			        try {
			        	String aucid = url.split("-")[1];
			        	value = new Text(aucid);
			        	return value;
			        } catch (Exception e) {
			            return null;
			        }
			    } else if (UDFStringUtil.indexOf(urlstr, "http://item.hitao.com/item-")>=0) {
			    	int flag = UDFStringUtil.indexOf(urlstr, "http://item.hitao.com/item-");
					int index = urlstr.indexOf(".htm", flag);
					int end = index == -1 ? urlstr.length() : index;
					value = new Text();
					value.set(urlstr.substring(flag + "http://item.hitao.com/item-".length(), end));
			    }
			}
			return value;
		}
	}
	
	private Text getSPUAcutionId(String urlstr,Text refer) {
		if (refer == null || "".equals(refer.toString())) return null;
		if (UDFStringUtil.indexOf(urlstr, "http://detail.tmall.com/venus/spu_detail.htm")==0){
			Text spuValue = getvaluefromrefer.evaluate(refer,SPLITOR,SPU_KEY_AT_ITEMID);
			if (spuValue != null && !"".equals(spuValue.toString())) {
				return spuValue;
			} else {
				spuValue = getValueFromURL.evaluate(urlstr, "default_item_id");
				if (spuValue != null && !"".equals(spuValue.toString())) {
					return spuValue;
				} else {
					spuValue = getValueFromURL.evaluate(urlstr, "mallstitemid");
					return spuValue;
				}
			}
		} else if (UDFStringUtil.indexOf(urlstr, "http://spu.tmall.com/")==0) {
			return getvaluefromrefer.evaluate(refer,SPLITOR,SPU_KEY1);
		} else if (UDFStringUtil.indexOf(urlstr, "http://list.3c.tmall.com/spu")==0) {
			Text value = getvaluefromrefer.evaluate(refer,SPLITOR,SPU_KEY2);
			if (value == null) return null;
			String valueStr = value.toString();
			String[] values = valueStr.split("_");
			if (values.length == 4) {
				result.set(values[3]);
				return result;
			}
		}
		return null;
	}
	
	private Text getURLAuctionId(String urlstr) {
		if (pattern == null) {
			pattern = Pattern.compile("(&|^)(id|item_id|item_num_id|item_num)=([^&]*)");
		}
		if (!urlstr.equals(lastUrl)) {
			try {
				url = new URL(urlstr);
			} catch (Exception e) {
				return null;
			}
			lastUrl = urlstr;
		}
		String query = url.getQuery();
		if (query == null) return null;
		Matcher mather = pattern.matcher(query);
		if (mather.find()) {
			result.set(mather.group(3));
			return result;
		}
		return null;
	}
	
	public static void main(String[] args) {
		try {
			UDFGetAuctionID2 udf = new UDFGetAuctionID2();
			Text url = new Text("http://item.hitao.com/item-6934145496.htm");
			System.out.println(udf.evaluate(url).toString());
			url = new Text("http://ju.taobao.com/tg/home.htm?spm=608.1000562.0.1&item_id=15147840808");
			System.out.println(udf.evaluate(url).toString());
			url = new Text("http://ju.taobao.com/tg/life_home.htm?spm=608.1000526.10.1&item_id=17414516441");
			System.out.println(udf.evaluate(url).toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

