package com.taobao.ad.data.search.udf;

import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.exec.UDFArgumentLengthException;
import org.apache.hadoop.hive.ql.exec.UDFArgumentTypeException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDF;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDFUtils;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorConverters;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector.Category;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.apache.hadoop.io.Text;import org.apache.hadoop.io.LongWritable;

@Description(name = "row_number", value = "_FUNC_(x) - Returns the rank of a set of collector")
public class UDFrow_number extends GenericUDF
{   
	ObjectInspectorConverters.Converter[] cvt ;
	private Text[] pre_args;
	private LongWritable pre_count = new LongWritable(0);
	private long pre_count_long = 1;
	private boolean thesamekey = false;
	private boolean total_firstrow = true;
	private Text txt = new Text();
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
		
		return PrimitiveObjectInspectorFactory.writableLongObjectInspector;
	}

	@Override
	public Object evaluate(DeferredObject[] arguments) throws HiveException
	{   
		//�ж�ǰ��keyֵ�Ƿ���仯
		if (total_firstrow==false){
		     for (int idx = 0; idx < arguments.length; idx++){
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
			pre_args = new Text[arguments.length];
		}
		//�ж�ǰ��keyֵ�Ƿ���仯 end
		//���õķ���ֵ
		 if (thesamekey){
			  pre_count_long +=1 ;
		  } else{
			  pre_count_long = 1 ;
			  for (int idx = 0; idx < arguments.length; idx++){
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
