package org.sourceopen.hadoop.hive.resources.parser.node.eva;

import org.sourceopen.hadoop.hive.resources.parser.analyze.ast.HiveLexer;

/**
 * 类EvaNode.java的实现描述：TODO 类实现描述 
 * @author zalot.zhaoh Jul 31, 2012 5:13:15 PM
 */
public class EvaNode extends ValNode {

    ValNode left;
    ValNode right;
    int     evaType;

    public EvaNode(ValNode left, ValNode right, int evaType){
        this.left = left;
        this.right = right;
        this.evaType = evaType;
    }

    public String get() throws Exception {
        long lv = -1;
        String l = left.get().replaceAll("\'", "");
        String r = right.get().replaceAll("\'", "");;
        try {
            switch (evaType) {
                case HiveLexer.PLUS:
                    lv = Long.parseLong(l) + Long.parseLong(r);
                    break;
                case HiveLexer.MINUS:
                    lv = Long.parseLong(l) - Long.parseLong(r);
                    break;
                case HiveLexer.STAR:
                    lv = Long.parseLong(l) * Long.parseLong(r);
                    break;
                case HiveLexer.DIVIDE:
                    lv = Long.parseLong(l) / Long.parseLong(r);
                    break;
                case HiveLexer.MOD:
                    lv = Long.parseLong(l) % Long.parseLong(r);
                    break;
            }
            return String.valueOf(lv);
        } catch (NumberFormatException e) {
            return l + r;
        }
    }

    @Override
    public ValType getValType() {
        return ValType.INTEGER;
    }

}
