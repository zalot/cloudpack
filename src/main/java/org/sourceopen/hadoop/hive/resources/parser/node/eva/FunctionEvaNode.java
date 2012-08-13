package org.sourceopen.hadoop.hive.resources.parser.node.eva;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.sourceopen.hadoop.hive.resources.parser.node.helper.HiveUDFBridgeConvertHelper;

/**
 * 类FunctionEvaNode.java的实现描述：TODO 类实现描述 
 * @author zalot.zhaoh Jul 31, 2012 5:13:08 PM
 */
public class FunctionEvaNode extends ValNode {

    String                     functionName;
    ValNode[]                  params;
    HiveUDFBridgeConvertHelper helper;
    Object                     obj;
    Method                     method;

    public FunctionEvaNode(String functionName, ValNode[] params) throws InstantiationException, IllegalAccessException{
        this.functionName = functionName;
        this.params = params;
        helper = new HiveUDFBridgeConvertHelper(functionName);
    }

    @Override
    public String get() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        return helper.invork(params).toString().replaceAll("\'", "");
    }

    @Override
    public ValType getValType() {
        return ValType.STRING;
    }
}
