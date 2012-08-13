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
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorUtils;
import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.io.LongWritable;

@Description(name = "UDFrow_rank", value = "_FUNC_(col1,key1,key2) - Returns the preceding sum of col1's value partition by key1,key2")
public class UDFrow_rank extends GenericUDF
{   
    private ObjectInspector[] inputOI;
	ObjectInspectorConverters.Converter cvt ;
	private LongWritable Lwresult = new LongWritable(1);
	private long Lcount = 1;
	private long Lcount1 = 1;
	private Object[] pre_keys;
	private boolean thesamekey = true;
	private boolean total_firstrow = true;
	@Override
	public ObjectInspector initialize(ObjectInspector[] arguments)
			throws UDFArgumentException
	{
		if (arguments.length < 1)
		{
			throw new UDFArgumentLengthException(
					"The function UDFcsum(col1,...) needs at least one arguments.");
		}
		inputOI = arguments;
		for (int i = 0; i < arguments.length; i++)
		{
			Category category = arguments[i].getCategory();
			if (category != Category.PRIMITIVE)
			{
				throw new UDFArgumentTypeException(i, "The "
						+ GenericUDFUtils.getOrdinal(i + 1)
						+ " argument of function row_rank is expected to a "
						+ Category.PRIMITIVE.toString().toLowerCase()
						+ " type, but " + category.toString().toLowerCase()
						+ " is found");
			}
		}
		
	    pre_keys = new Object[arguments.length];
		return PrimitiveObjectInspectorFactory.writableLongObjectInspector;
	}

	@Override
	public Object evaluate(DeferredObject[] arguments) throws HiveException
	{   
		//判断前后key值是否发生变化
		for (int idx = 1; idx < arguments.length; idx++){
			if (arguments[idx].get()==null){
				if(!(pre_keys[idx]==null)){
		    	    thesamekey=false;
				}
				pre_keys[idx]=null;
			}else{
			if (!PrimitiveObjectInspectorUtils.comparePrimitiveObjects(arguments[idx].get(),
			          (PrimitiveObjectInspector) inputOI[idx], pre_keys[idx],
			          (PrimitiveObjectInspector) inputOI[idx])){
				thesamekey=false;
				pre_keys[idx] = ((PrimitiveObjectInspector)inputOI[idx]).copyObject(arguments[idx].get());
			}
			}
		};
		if (!thesamekey||total_firstrow){
			total_firstrow=false;
			thesamekey = true;
			Lcount1=1;
			Lcount=Lcount1;
			pre_keys[0] = ((PrimitiveObjectInspector)inputOI[0]).copyObject(arguments[0].get());
		}else{  
			thesamekey = true;
			Lcount1=Lcount1+1;
			if (arguments[0].get()==null){
				if(!(pre_keys[0]==null)){
					Lcount=Lcount1;
				} 
				pre_keys[0]=null;
			}else{
			if (!PrimitiveObjectInspectorUtils.comparePrimitiveObjects(arguments[0].get(),
			          (PrimitiveObjectInspector) inputOI[0], pre_keys[0],
			          (PrimitiveObjectInspector) inputOI[0])){
				Lcount=Lcount1;
				pre_keys[0] = ((PrimitiveObjectInspector)inputOI[0]).copyObject(arguments[0].get());
			}
			}
		}
		Lwresult.set(Lcount);
	    return Lwresult;
	}
	@Override
	public String getDisplayString(String[] children)
	{
		assert (children.length >= 2);
		StringBuilder sb = new StringBuilder();
		sb.append("UDFrow_rank(");
		for (int i = 0; i < children.length - 1; i++)
		{
			sb.append(children[i]).append(", ");
		}
		sb.append(children[children.length - 1]).append(")");
		return sb.toString();
	}
}
