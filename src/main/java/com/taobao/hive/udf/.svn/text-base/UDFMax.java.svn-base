package com.taobao.hive.udf;

import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.io.Text;


/**
 *  
 * 以后不定参数的UDF函数都可以先继承YouLiangUDF    写法参考本类即可.
 * Text evaluate(DeferredObject[] arguments)  为接收到的参数.
 * for (int i = 0; i < arguments.length; i++){
 *      text=(Text) cvt[i].convert(arguments[i].get());
 *      以上为在循环中取得每一个参数的方式.text.toString() 就可以得到String类型的参数值了.
 * CREATE TEMPORARY FUNCTION udf_max  AS 'com.taobao.hive.udf.UDFMax';
 * select udf_max(3,5,7,9,44,22,333,4,5,6,7) from dual;  返回333.0
 * select udf_max('3','5','7','9','44','22','333','4','5a','6','7') from dual; 返回333.0
 * 
 * 
 * @author youliang   
 * 2010-12-18
 *  
 *     
 *     
 */
public class UDFMax extends YouLiangUDF
{   
	Text result = new Text();
	private Text text = new Text();
	
	@Override
	public Text evaluate(DeferredObject[] arguments) throws HiveException
	{   
		     try{
			     double yuansu = 0;
			     text=(Text) cvt[0].convert(arguments[0].get());
			     double max = Double.parseDouble(text.toString());
			     for (int i = 0; i < arguments.length; i++){//
			         text=(Text) cvt[i].convert(arguments[i].get());//获得参数值
					 try{
						  yuansu = Double.parseDouble(text.toString());
					 }catch(Exception e){
						  continue;
					 }

					 if(yuansu>max){
						  max = yuansu;
					 }
			     }
			     result.set(max+"");
			     return result;
		     }catch(Exception e){
			      result.set("err");
			      return result;
		     }

	}
}
