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


public class UDFgettaglist extends UDF{

	  Text result = new Text();
	  private static final String FILE1 = "./heart_stbTAG-0";
	  private static final String FILE2 = "./heart_stbTAG_GROUP-0";
      private Pattern pattern = Pattern.compile("-*[0-9]*(\\.)?[0-9]*");
	  Set<Integer> tag_regular = new HashSet<Integer>();
	  private boolean init_flag_tag = false;
	  private boolean init_flag_tag_group = false;
	  //private static Set<Integer> tag_group_filter = new HashSet<Integer>();
	  Map<Integer, Integer> m_group2tag_count = new HashMap< Integer, Integer >();
	  Map<Integer,HashSet<Integer>> m_tag2group_list = new HashMap<Integer,HashSet<Integer>>();	  	  
	  
	  public UDFgettaglist() {
		  
	  }
	  public Text evaluate(String user_data
			              ,String features
			              ,String category_level_one
			              ,String property
			              ,String spu_features
			              ,String cat_features
			              ,String category_tag_list
			              ,String status
			              ,String category
			              ,String category_level_two
			              ,String category_level_three
			              ,String category_level_four
			              ,String category_level_five)
	  {
         
		 if(property == null) {
			 //System.out.println("property is null");
			 property = "";
		 }
		 if(spu_features == null){
			 //System.out.println("spu_features is null");
			 spu_features ="";
		 }
		 if(category_level_one == null){
			 //System.out.println("category_level_one is null");
			 category_level_one = "";
		 }
		 if(category_level_two == null){
			 //System.out.println("category_level_two is null");
			 category_level_two = "";
		 }
		 if(category_level_three == null){
			 //System.out.println("category_level_three is null");
			 category_level_three = "";
		 }
		 if(category_level_four == null){
			 //System.out.println("category_level_four is null");
			 category_level_four = "";
		 }
		 if(category_level_five == null){
			 //System.out.println("category_level_five is null");
			 category_level_five = "";
		 }
		 
		 

		 if(user_data == null) {
			 //System.out.println("user_data is null");
			 user_data = "";
		 }
		 
		 if(features == null){
			 //System.out.println("features is null");
			 features = "";
		 }
		 if(cat_features == null){
			 //System.out.println("cat_features is null");
			 cat_features = "";
		 }
		 if(category_tag_list == null){
			 //System.out.println("category_tag_list is null");
			 category_tag_list = "";
		 }
		 if(status == null){
			 //System.out.println("status is null");
			 status = "";
		 }
		 
		 if(category == null){
			 //System.out.println("category is null");
			 category = "";
		 }
		 
		//property start
		 HashSet<Long> s_pvs = new HashSet<Long>();
		 
		 if(!property.toString().isEmpty()&&property!=null){
		   
	       String[] token1 = property.toString().split(";");
	       //String[] token1 = splitString(property,";");
	       for (int k = 0; k < token1.length; k++) {
	         if (token1[k].length() == 0)
	            continue;

             String[] token2 = token1[k].split(":");
	         //String[] token2 = splitString(token1[k],":");
	         if (token2.length == 2) {
	            int pid = Integer.parseInt(token2[0]);
	            int vid = Integer.parseInt(token2[1]);
	            //PID_VID pv = new PID_VID();
	            //pv.pid = pid;
	            //pv.vid = vid;

	            long pid_vid = getlong(vid, pid);

	            //prop_map.put(pid_vid, pv);	            
	            s_pvs.add(pid_vid);	            
	         }
	      }
		 }
	     //property end
		  
	     //spu features start 
	      HashSet<Integer> spu_tags_list = new HashSet<Integer>();
	      
		  if (!spu_features.toString().isEmpty() && spu_features.toString().contains("tags:"))
	          parseTaglists(spu_tags_list,spu_features.toString());
	       
	     //spu features end 
	      
		 //features start
		  
		  Map<Integer,HashSet<Integer>> m_catTagList = new HashMap<Integer,HashSet<Integer>>();
		  if (!category_level_one.toString().isEmpty()){
		  int cat_id = Integer.parseInt(category_level_one.toString());
	      String oneLine = cat_features.toString();
	      
	         if (oneLine.length() >0 && oneLine.contains("tags:")){
	                  HashSet<Integer> tag_list = new HashSet<Integer>();
	                  parseTaglists(tag_list,oneLine);
	                  m_catTagList.put(cat_id,tag_list);
	         }
		
		  }
		  //features end
		  
		  
		  //category_tag_list start	      
		  Map<Integer,HashSet<Integer>> m_catTagList2 = new HashMap<Integer,HashSet<Integer>>();
		  if (!category.toString().isEmpty()){
		  int cat_id2 = Integer.parseInt(category.toString());
	         if (!category_tag_list.toString().isEmpty()){
	                  HashSet<Integer> tag_list = new HashSet<Integer>();
	                  parseCaglists(tag_list,category_tag_list.toString());
	                  m_catTagList2.put(cat_id2,tag_list);
	         }
		
		  }
	     
	     //category_tag_list end
	      
		  HashSet<Integer> tag_result = new HashSet<Integer>();
	      get_valid_tag_lists(tag_result,user_data.toString(),features.toString(),
			        category_level_one.toString(),s_pvs,spu_tags_list,
			        m_catTagList,m_catTagList2,category_tag_list,status,category,
			        category_level_two,category_level_three,category_level_four,category_level_five);
	      
	      //tag start
	      if (!init_flag_tag) {
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
				init_flag_tag = true;
	      }
			//tag_group start
	      if (!init_flag_tag_group) {
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
			init_flag_tag_group = true;
	      }
	        //
			
			//
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

	  private void parseCaglists(HashSet<Integer> tag_list,String features) {
	         try {
	              String[] taglists = features.split(",");
	              for(int i=0;i<taglists.length;i++) {
	                  if(!"".equals(taglists[i].trim())&&taglists[i].trim().indexOf("-")<0)
	                  {
	                	  tag_list.add(Integer.parseInt(taglists[i].trim()));	                	  
	                  }
	                  else if (taglists[i].trim().indexOf("-")>0){
	                	  String[] datas = taglists[i].trim().split("-");
	                	  tag_list.add(Integer.parseInt(datas[0].trim()));
	                  }
	            	  
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

	  
	  public boolean isNumeric(String str){
	       if (str == null){
	          return false;
	       }
	       if (str.length() ==0){
	          return false;
	       }
	       
	       boolean regFlag =  pattern.matcher(str).matches();
	       return regFlag;
	    }
	  
	  public void get_valid_tag_lists(HashSet<Integer> tag_result,String user_data,String features,
		        String cat_id,HashSet<Long> s_pvs ,HashSet<Integer> s_sputaglist
		        ,Map<Integer,HashSet<Integer>> m_catTagList
		        ,Map<Integer,HashSet<Integer>> m_catTagList2,String category_tag_list,String status,String category,
		        String category_level_two ,String category_level_three,
		        String category_level_four,String category_level_five){
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
//	    						    if (m_catTagList.containsKey(Integer.parseInt(cat_id))) {  //g cat_id
//	    						        if (!m_catTagList.get(Integer.parseInt(cat_id)).contains(tag_id)) { //g cat_id
//	    						            continue;
//	    						        }
//	    						    }else {
//	    						        continue;
//	    						    }
	    						    if (tag_type == 7&&m_catTagList2.get(Integer.parseInt(category)).contains(tag_id)) {
	    						        tag_result.add(tag_id);	    						        
	    						    }
	    						    else {
	    						        if (s_sputaglist.contains(tag_id)&&m_catTagList2.get(Integer.parseInt(category)).contains(tag_id))
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
//										if (tag_infos[1].equals(cat_id)){ //g cat_id
//											tag_result.add(tag_id);
//										}
//										if(m_catTagList2.get(Integer.parseInt(category)).contains(tag_id))
//										{
//											tag_result.add(tag_id);
//										}		
										if(tag_infos[1].equals(cat_id)
										 ||tag_infos[1].equals(category_level_two)
										 ||tag_infos[1].equals(category_level_three)
										 ||tag_infos[1].equals(category_level_four)
										 ||tag_infos[1].equals(category_level_five))
										{
											tag_result.add(tag_id);
										}									
									}
								if (tag_type == 9) {
//									if (tag_infos[1].equals(cat_id)) { // g// cat_id
									if (m_catTagList2.get(Integer.parseInt(category)).contains(tag_id)){
									    
										String parseStatus = "";

										if (!category_tag_list.toString().isEmpty()) {
											String[] cat_list = category_tag_list.split(",");
											for (String a : cat_list) {
												if (a.indexOf("-") > 0) {
													String[] parses = a.split("-");
													if (tag_id == Integer.parseInt(parses[0])) {
														parseStatus = parses[1];
													}
												}
											}
											if (parseStatus.equals(status)||parseStatus.equals("a") ) {
												
												if(tag_infos[1].equals(cat_id)
												 ||tag_infos[1].equals(category_level_two)
												 ||tag_infos[1].equals(category_level_three)
												 ||tag_infos[1].equals(category_level_four)
												 ||tag_infos[1].equals(category_level_five))
												{
													tag_result.add(tag_id);
												}												    
											}
										}
									}
								}
									
								}
							}else if (2 == index){
								if (tag_infos.length ==3){
									if (tag_type == 5){
										//category
//										if (!tag_infos[1].equals(cat_id)){ //g cat_id
//											
//										}										
										if(!(tag_infos[1].equals(cat_id)
										 ||tag_infos[1].equals(category_level_two)
										 ||tag_infos[1].equals(category_level_three)
										 ||tag_infos[1].equals(category_level_four)
										 ||tag_infos[1].equals(category_level_five)
										 ))
										 {
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
    	  UDFgettaglist test = new UDFgettaglist();
//          System.out.println(
//          		  test.evaluate(
//          				        "1,3,7,8,67~137-2101,132-2101,4-2101~5-50017143-20000:10122"   //user_data
//          		               ,";tags:1346,2,3;sp:1;source:Sell;sellPromise:1;prop:1632501^1632501$货号=1053$1615~`;"   //features
//          		               ,""   //category_level_one
//          		               ,"20000:10122;1627207:28338;20561:28886;20561:28887;20561:28888;20561:28889;20561:28890;30590:92015;30589:92012;20563:28342;34321:130317;24477:20533;1632501:3265825;2396373:16559514;21723:29971"   //property
//          		               ,";tags:8;prop:2100010^2100010$货号=75744236$NY9183~`;"   //spu_features
//          		               ,";tags:8,200;*alimallEnable:1;*vm:16;*xcard:1;*cod:1;*h_new_second:new;*sku_price_limit:0.6,10;*pcs_new:1,32;h_auctionNum:0-250X300&251-2000X500&2001-10000X800;"   //cat_features
//          		               ,",137-a,200,456,7,8,4,5,132,"   //category_tag_list
//          		               ,"6"   //status
//          		               ,"50017234"
//          		               ,"50017143"
//          		               ,"2101"
//          		               ,"50017234"
//          		               ,""
//          		               )
//          		             );   
//          //System.out.println(test.evaluate());
          //System.out.println(test.evaluate());
          //System.out.println(test.evaluate());
          //System.out.println(test.evaluate());   
	      }
      
}
