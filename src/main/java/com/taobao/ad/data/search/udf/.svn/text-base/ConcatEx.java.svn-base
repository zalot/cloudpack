package com.taobao.ad.data.search.udf;

import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.exec.UDFArgumentLengthException;
import org.apache.hadoop.hive.ql.exec.UDFArgumentTypeException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDF;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDFUtils;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector.Category;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorConverter;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.apache.hadoop.io.Text;

//实现多个字符串连接的操作 
public class ConcatEx extends GenericUDF
{
	PrimitiveObjectInspectorConverter.TextConverter cvt ;
	
	@Override
	public ObjectInspector initialize(ObjectInspector[] arguments)
			throws UDFArgumentException
	{
		if (arguments.length < 2)
		{
			throw new UDFArgumentLengthException(
					"The function ConcatEx(str1,str2,str3,...) needs at least two arguments.");
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

		cvt = new PrimitiveObjectInspectorConverter.TextConverter(PrimitiveObjectInspectorFactory.writableStringObjectInspector) ;
		return PrimitiveObjectInspectorFactory.writableStringObjectInspector;
	}

	@Override
	public Object evaluate(DeferredObject[] arguments) throws HiveException
	{
		StringBuilder sb = new StringBuilder();
		for (int idx = 0; idx < arguments.length; idx++)
		{
			//Text txt = (Text) converters[idx].convert(arguments[idx].get());
			Text txt = (Text) cvt.convert(arguments[idx].get());
			if (txt != null) sb.append(txt.toString());
		}

		return new Text(sb.toString());
	}

	@Override
	public String getDisplayString(String[] children)
	{
		assert (children.length >= 2);
		StringBuilder sb = new StringBuilder();
		sb.append("ConcatEx(");
		for (int i = 0; i < children.length - 1; i++)
		{
			sb.append(children[i]).append(", ");
		}
		sb.append(children[children.length - 1]).append(")");
		return sb.toString();
	}
}
