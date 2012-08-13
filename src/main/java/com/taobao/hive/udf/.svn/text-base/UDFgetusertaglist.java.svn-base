package com.taobao.hive.udf;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import java.io.InputStreamReader;
import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.Text;


public class UDFgetusertaglist extends UDF{

	  Text result = new Text();
	  private static final String FILE1 = "./heart_stbTAG-0";
	  private static final String FILE2 = "./heart_stbTAG_GROUP-0";
      
	  Set<Integer> tag_regular = new HashSet<Integer>();
	  //private static Set<Integer> tag_group_filter = new HashSet<Integer>();
	  Map<Integer, Integer> m_group2tag_count = new HashMap< Integer, Integer >();
	  Map<Integer,HashSet<Integer>> m_tag2group_list = new HashMap<Integer,HashSet<Integer>>();	  	  
	  
	  public UDFgetusertaglist() {
		  
	  }
	  public Text evaluate(String user_data)
	  {
         if(user_data == null) {
			 //System.out.println("user_data is null");
			 user_data = "";
		 }
		 
		 
	      HashSet<Integer> tag_result = new HashSet<Integer>();
	      
	      get_valid_tag_lists(tag_result,user_data.toString());
	      
	      //System.out.println("test0");
	      System.out.println(tag_result);
	      //System.out.println("mid result");
	      //System.out.println("test1");
	      
	      //处理tag表
	      BufferedReader reader1 = null;
	      String line1 = null;
	      String[] strList1 = null;
	      
	      
		  try {
	         reader1 = new BufferedReader(new InputStreamReader(new FileInputStream(FILE1)));
	         while (((line1 = reader1.readLine()) != null)) {
	        	   
				   strList1   = line1.split("\t");
				   if (isNumeric(strList1[0].toString())){
					       int tagid = Integer.parseInt(strList1[0].toString());                       
					      //int isFilter = Integer.parseInt(strList1[10].toString());					   
					   
					   tag_regular.add(tagid);
					   //if (isFilter != 1){
					   //    tag_group_filter.add(tagid);
					   //}					   
				   }
	         }//end while
	        } catch (FileNotFoundException e) {
				System.err.println("file1: not exist");
				e.printStackTrace();
			} catch (IOException e) {
				System.err.println(": read line1 error");
				e.printStackTrace();
			} catch(NumberFormatException e){
				System.err.println(strList1[0] + " parse string1 to integer error.");
				e.printStackTrace();
			}		  
			//处理tag_group表
			BufferedReader reader2 = null;
		    String line2 = null;
		    String[] strList2 = null;
			try {
				reader2 = new BufferedReader(new InputStreamReader(new FileInputStream(FILE2)));
				while((line2 = reader2.readLine()) != null)
				{
					    strList2 = line2.split("\t");
					    
					    if (!isNumeric(strList2[0].toString())){
							continue;
						}
					    
						int tag_groupid = Integer.parseInt(strList2[0].toString());
						String expressions = strList2[3].toString();
						//int isFilter = Integer.parseInt(strList2[9].toString());
						//if (isFilter != 1){
						//	tag_group_filter.add(tag_groupid);
						//}
						
						if (expressions.contains("&") && !expressions.contains("|")){
							String[] tags_id = expressions.split("&");
							m_group2tag_count.put(tag_groupid, tags_id.length);
							for(String tagid :tags_id){		
								if (!isNumeric(tagid)){
									break;
								}
								int tid = Integer.parseInt(tagid);
								HashSet<Integer> ts = m_tag2group_list.get(tid);
								if (ts != null){
									ts.add(tag_groupid);
								}else{
									ts = new HashSet<Integer>();
									ts.add(tag_groupid);
									m_tag2group_list.put(tid, ts);
								}
							}
						}else if (!expressions.contains("&") && expressions.contains("|")){
							String[] tags_id = expressions.split("\\|");
							m_group2tag_count.put(tag_groupid,1);
							
							for(String tagid :tags_id){
								if (!isNumeric(tagid)){
									break;
								}
								int tid = Integer.parseInt(tagid);
								HashSet<Integer> ts = m_tag2group_list.get(tid);
								if (ts != null){
									ts.add(tag_groupid);
								}else{
									ts = new HashSet<Integer>();
									ts.add(tag_groupid);
									m_tag2group_list.put(tid, ts);
								}
							}
						}else{
							continue;
						}
					}									
			} catch (FileNotFoundException e) {
				System.err.println("file2: not exist");
				e.printStackTrace();
			} catch (IOException e) {
				System.err.println(": read line2 error");
				e.printStackTrace();
			} catch(NumberFormatException e){
				System.err.println(strList2[0] + " parse string2 to integer error.");
				e.printStackTrace();
			}
	        //
			
			//结果集
			ArrayList<Integer> tag_field_show = new ArrayList<Integer>();
			//ArrayList<Integer> tag_field_index = new ArrayList<Integer>();
			
			Map<Integer,Integer> m_group_tags = new HashMap<Integer,Integer>();
			if (tag_result.size()>0){
				//tag_regular
				for (int tag_id : tag_result){
					if (tag_regular.contains(tag_id)){
						//if (tag_group_filter.contains(tag_id)){
							tag_field_show.add(tag_id);
						//}												
						if (m_tag2group_list.containsKey(tag_id)){
							HashSet<Integer> ts = m_tag2group_list.get(tag_id);
							for (int group_id : ts){
								if (m_group_tags.containsKey(group_id)){
									int count = m_group_tags.get(group_id);
									count = count + 1;
									
									m_group_tags.put(group_id, count);
								}else{
									m_group_tags.put(group_id, 1);
								}
							}
						}
					}
				}//end for
				
				for (Entry<Integer, Integer> group_count : m_group_tags.entrySet()){
					int key = group_count.getKey();
					if (m_group2tag_count.containsKey(key)){
						if (group_count.getValue() >= m_group2tag_count.get(key)){
							//if (tag_group_filter.contains(key)){								
								tag_field_show.add(key);
							//}
						}
					}					
				}
			}
			String results = "";
			for(Integer i : tag_field_show){
				if (results == ""){
					results = i.toString();
				}
				else{
     				results = results.concat(",").concat(i.toString());
				}				
			}
			
			
			//System.out.println(tag_field_show);
			//System.out.println("test2");		
			result.set(results);
			return result;			
	  }
	  
	  
	  private void parseTaglists(HashSet<Integer> tag_list,String features) {
	         try {
	              int start_index = features.indexOf("tags:");
	              int end_index = features.indexOf(";", start_index);      
	              if (-1 == end_index) {
	                  end_index = features.length();
	              }
	      
	              String[] taglists = features.substring(start_index + "tags:".length(),end_index).split(",");
	              for(int i=0;i<taglists.length;i++) {
	                  tag_list.add(Integer.parseInt(taglists[i].trim()));
	              }
	         } 
	         catch (ArrayIndexOutOfBoundsException e) {
	              e.printStackTrace(System.err);
	         }catch(NumberFormatException e) {
	              System.err.println(e.getMessage());
	              System.err.println("NumberFormatException at: " + features);
	          }
	     }

	  
	  public long getlong(int vid, int pid) {
		      long l = (long) vid;
		      l = l << 32;
		      l += pid;

		      return l;
      }
	  
	  public String get_tags(String features){
	      String result = "";
	      String token = "tags:";
	      String[] items = features.split(";");
	      for (int i=0;i<items.length;i++){
	         if (items[i].startsWith(token)){
	            result = items[i].substring(items[i].indexOf(":")+1);
	         }
	      }
	      return result;
	   }
	  
	  public int get_tag_type(int tagId){
	      return tagId & 0x3f;
	   }
	   /**
	    * 判断字符串中字符是否全是数字
	    * @param str 要解析的字符串
	    * @return true是数字false不是数字
	    */
	  public static boolean isNumeric(String str){
	       if (str == null){
	          return false;
	       }
	       if (str.length() ==0){
	          return false;
	       }
	       //正则表达式判断是否是数字包括负数
	        Pattern pattern = Pattern.compile("-*[0-9]*(\\.)?[0-9]*");
	        boolean regFlag =  pattern.matcher(str).matches();
	        return regFlag;
	    }
	  
	  public void get_valid_tag_lists(HashSet<Integer> tag_result,String user_data){
			try{
				String[] user_datas = user_data.split("~");				
				//category_level_one
				//s_pvs
				//Set<Integer> stags = new HashSet<Integer>();
				
				if (user_data== null || user_data.length() ==0){
					return;
				}
				//first user-data
				//1,3,65,67~4-1512,68-1102~5-1101-20000:1029,69-1512-20000:394;20001:666
				for(int index = 0;index<user_datas.length;index++){
					String[] datas = user_datas[index].split(",");

					for(String s : datas){
						if (0 == index){
							if (!isNumeric(s)){
								continue;
							}
							int tag_id = Integer.parseInt(s);
							int tag_type = get_tag_type(tag_id);
							if (tag_type == 1 ||tag_type == 3||tag_type == 7||tag_type == 8){
								tag_result.add(tag_id);
							}
						}else{
							String[] tag_infos = s.split("-");
							if (tag_infos.length <2|| !isNumeric(tag_infos[0])){
								continue;
							}
							int tag_id = Integer.parseInt(tag_infos[0]);
							int tag_type = get_tag_type(tag_id);
							if (1 == index){
								if (tag_infos.length ==2){
									if (tag_type == 4){										
											tag_result.add(tag_id);
										}
									}								
							}else if (2 == index){
								if (tag_infos.length ==3){
									if (tag_type == 5){
										//category
										tag_result.add(tag_id);										
									}
								}
							}
						}//end if 
					}
				}//end for
			}catch (Exception e) {
				System.err.println("Exception:" + e.getMessage());
				e.printStackTrace();
			}
		}
	  
	  //(String user_data,String features
      //,String category_level_one,String property,String spu_features,String cat_features)
      public static void main(String[] args){
    	  UDFgetusertaglist test = new UDFgetusertaglist();
          System.out.println(
          		  test.evaluate("67,385,71,72~4-0,260-0~5-0-0:0"));
          //System.out.println(test.evaluate());
          //System.out.println(test.evaluate());
          //System.out.println(test.evaluate());
          //System.out.println(test.evaluate());   
      }
      
}
