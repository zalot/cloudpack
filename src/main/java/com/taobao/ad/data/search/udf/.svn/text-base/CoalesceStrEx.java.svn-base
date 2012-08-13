package com.taobao.ad.data.search.udf;

import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.exec.UDFArgumentLengthException;
import org.apache.hadoop.hive.ql.exec.UDFArgumentTypeException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDF;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDFUtils;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorConverters;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector.Category;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorConverters.Converter;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorConverter;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.apache.hadoop.io.Text;

//实现取第一个不为Null，“”的串 
public class CoalesceStrEx extends GenericUDF
{
	//PrimitiveObjectInspectorConverter.TextConverter cvt ;
	Converter[] converters;
	@Override
	public ObjectInspector initialize(ObjectInspector[] arguments)
			throws UDFArgumentException
	{
		if (arguments.length < 2)
		{
			throw new UDFArgumentLengthException(
					"The function CoalesceStrEx(str1,str2,str3,...) needs at least two arguments.");
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

		//cvt = new PrimitiveObjectInspectorConverter.TextConverter(PrimitiveObjectInspectorFactory.writableStringObjectInspector) ;
		//return PrimitiveObjectInspectorFactory.writableStringObjectInspector;
		converters = new ObjectInspectorConverters.Converter[arguments.length];
		for (int i = 0; i < arguments.length; i++) {
			converters[i] = ObjectInspectorConverters.getConverter(
							arguments[i],
							PrimitiveObjectInspectorFactory.writableStringObjectInspector);
		}
		return PrimitiveObjectInspectorFactory.writableStringObjectInspector;
	}

	@Override
	public Object evaluate(DeferredObject[] arguments) throws HiveException
	{
		for (int idx = 0; idx < arguments.length; idx++)
		{
			//Text txt = (Text) cvt.convert(arguments[idx].get());
			Text txt = (Text) converters[idx].convert(arguments[idx].get());			
			if (txt != null && !txt.toString().equals("")) return txt;
		}

		return null;
	}

	@Override
	public String getDisplayString(String[] children)
	{
		assert (children.length >= 2);
		StringBuilder sb = new StringBuilder();
		sb.append("CoalesceStrEx(");
		for (int i = 0; i < children.length - 1; i++)
		{
			sb.append(children[i]).append(", ");
		}
		sb.append(children[children.length - 1]).append(")");
		return sb.toString();
	}
}
