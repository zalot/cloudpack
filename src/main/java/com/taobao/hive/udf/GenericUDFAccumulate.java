//package com.taobao.hive.udf;
//
//import org.apache.hadoop.hive.ql.exec.Description;
//import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
//import org.apache.hadoop.hive.ql.exec.UDFArgumentLengthException;
//import org.apache.hadoop.hive.ql.exec.UDFArgumentTypeException;
//import org.apache.hadoop.hive.ql.metadata.HiveException;
//import org.apache.hadoop.hive.ql.udf.generic.GenericUDF;
//import org.apache.hadoop.hive.ql.udf.generic.GenericUDFUtils;
//import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
//import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorConverters;
//import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector.Category;
//import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
//import org.apache.hadoop.io.Text;
//
//@Description(name = "accumulate", value = "_FUNC_(x) - Returns accumulation of a set of col")
//public class GenericUDFAccumulate extends GenericUDF
//{   
//	ObjectInspectorConverters.Converter cvt ;
//	private Text sum = new Text("0");
//	private Text txt = new Text();
//	private double lastSum = 0;
//	@Override
//	public ObjectInspector initialize(ObjectInspector[] arguments)
//			throws UDFArgumentException
//	{
//		if (arguments.length != 1)
//		{
//			throw new UDFArgumentLengthException(
//					"The function accumulate(col1) only supports one argument.");
//		}
//		Category category = arguments[0].getCategory();
//		if (category != Category.PRIMITIVE)
//		{
//			throw new UDFArgumentTypeException(0, "The "
//					+ GenericUDFUtils.getOrdinal(0 + 1)
//					+ " argument of function ELT is expected to a "
//					+ Category.PRIMITIVE.toString().toLowerCase()
//					+ " type, but " + category.toString().toLowerCase()
//					+ " is found");
//		}
//		cvt = ObjectInspectorConverters.getConverter(arguments[0],
//		          PrimitiveObjectInspectorFactory.writableStringObjectInspector);
//		
//		return PrimitiveObjectInspectorFactory.writableStringObjectInspector;
//	}
//
//	@Override
//	public Object evaluate(DeferredObject[] arguments) throws HiveException
//	{   
//		txt=(Text) cvt.convert(arguments[0].get());
//		if (txt == null) return sum;
//		double inputValue = 0;
//		try {
//			inputValue = Double.parseDouble(txt.toString());
//		} catch (Exception e) {
//			//do nothing
//		}
//		lastSum += inputValue;
//		sum.set(lastSum + "");
//		return sum;
//		
//	}
//	
//	@Override
//	public String getDisplayString(String[] children)
//	{
//		assert (children.length != 1);
//		StringBuilder sb = new StringBuilder();
//		sb.append("accumulate(");
//		for (int i = 0; i < children.length - 1; i++)
//		{
//			sb.append(children[i]).append(", ");
//		}
//		sb.append(children[children.length - 1]).append(")");
//		return sb.toString();
//	}
//}
package com.taobao.hive.udf;

import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.exec.UDFArgumentLengthException;
import org.apache.hadoop.hive.ql.exec.UDFArgumentTypeException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDF;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDFUtils;
import org.apache.hadoop.hive.serde2.io.DoubleWritable;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorConverters;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector.Category;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.apache.hadoop.io.Text;

@Description(name = "row_number", value = "_FUNC_(x) - Returns the rank of a set of collector")
public class GenericUDFAccumulate extends GenericUDF
{   
	ObjectInspectorConverters.Converter[] cvt ;
	private Text[] pre_args;
	private DoubleWritable pre_count = new DoubleWritable(0);
	private double pre_count_long = 0;
	private boolean thesamekey = false;
	private boolean total_firstrow = true;
	private Text txt = new Text();
	private Text value = new Text();
	@Override
	public ObjectInspector initialize(ObjectInspector[] arguments)
			throws UDFArgumentException
	{
		if (arguments.length < 1)
		{
			throw new UDFArgumentLengthException(
					"The function row_number(str1,str2,str3,...) needs at least one arguments.");
		}
		for (int i = 0; i < arguments.length; i++)
		{
			Category category = arguments[i].getCategory();
			if (category != Category.PRIMITIVE)
			{
				throw new UDFArgumentTypeException(i, "The "
						+ GenericUDFUtils.getOrdinal(i + 1)
						+ " argument of function ELT is expected to a "
						+ Category.PRIMITIVE.toString().toLowerCase()
						+ " type, but " + category.toString().toLowerCase()
						+ " is found");
			}
		}
		cvt = new ObjectInspectorConverters.Converter[arguments.length];
		for (int i = 0; i < arguments.length; i++) {
			cvt[i] = ObjectInspectorConverters.getConverter(arguments[i],
		          PrimitiveObjectInspectorFactory.writableStringObjectInspector);
		    }
		
		return PrimitiveObjectInspectorFactory.writableDoubleObjectInspector;
	}

	@Override
	public Object evaluate(DeferredObject[] arguments) throws HiveException
	{   
		value=(Text) cvt[arguments.length-1].convert(arguments[arguments.length-1].get());
		//�ж�ǰ��keyֵ�Ƿ���仯
		if (total_firstrow==false){
		     for (int idx = 0; idx < arguments.length-1; idx++){
		         txt=(Text) cvt[idx].convert(arguments[idx].get());
			     if (txt ==null ){
			    	 if (pre_args[idx]== null){
			    		 thesamekey = true;
			    	 } else{
			    		 thesamekey = false;
			    		 break; 
			    	 } 
			     }else{
			    	 thesamekey=txt.equals(pre_args[idx]);
			    	  if (!thesamekey){
			    		  break;
			    	  }
			     }
		      }
		} else {
			total_firstrow = false;
			thesamekey = false; 
			pre_args = new Text[arguments.length-1];
		}
		//�ж�ǰ��keyֵ�Ƿ���仯 end
		//���õķ���ֵ
		 if (thesamekey){
			  pre_count_long += parseDoubleValue(value.toString());
		  } else{
			  pre_count_long = parseDoubleValue(value.toString());
			  for (int idx = 0; idx < arguments.length-1; idx++){
			       txt=(Text) cvt[idx].convert(arguments[idx].get());
				   if (txt == null ){
					   pre_args[idx] =  new Text("");
				   }else{
					   pre_args[idx] = new Text(txt.toString()); 
				   }
			  }
		  }
		 pre_count.set(pre_count_long);
		 return pre_count;

	}
	
	private double parseDoubleValue(String str) {
		try {
			return Double.parseDouble(str);
		} catch(Exception e) {
			return 0;
		}
	}
	
	@Override
	public String getDisplayString(String[] children)
	{
		assert (children.length >= 2);
		StringBuilder sb = new StringBuilder();
		sb.append("row_number(");
		for (int i = 0; i < children.length - 1; i++)
		{
			sb.append(children[i]).append(", ");
		}
		sb.append(children[children.length - 1]).append(")");
		return sb.toString();
	}
}
