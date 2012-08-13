package org.sourceopen.hadoop.hive.resources.parser.node.eva;

import org.sourceopen.hadoop.hive.resources.parser.node.helper.TypeInfoUtil;

/**
 * 类DefaultValNode.java的实现描述：TODO 类实现描述 
 * @author zalot.zhaoh Jul 31, 2012 5:13:19 PM
 */
public class DefaultValNode extends ValNode {

    int    type;
    String text;

    public DefaultValNode(String text, int type){
        this.text = text;
        this.type = type;
    }

    @Override
    public String get() {
        return text.replaceAll("\'", "");
    }

    @Override
    public ValType getValType() {
        return TypeInfoUtil.getValType(type);
    }

}
