package com.taobao.hive.udf;

import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.exec.UDFArgumentLengthException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDF;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDFUtils;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDFUtils.ReturnObjectInspectorResolver;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorUtils;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.apache.hadoop.io.BooleanWritable;
/**
 *  
 *  
 * CREATE TEMPORARY FUNCTION ins  AS 'com.taobao.hive.udf.UDFIn';
 *  
 *  
 * 
 *  
 *  
 *     
 *     
 */



public class UDFIn extends GenericUDF {

  private ObjectInspector[] argumentOIs;
  BooleanWritable bw = new BooleanWritable();

  ReturnObjectInspectorResolver conversionHelper = null;
  ObjectInspector compareOI;

  @Override
  public ObjectInspector initialize(ObjectInspector[] arguments)
      throws UDFArgumentException {
    if (arguments.length < 2) {
      throw new UDFArgumentLengthException(
          "The function IN requires at least two arguments, got "
          + arguments.length);
    }
    argumentOIs = arguments;

    // We want to use the ReturnObjectInspectorResolver because otherwise
    // ObjectInspectorUtils.compare() will return != for two objects that have
    // different object inspectors, e.g. 238 and "238". The ROIR will help convert
    // both values to a common type so that they can be compared reasonably.
    conversionHelper = new GenericUDFUtils.ReturnObjectInspectorResolver(true);

    for (ObjectInspector oi : arguments) {
      if(!conversionHelper.update(oi)) {
        StringBuilder sb = new StringBuilder();
        sb.append("The arguments for IN should be the same type! Types are: {");
        sb.append(arguments[0].getTypeName());
        sb.append(" IN (");
        for(int i=1; i<arguments.length; i++) {
          if (i != 1) {
            sb.append(", ");
          }
          sb.append(arguments[i].getTypeName());
        }
        sb.append(")}");
        throw new UDFArgumentException(sb.toString());
      }
    }
    compareOI = conversionHelper.get();

    return PrimitiveObjectInspectorFactory.writableBooleanObjectInspector;
  }

  @Override
  public Object evaluate(DeferredObject[] arguments) throws HiveException {
    bw.set(false);

    if (arguments[0].get() == null) {
      return null;
    }

    for (int i=1; i<arguments.length; i++) {
      if(ObjectInspectorUtils.compare(
          conversionHelper.convertIfNecessary(
              arguments[0].get(), argumentOIs[0]), compareOI,
          conversionHelper.convertIfNecessary(
              arguments[i].get(), argumentOIs[i]), compareOI) == 0) {
        bw.set(true);
        return bw;
      }
    }
    // Nothing matched. See comment at top.
    for (int i=1; i<arguments.length; i++) {
      if(arguments[i].get() == null) {
        return null;
      }
    }
    return bw;
  }

  @Override
  public String getDisplayString(String[] children) {
    assert (children.length >= 2);
    StringBuilder sb = new StringBuilder();

    sb.append("(");
    sb.append(children[0]);
    sb.append(") ");
    sb.append("IN (");
    for(int i=1; i<children.length; i++) {
      sb.append(children[i]);
      if (i+1 != children.length) {
        sb.append(", ");
      }
    }
    sb.append(")");
    return sb.toString();
  }

}
