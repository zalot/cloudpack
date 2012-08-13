package com.taobao.hive.udtf;

import java.util.ArrayList;
import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.exec.UDFArgumentLengthException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDTF;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;

public class UDTFSerial extends GenericUDTF {
 
  Object[] result = new Object[1];
 
  @Override
  public void close() throws HiveException {
  }
 
  @Override
  public StructObjectInspector initialize(ObjectInspector[] args)
          throws UDFArgumentException {
      if (args.length != 1) {
          throw new UDFArgumentLengthException("UDTFSerial takes only one argument");
      }
      if (!args[0].getTypeName().equals("int")) {
       throw new UDFArgumentException("UDTFSerial only takes an integer as a parameter");
        }
      ArrayList<String> fieldNames = new ArrayList<String>();
      ArrayList<ObjectInspector> fieldOIs = new ArrayList<ObjectInspector>();
      fieldNames.add("col1");
      fieldOIs.add(PrimitiveObjectInspectorFactory.javaIntObjectInspector);
     
      return ObjectInspectorFactory.getStandardStructObjectInspector(fieldNames,fieldOIs);
  }
 
  @Override
  public void process(Object[] args) throws HiveException {
   try
   {
    int n = Integer.parseInt(args[0].toString());
    for (int i=0;i<n;i++)
    {
     result[0] = i+1;
     forward(result);
    }
   }
   catch (Exception e) { 
    throw new HiveException("UDTFSerial has an exception");
   }
  }
}