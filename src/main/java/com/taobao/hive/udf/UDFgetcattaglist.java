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


public class UDFgetcattaglist extends UDF{

	  Text result = new Text();
	  private static final String FILE1 = "./heart_stbTAG-0";
	  private static final String FILE2 = "./heart_stbTAG_GROUP-0";
      private static final String File0 = "./dwdbCAT_TAG-0";
	  Set<Integer> tag_regular = new HashSet<Integer>();
	  //private static Set<Integer> tag_group_filter = new HashSet<Integer>();
	  Map<Integer, Integer> m_group2tag_count = new HashMap< Integer, Integer >();
	  Map<Integer,HashSet<Integer>> m_tag2group_list = new HashMap<Integer,HashSet<Integer>>();	  	  
	  
	  public UDFgetcattaglist() {
		  
	  }
	  public Text evaluate(String category_level_one,String cat_features)
	  {
         
		 if(category_level_one == null){
			 //System.out.println("category_level_one is null");
			 category_level_one = "";
		 }

		 if(cat_features == null){
			 //System.out.println("cat_features is null");
			 cat_features = "";
		 }
		  
		 //处理类目表features 开始
		  
		  Map<Integer,HashSet<Integer>> m_catTagList = new HashMap<Integer,HashSet<Integer>>();
		  HashSet<Integer> tag_result = new HashSet<Integer>();
		  int cat_id = Integer.parseInt(category_level_one.toString());
		  if (!category_level_one.toString().isEmpty()){
		  
	      String oneLine = cat_features.toString();
	      
	         if (oneLine.length() >0 && oneLine.contains("tags:")){	        	      
	        	      //HashSet<Integer> tag_list = new HashSet<Integer>();
	                  parseTaglists(tag_result,oneLine);
	                  m_catTagList.put(cat_id,tag_result);
	         }
		 //处理类目表features 结束
		  }
	      //HashSet<Integer> tag_result = new HashSet<Integer>();
	      
	      //get_valid_tag_lists(tag_result,user_data.toString(),features.toString(),
		  //	        category_level_one.toString(),s_pvs,spu_tags_list,m_catTagList);
	      
	      //System.out.println("test0");
	      //System.out.println(tag_result);
	      //System.out.println("test1");
	      BufferedReader reader0 = null;
	      String line0 = null;
	      String[] strList0 = null;
	      
	      try {
		         reader0 = new BufferedReader(new InputStreamReader(new FileInputStream(File0)));
		         while (((line0 = reader0.readLine()) != null)) {
		        	   
					   strList0  = line0.split("\t");
					   if (isNumeric(strList0[0].toString())&&isNumeric(strList0[1].toString())){
						       int tagid = Integer.parseInt(strList0[0].toString());                       
						      //int isFilter = Integer.parseInt(strList1[10].toString());					   
						       if (!tag_result.contains(tagid)&& cat_id == Integer.parseInt(strList0[1])){
						           tag_result.add(tagid);
						       }
						   //if (isFilter != 1){
						   //    tag_group_filter.add(tagid);
						   //}					   
					   }
		         }//end while
		        } catch (FileNotFoundException e) {
					System.err.println("file0: not exist");
					e.printStackTrace();
				} catch (IOException e) {
					System.err.println(": read line0 error");
					e.printStackTrace();
				} catch(NumberFormatException e){
					System.err.println(strList0[0] + " parse string1 to integer error.");
					e.printStackTrace();
				}		  	      
		  System.out.println(tag_result);      
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
			System.out.println(tag_field_show);
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
	  
	  public void get_valid_tag_lists(HashSet<Integer> tag_result,String user_data,String features,
		        String cat_id,HashSet<Long> s_pvs ,HashSet<Integer> s_sputaglist
		        ,Map<Integer,HashSet<Integer>> m_catTagList){
			try{
				String[] user_datas = user_data.split("~");
				String[] feature_tags = get_tags(features).split(",");
				//category_level_one
				//s_pvs
				Set<Integer> stags = new HashSet<Integer>();
				
				if (feature_tags.length >0){
					for (String f_tag : feature_tags){
						if (isNumeric(f_tag)){
							int tag = Integer.parseInt(f_tag);
							stags.add(tag);
							if (get_tag_type(tag) == 2){
								tag_result.add(tag);
							}
						}
					}
				}
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
							if (tag_type == 1){
								tag_result.add(tag_id);
							}else if (tag_type == 3){
								if(stags.contains(tag_id)){
									tag_result.add(tag_id);
								}
							}else {
							    //add by tianfeng for sprint9 2010-11-22
							    if (tag_type == 7 || tag_type == 8) {
	    						    if (m_catTagList.containsKey(Integer.parseInt(cat_id))) {
	    						        if (!m_catTagList.get(Integer.parseInt(cat_id)).contains(tag_id)) {
	    						            continue;
	    						        }
	    						    }else {
	    						        continue;
	    						    }
	    						    if (tag_type == 7) {
	    						        tag_result.add(tag_id);
	    						    }
	    						    else {
	    						        if (s_sputaglist.contains(tag_id))
	    						            tag_result.add(tag_id);
	    						    }
							    }
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
										if (tag_infos[1].equals(cat_id)){
											tag_result.add(tag_id);
										}
									}
								}
							}else if (2 == index){
								if (tag_infos.length ==3){
									if (tag_type == 5){
										//category
										if (!tag_infos[1].equals(cat_id)){
											continue;
										}
										
										String[] pidvids = tag_infos[2].split(";");
										if (pidvids.length >3){
											continue;
										}
										boolean pv_check = true;
										for (String pv :pidvids){
											String[] item = pv.split(":");
											if (item.length != 2){
												pv_check = false;
												break;
											}
											long l_pv = getlong(Integer.parseInt(item[1]),Integer.parseInt(item[0]));
											if (!s_pvs.contains(l_pv)){
												pv_check = false;
												break;
											}
										}
										if (true == pv_check){
											tag_result.add(tag_id);
										}
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
    	    UDFgetcattaglist test = new UDFgetcattaglist();
            System.out.println(
          		  test.evaluate("50010388",";*tags:72;*alimallEnable:1;*vm:3;*xcard:1;*h_assCatLimit:1;*site_3c_lady:化妆品;charityChannel:household;*b_point_lmt:1;*cod:1;*enable_tabbed_spu_module:rate,comment,price;*pcs_new_old:1,4,32;*pcs_new:1,4,16,32;h_auctionNum:0-3X800&4-250X800&251-10000X800&10001-100000000X800;*h_videoshow:1;"));
          //		               ,null
          //		               ,null
          //		               ,null
          //		               ,null
          //		               ,null
          //		               )
          //		            );   
          //System.out.println(test.evaluate());
          //System.out.println(test.evaluate());
          //System.out.println(test.evaluate());
          //System.out.println(test.evaluate());   
	      }
      
}
