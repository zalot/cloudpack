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

public class UDTFExplodeKeyValue extends GenericUDTF{
	
	private String spliter1 = ";";
	private String spliter2 = ":";

	@Override
	public void close() throws HiveException {
		// TODO Auto-generated method stub	
	}

	@Override
	public StructObjectInspector initialize(ObjectInspector[] args)
			throws UDFArgumentException {
		if (args.length != 1 && args.length !=3) {
		    throw new UDFArgumentLengthException("UDTFExplodeKeyValue takes one or three argument(s)");
		}
		if (args[0].getCategory() != ObjectInspector.Category.PRIMITIVE) {
			throw new UDFArgumentException("UDTFExplodeKeyValue takes string as a parameter");
		}
		
		ArrayList<String> fieldNames = new ArrayList<String>();
	    ArrayList<ObjectInspector> fieldOIs = new ArrayList<ObjectInspector>();
	    fieldNames.add("col1");
	    fieldOIs.add(PrimitiveObjectInspectorFactory.javaStringObjectInspector);
	    fieldNames.add("col2");
	    fieldOIs.add(PrimitiveObjectInspectorFactory.javaStringObjectInspector);
	    
	    return ObjectInspectorFactory.getStandardStructObjectInspector(fieldNames,fieldOIs);
	}

	@Override
	public void process(Object[] args) throws HiveException {
		if(args==null || args[0] == null){
			forward(new Object[2]);
			return;
		}
		if (args.length == 3) {
			spliter1 = (args[1]==null || "".equals(args[1].toString()))?";":args[1].toString();
			spliter2 = (args[2]==null || "".equals(args[2].toString()))?":":args[2].toString();
		}
		String input = args[0].toString();
		String[] test = input.split(spliter1);
		for(int i=0; i<test.length; i++) {
			try {
				int idx = test[i].indexOf(spliter2);
				if (idx == -1) continue;
				String[] result = test[i].split(spliter2);
				if (result.length == 2) {
					forward(result);
				} else if (idx != 0) {
					String[] fResult = new String[2];
					fResult[0] = result[0];
					fResult[1] = "";
					forward(fResult);
				} else {
					String[] fResult = new String[2];
					fResult[0] = "";
					fResult[1] = result[0];
					forward(fResult);
				}
			} catch (Exception e) {
				continue;
			}
		}
	}
//	
//	public static void main(String[] args) {
//		String input = "key1:value1;key2:value2;";
//		String split1 = ";";
//		String split2 = ":";
//		String[] test = input.split(split1);
//		String[] test1 = test[1].split("-");
//		System.out.println(test.length);
//		System.out.println(test1.length);
//		String a="input>" + input + " spliter1>" + split1 + " spliter2>" + split2 + " test.length>" + test.length + " test[0]>" + test[0];
//		System.out.println(a);
//	}
}
