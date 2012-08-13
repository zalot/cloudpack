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
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorUtils;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.hive.serde2.io.DoubleWritable;



@Description(name = "UDFcsum", value = "_FUNC_(col1,key1,key2) - Returns the preceding sum of col1's value partition by key1,key2")
public class UDFcsum extends GenericUDF
{   
    private ObjectInspector[] inputOI;
	ObjectInspectorConverters.Converter cvt ;
	//ObjectInspectorConverters.Converter cvt1 ; 
	private LongWritable Lwresult = new LongWritable(0);
	private DoubleWritable Dwresult = new DoubleWritable(0.00);
	private Object[] pre_keys;
	private boolean thesamekey = true;
	private boolean total_firstrow = true;
	private GenericUDFUtils.ReturnObjectInspectorResolver returnOIResolver;
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
						+ " argument of function ELT is expected to a "
						+ Category.PRIMITIVE.toString().toLowerCase()
						+ " type, but " + category.toString().toLowerCase()
						+ " is found");
			}
		}
		switch (((PrimitiveObjectInspector) inputOI[0]).getPrimitiveCategory()) {
		case BYTE:
			 break;
	    case SHORT:
	    	 break;
	    case INT:
	    	 break;
	    case LONG:
	    	 break;
	    case DOUBLE:
	    	 break;
	    default:
	      throw new UDFArgumentTypeException(0,
	          "Only numeric type arguments are accepted but "
	          + inputOI[0].getTypeName() + " is passed.");
	    }
	    
	    pre_keys = new Object[arguments.length-1];
		returnOIResolver = new GenericUDFUtils.ReturnObjectInspectorResolver();

		if (!returnOIResolver.update(arguments[0])) {
	        throw new UDFArgumentTypeException(0,
	            "The expressions after csum should all have the same type: \""
	            + returnOIResolver.get().getTypeName()
	            + "\" is expected but \"" + arguments[0].getTypeName()
	            + "\" is found");
	      }
		
		switch (((PrimitiveObjectInspector) inputOI[0]).getPrimitiveCategory()) {
		case BYTE:
	    case SHORT:
	    case INT:
	    case LONG:
	    	cvt = ObjectInspectorConverters.getConverter(arguments[0],
			          PrimitiveObjectInspectorFactory.writableLongObjectInspector);
	    case DOUBLE:
	   }
		return returnOIResolver.get();
	}

	@Override
	public Object evaluate(DeferredObject[] arguments) throws HiveException
	{   
		//判断前后key值是否发生变化
		for (int idx = 1; idx < arguments.length; idx++){
			if (arguments[idx].get()==null){
				if(!(pre_keys[idx-1]==null)){
		    	    thesamekey=false;
				}
				pre_keys[idx-1]=null;
			}else{
			if (!PrimitiveObjectInspectorUtils.comparePrimitiveObjects(arguments[idx].get(),
			          (PrimitiveObjectInspector) inputOI[idx], pre_keys[idx-1],
			          (PrimitiveObjectInspector) inputOI[idx])){
				thesamekey=false;
				pre_keys[idx-1] = ((PrimitiveObjectInspector)inputOI[idx]).copyObject(arguments[idx].get());
			}
			}
		};
		if (!thesamekey||total_firstrow){
			total_firstrow=false;
			thesamekey = true;
			switch (((PrimitiveObjectInspector) inputOI[0]).getPrimitiveCategory()) {
			case BYTE:
		    case SHORT:
		    case INT:
		    case LONG:
		    	 if(!(arguments[0].get()==null))
		    	     Lwresult.set(((LongWritable)cvt.convert(arguments[0].get())).get());
		    	 return Lwresult;
		    case DOUBLE:
		    	 if(!(arguments[0].get()==null))
		    	 Dwresult.set(((DoubleWritable)(arguments[0].get())).get());
		    	 return Dwresult;
			}
		}else{  
			    thesamekey = true;
				switch (((PrimitiveObjectInspector) inputOI[0]).getPrimitiveCategory()) {
				case BYTE:
			    case SHORT:
			    case INT:
			    case LONG:
			    	if(!(arguments[0].get()==null))
			    	    Lwresult.set(((LongWritable)cvt.convert(arguments[0].get())).get()+Lwresult.get());
			    	 return Lwresult;
			    case DOUBLE:
			    	if(!(arguments[0].get()==null))
			    	Dwresult.set(((DoubleWritable)(arguments[0].get())).get()+Dwresult.get());
			    	return Dwresult;
		}
		}
		return null;
	}
	@Override
	public String getDisplayString(String[] children)
	{
		assert (children.length >= 2);
		StringBuilder sb = new StringBuilder();
		sb.append("UDFcsum(");
		for (int i = 0; i < children.length - 1; i++)
		{
			sb.append(children[i]).append(", ");
		}
		sb.append(children[children.length - 1]).append(")");
		return sb.toString();
	}
}
