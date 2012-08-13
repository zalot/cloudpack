package com.taobao.hive.udtf;

import java.util.ArrayList;

import org.apache.hadoop.hive.ql.udf.generic.GenericUDTF;
import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.exec.UDFArgumentLengthException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;

/*
ExplodeEX:  Explode and rember their orginial order. 
*/
public class UDTFExplodeEX extends GenericUDTF {

	private String splitter = ",";

	@Override
	public void close() throws HiveException {
		// TODO Auto-generated method stub
	}

	@Override
	public StructObjectInspector initialize(ObjectInspector[] args)
			throws UDFArgumentException {
		if (args.length != 1 && args.length != 2) {
			throw new UDFArgumentLengthException(
					"UDTFExplodeEX takes one or two argument(s): ExplodeEX(str) or ExplodeEX(str,splitter)");
		}
		if (args[0].getCategory() != ObjectInspector.Category.PRIMITIVE) {
			throw new UDFArgumentException(
					"UDTFExplodeEX takes string as a parameter");
		}

		ArrayList<String> fieldNames = new ArrayList<String>();
		ArrayList<ObjectInspector> fieldOIs = new ArrayList<ObjectInspector>();
		fieldNames.add("col1");
		fieldOIs.add(PrimitiveObjectInspectorFactory.javaStringObjectInspector);
		fieldNames.add("col2");
		fieldOIs.add(PrimitiveObjectInspectorFactory.javaStringObjectInspector);

		return ObjectInspectorFactory.getStandardStructObjectInspector(
				fieldNames, fieldOIs);
	}

	@Override
	public void process(Object[] args) throws HiveException {
		if (args == null || args[0] == null) {
			forward(new Object[2]);
			return;
		} else if (args.length >= 1 && args.length <= 2) {
			splitter = (args[1] == null || "".equals(args[1].toString())) ? ","
					: args[1].toString();
		}
		String input = args[0].toString();
		String[] test = input.split(splitter);
		for (int i = 0; i < test.length; i++) {
			try {
				String[] result = { test[i], String.valueOf(i) };
				forward(result);
			} catch (Exception e) {
				continue;
			}
		}
	}
	
//	 public static void main(String[] args) {
//	 String input = "-5519603014150280553_5519603014150280551_1|-14216336145_14216336140_0|-14444380033_14444380030_0";
//	 String split1 = "|";
//	 String[] test = input.split(split1);
//	 System.out.println(test.length);
//	 }
}
