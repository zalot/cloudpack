package com.taobao.hive.udf;

import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.UDFType;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDF;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorConverters;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;


@UDFType(deterministic = true)
public abstract class YouLiangUDF extends GenericUDF{
	ObjectInspectorConverters.Converter[] cvt ;
	
	public YouLiangUDF() { 
		  
	}

	public ObjectInspector initialize(ObjectInspector[] arguments) throws UDFArgumentException
	{
		cvt = new ObjectInspectorConverters.Converter[arguments.length];
		for (int i = 0; i < arguments.length; i++) {
			cvt[i] = ObjectInspectorConverters.getConverter(arguments[i],
		          PrimitiveObjectInspectorFactory.writableStringObjectInspector);
		    }
		
		return PrimitiveObjectInspectorFactory.writableStringObjectInspector;
	}

    public abstract Object evaluate(DeferredObject[] arguments) throws HiveException;
    

	
	
	@Override
	public String getDisplayString(String[] children)
	{
		return "";
	}
}
