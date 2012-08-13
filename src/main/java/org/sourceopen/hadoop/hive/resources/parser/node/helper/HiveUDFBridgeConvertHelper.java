package org.sourceopen.hadoop.hive.resources.parser.node.helper;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.serde2.io.DoubleWritable;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoFactory;
import org.apache.hadoop.io.BooleanWritable;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.sourceopen.hadoop.hive.resources.parser.DWFunctionRegister;
import org.sourceopen.hadoop.hive.resources.parser.node.eva.ValNode;

/**
 * 类HiveUDFBridgeConvertHelper.java的实现描述：TODO 类实现描述 
 * @author zalot.zhaoh Jul 31, 2012 5:12:58 PM
 */
public class HiveUDFBridgeConvertHelper {

    protected String functionName;
    protected Class  functionClass;

    public HiveUDFBridgeConvertHelper(String functionName){
        this.functionName = functionName;
        this.functionClass = DWFunctionRegister.getFunctionInfo(functionName).getFunctionClass();
        if (functionClass == null) {
            throw new RuntimeException("can no found function[" + functionName + "]");
        }
    }

    public Object invork(ValNode[] nodes) {
        List<TypeInfo> ty = new ArrayList<TypeInfo>();
        for (ValNode vn : nodes) {
            ty.add(TypeInfoFactory.getPrimitiveTypeInfo(vn.getValType().getName()));
        }

        Method targetMethod = null;
        try {
            targetMethod = DWFunctionRegister.getMethodInternal(functionClass, "evaluate", false, ty);
        } catch (UDFArgumentException e1) {
            throw new RuntimeException("can not found match function!");
        }
        Object[] params = null;
        Class[] czs = null;
        try {
            int idx = 0;
            czs = targetMethod.getParameterTypes();
            if (czs.length > 1) {
                params = new Object[czs.length];
            }
            for (ValNode vn : nodes) {
                Class cz = idx >= czs.length ? czs[czs.length - 1] : czs[idx];
                if (cz == String.class || cz.equals(new String[0].getClass())) {
                    if (params == null) params = new String[nodes.length];
                    params[idx] = new String(nodes[idx].get());
                } else if (cz == Integer.class || cz.equals(new Integer[0].getClass())) {
                    if (params == null) params = new Integer[nodes.length];
                    params[idx] = Integer.parseInt(nodes[idx].get());
                } else if (Long.class == cz || cz.equals(new Long[0].getClass())) {
                    if (params == null) params = new Long[nodes.length];
                    params[idx] = Long.parseLong(nodes[idx].get());
                } else if (Boolean.class == cz || cz.equals(new Boolean[0].getClass())) {
                    if (params == null) params = new Boolean[nodes.length];
                    params[idx] = Boolean.parseBoolean(nodes[idx].get());
                } else if (Double.class == cz || cz.equals(new Double[0].getClass())) {
                    if (params == null) params = new Double[nodes.length];
                    params[idx] = Double.parseDouble(nodes[idx].get());
                } else if (Float.class == cz || cz.equals(new Float[0].getClass())) {
                    if (params == null) params = new Float[nodes.length];
                    params[idx] = Float.parseFloat(nodes[idx].get());
                } else if (IntWritable.class == cz || cz.equals(new IntWritable[0].getClass())) {
                    if (params == null) params = new IntWritable[nodes.length];
                    params[idx] = new IntWritable(Integer.parseInt(nodes[idx].get()));
                } else if (LongWritable.class == cz || cz.equals(new LongWritable[0].getClass())) {
                    if (params == null) params = new LongWritable[nodes.length];
                    params[idx] = new LongWritable(Integer.parseInt(nodes[idx].get()));
                } else if (cz == Text.class || cz.equals(new Text[0].getClass())) {
                    if (params == null) params = new Text[nodes.length];
                    params[idx] = new Text(nodes[idx].get());
                } else if (BooleanWritable.class == cz || cz.equals(new BooleanWritable[0].getClass())) {
                    if (params == null) params = new BooleanWritable[nodes.length];
                    params[idx] = new BooleanWritable(Boolean.parseBoolean(nodes[idx].get()));
                } else if (DoubleWritable.class == cz || cz.equals(new DoubleWritable[0].getClass())) {
                    if (params == null) params = new DoubleWritable[nodes.length];
                    params[idx] = new DoubleWritable(Double.parseDouble(nodes[idx].get()));
                } else if (FloatWritable.class == cz || cz.equals(new FloatWritable[0].getClass())) {
                    if (params == null) params = new FloatWritable[nodes.length];
                    params[idx] = new FloatWritable(Float.parseFloat(nodes[idx].get()));
                }
                idx++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (czs.length == 1 && nodes.length > 1) {
                Object[] array = (Object[]) Array.newInstance(Object.class, 1);
                array[0] = params;
                return DWFunctionRegister.invoke(targetMethod, functionClass.newInstance(), array);
            } else if (czs.length == nodes.length && nodes.length > 1) {
                return DWFunctionRegister.invoke(targetMethod, functionClass.newInstance(), params);
            } else if (czs.length == 1 && nodes.length == 1) {
                return DWFunctionRegister.invoke(targetMethod, functionClass.newInstance(), params[0]);
            }
        } catch (HiveException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return "";
    }
}
