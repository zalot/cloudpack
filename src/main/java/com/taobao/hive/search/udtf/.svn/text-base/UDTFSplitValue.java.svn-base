package com.taobao.hive.search.udtf;

import java.util.ArrayList;

import org.apache.hadoop.hive.ql.udf.generic.GenericUDTF;
import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.exec.UDFArgumentLengthException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;

public class UDTFSplitValue extends GenericUDTF {

	//默认分隔符为,
	private String splitchar = ",";

	@Override
	public StructObjectInspector initialize(ObjectInspector[] args)
			throws UDFArgumentException {

		if (args.length != 1 && args.length != 2) {
			throw new UDFArgumentLengthException(
					"UDTFSplitValue takes only one or two argument");
		}

		if (args[0].getCategory() != ObjectInspector.Category.PRIMITIVE) {
			throw new UDFArgumentException(
					"UDTFSplitValue takes string as a parameter");
		}

		ArrayList<String> fieldNames = new ArrayList<String>();
		ArrayList<ObjectInspector> fieldOIs = new ArrayList<ObjectInspector>();

		fieldNames.add("col");
		fieldOIs.add(PrimitiveObjectInspectorFactory.javaStringObjectInspector);

		return ObjectInspectorFactory.getStandardStructObjectInspector(
				fieldNames, fieldOIs);
	}

	/*
	 * 把每一行的字符串按照指定的分隔符切分，如果没有指定分隔符，
	 * 则默认按照逗号切分，然后返回多行，每行只有一列
	 */
	public void process(Object[] args) throws HiveException {

		try {
			// 如果传进来的参数不为null而且第一个参数合法，才进行下一步；否则，什么也不做。
			if (args != null && args[0] != null && "".equals(args[0].toString()) == false) {
				String input = args[0].toString();
				//如果有两个参数，第一个为要切分的字符串，第二个为分隔符
				if (args.length == 2) {
					//如果第二个参数为null或者"",则直接返回第一个参数
					if (args[1] == null || "".equals(args[1].toString()) == true) {
						String[] temp = { args[0].toString() };
						forward(temp);
						return;
					}
					//如果第二个参数合法，则按照指定的分隔符分割字符串
					else {
						splitchar = args[1].toString();
					}
				 } 
				 //按照分隔符切分字符串，然后返回。
				 String[] line = input.split(splitchar);
				 for (int i = 0; i < line.length; i++) {
					// 要先把字符串转化为字符串数组，再返回。
					String[] temp = { line[i] };
					forward(temp);
				  }
			   }
		  } catch (Exception e) {}
	}

	public void close() throws HiveException {
	}

	
	/*  public static void main(String[] args) throws HiveException { // TODO
	  UDTFSplitValue explode = new UDTFSplitValue();
	  Object[] test = new Object[2]; test[0] = ("null");
	  test[1] = ("u");
	   explode.process(test);
	  }*/
	 

}
