package org.sourceopen.hadoop.hive.resources.parser.node.helper;

import org.sourceopen.hadoop.hive.resources.parser.analyze.ast.HiveLexer;
import org.sourceopen.hadoop.hive.resources.parser.node.eva.ValNode.ValType;

/**
 * 类TypeInfoUtil.java的实现描述：TODO 类实现描述 
 * @author zalot.zhaoh Jul 31, 2012 5:13:04 PM
 */
public class TypeInfoUtil {

    public static ValType getValType(int astType) {
        if (astType == HiveLexer.Number) {
            return ValType.INTEGER;
        }
        return ValType.STRING;
    }
}
