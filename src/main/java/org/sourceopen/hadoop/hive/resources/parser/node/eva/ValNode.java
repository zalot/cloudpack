package org.sourceopen.hadoop.hive.resources.parser.node.eva;


/**
 * 类ValNode.java的实现描述：TODO 类实现描述 
 * @author zalot.zhaoh Jul 31, 2012 5:13:11 PM
 */
public abstract class ValNode {

    public static enum ValType {
        STRING("string"), INTEGER("int");

        String name;

        ValType(String name){
            this.name = name;
        }
        public String getName(){
            return this.name;
        }
    }

    public abstract String get() throws Exception;

    public abstract ValType getValType();
}
