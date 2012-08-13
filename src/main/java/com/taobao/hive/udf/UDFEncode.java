package com.taobao.hive.udf;

import java.io.UnsupportedEncodingException;

import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.exec.UDFArgumentLengthException;
import org.apache.hadoop.hive.ql.exec.UDFArgumentTypeException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDF;
import org.apache.hadoop.hive.serde.Constants;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.StringObjectInspector;
import org.apache.hadoop.io.Text;

public class UDFEncode extends GenericUDF {

  private ObjectInspector[] argumentOIs;
  private String charset;

  @Override
  public Object evaluate(DeferredObject[] arguments) throws HiveException {

    if (arguments[0].get() == null) {
      return null;
    }

    Text src = ((StringObjectInspector) argumentOIs[0])
        .getPrimitiveWritableObject(arguments[0].get());

    if (arguments.length == 2) {
      charset = ((StringObjectInspector) argumentOIs[1])
          .getPrimitiveJavaObject(arguments[1].get());
    }

    try {
      String res = new String(src.getBytes(), 0, src.getLength(), charset);
      return new Text(res);
    } catch (UnsupportedEncodingException e) {
      return null;
    }
  }

  @Override
  public String getDisplayString(String[] children) {
    assert (children.length >= 1);
    StringBuilder sb = new StringBuilder();
    sb.append("encode(");
    for (int i = 0; i < children.length - 1; i++) {
      sb.append(children[i]).append(", ");
    }
    sb.append(children[children.length - 1]).append(")");
    return sb.toString();
  }

  @Override
  public ObjectInspector initialize(ObjectInspector[] arguments)
      throws UDFArgumentException {
    if (arguments.length > 2 || arguments.length < 1) {
      throw new UDFArgumentLengthException(
          "The function encode(str, charset) needs one or two arguments.");
    }

    for (int i = 0; i < arguments.length; i++) {
      if (arguments[i].getTypeName() != Constants.STRING_TYPE_NAME
          && arguments[i].getTypeName() != Constants.VOID_TYPE_NAME) {
        throw new UDFArgumentTypeException(i, "Argument " + (i + 1)
            + " of function encode must be \"" + Constants.STRING_TYPE_NAME
            + "\", but \"" + arguments[i].getTypeName() + "\" was found.");
      }
    }

    argumentOIs = arguments;
    if (argumentOIs.length == 1) {
      charset = "GBK";
    }
    return PrimitiveObjectInspectorFactory.writableStringObjectInspector;
  }
}
