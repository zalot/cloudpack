package com.taobao.hive.udtf;

import java.util.ArrayList;

import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.exec.UDFArgumentLengthException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDTF;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;

public class UDTFExplodeArray extends GenericUDTF{

	@Override
	public StructObjectInspector initialize(ObjectInspector[] args)
			throws UDFArgumentException {
		// TODO Auto-generated method stub
		if (args.length != 1) {
		    throw new UDFArgumentLengthException("UDTFExplodeArray takes one argument.");
		}
		if (args[0].getCategory() != ObjectInspector.Category.PRIMITIVE) {
			throw new UDFArgumentException("UDTFExplodeArray takes string as a parameter");
		}
		
		ArrayList<String> fieldNames = new ArrayList<String>();
	    ArrayList<ObjectInspector> fieldOIs = new ArrayList<ObjectInspector>();
	    fieldNames.add("col1");
	    fieldOIs.add(PrimitiveObjectInspectorFactory.javaStringObjectInspector);
	    fieldNames.add("col2");
	    fieldOIs.add(PrimitiveObjectInspectorFactory.javaStringObjectInspector);
	    fieldNames.add("col3");
	    fieldOIs.add(PrimitiveObjectInspectorFactory.javaStringObjectInspector);
	    
	    return ObjectInspectorFactory.getStandardStructObjectInspector(fieldNames,fieldOIs);
	}

	@Override
	public void process(Object[] args) throws HiveException {
		// TODO Auto-generated method stub
		if(args==null || args.length != 1){
			return;
		}
		String src = args[0].toString();
		String[] arr = src.split(",");
		if (arr.length>=3) {
			for(int i=0; i+2<arr.length;i++) {
				String[] result = new String[3];
				result[0] = arr[i];
				result[1] = arr[i+1];
				result[2] = arr[i+2];
				forward(result);
			}
		}
	}

	@Override
	public void close() throws HiveException {
		// TODO Auto-generated method stub
		
	}

}
