/**
 * 
 */
package com.taobao.hive.udf;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class RegexMapNew<V> extends HashMap<Pattern, V> {

  private static final long serialVersionUID = 4761214570434685507L;

  /** 委托到这个HashMap */
  protected HashMap<Pattern, V> map = null;

  // 构造函数
  // ----------------------------------------------------------------------

  public RegexMapNew() {
    super();
    this.map = new HashMap<Pattern, V>();
  }

  public RegexMapNew(int capacity) {
    super();
    this.map = new HashMap<Pattern, V>(capacity);
  }

  public RegexMapNew(int capacity, float factor) {
    super();
    this.map = new HashMap<Pattern, V>(capacity, factor);
  }

  public RegexMapNew(Map<? extends Pattern, ? extends V> map) {
    super();
    this.map = new HashMap<Pattern, V>(map);
  }

  //传入的字符串如果匹配上多个正则，则返回长度最长的那个正则表达式
  public V get(Object key) {
    if(key != null) {
      String tmp = "";
      for(Map.Entry<Pattern, V> entry : map.entrySet()) {
        if(entry.getKey().matcher(key.toString()).find()){
        	String nowValue = entry.getValue().toString();
        	if(nowValue.length()>tmp.length()){
        		tmp = nowValue;
        	}
        }
      }
      if(tmp.equals("")){
    	  return null;
      }
      return (V)tmp;
    }
    return null;
  }

  public int size() {
    return (map.size());
  }

  public boolean isEmpty() {
    return (map.isEmpty());
  }

  public boolean containsKey(Object key) {
    return (map.containsKey(key));
  }

  public boolean containsValue(Object value) {
    return (map.containsValue(value));
  }

  public V put(Pattern key, V value) {
    return (map.put(key, value));
  }

  public void putAll(Map<? extends Pattern, ? extends V> in) {
    map.putAll(in);
  }

  public V remove(Pattern key) {
    return (map.remove(key));
  }

  public void clear() {
    map.clear();
  }

  public Set<Map.Entry<Pattern, V>> entrySet() {
    return map.entrySet();
  }

  public Set<Pattern> keySet() {
    return map.keySet();
  }

  public Collection<V> values() {
    return map.values();
  }
  
  public static void main(String[] args){
	  RegexMapNew test = new RegexMapNew();
 
	  
	  Pattern  p = Pattern.compile("mall\\.taobao\\.com", Pattern.MULTILINE);
	  test.put(p, "mall\\.taobao\\.com"); 
	  
	  p = Pattern.compile("www\\.mall\\.taobao\\.com", Pattern.MULTILINE);
	  test.put(p, "www\\.mall\\.taobao\\.com");
	  
	  p = Pattern.compile("list\\.mall\\.taobao\\.com", Pattern.MULTILINE);
	  test.put(p, "list\\.mall\\.taobao\\.com");
	  System.out.println(test.get("http://list.mall.taobao.com/aaa.html"));
	  System.out.println(test.get("http://www.mall.taobao.com/aaa.html"));
	  System.out.println(test.get("http://mall.taobao.com/aaa.html"));
	  
	  
  }
  
  
  
  

}
