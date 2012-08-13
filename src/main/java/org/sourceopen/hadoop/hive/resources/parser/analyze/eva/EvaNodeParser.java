package org.sourceopen.hadoop.hive.resources.parser.analyze.eva;

import java.util.ArrayList;
import java.util.List;

import org.antlr.runtime.tree.Tree;
import org.sourceopen.hadoop.hive.resources.parser.analyze.ast.ASTParser;
import org.sourceopen.hadoop.hive.resources.parser.analyze.ast.HiveLexer;
import org.sourceopen.hadoop.hive.resources.parser.node.eva.DefaultValNode;
import org.sourceopen.hadoop.hive.resources.parser.node.eva.EvaNode;
import org.sourceopen.hadoop.hive.resources.parser.node.eva.FunctionEvaNode;
import org.sourceopen.hadoop.hive.resources.parser.node.eva.ValNode;

/**
 * 运行 main 类HiveMain.java的实现描述：TODO 类实现描述
 * 
 * @author zalot.zhaoh Jun 27, 2012 5:40:18 PM
 */
public class EvaNodeParser {

    public static ValNode[] findWhereTag(Tree tree, String ptCol) {
        List<Tree> foundChildTree = new ArrayList<Tree>();
        List<ValNode> vns = new ArrayList<ValNode>();
        ASTParser.findWhereTag(null, tree, ptCol, foundChildTree);
        for (Tree childTree : foundChildTree) {
            ValNode vn;
            try {
                vn = toValNode(childTree.getChild(1));
                if (vn != null) {
                    vns.add(vn);
                }
            } catch (Exception e) {
            }
        }
        return vns.toArray(new ValNode[0]);
    }

    protected static ValNode toValNode(Tree node) throws InstantiationException, IllegalAccessException {
        ValNode vn = null;
        switch (node.getType()) {
            case HiveLexer.PLUS:
            case HiveLexer.MINUS:
            case HiveLexer.STAR:
            case HiveLexer.DIVIDE:
            case HiveLexer.MOD:
                if (node.getChildCount() == 2) vn = new EvaNode(toValNode(node.getChild(0)),
                                                                toValNode(node.getChild(1)), node.getType());
                else if (node.getChildCount() == 1) vn = new EvaNode(new DefaultValNode("0", node.getType()),
                                                                     toValNode(node.getChild(0)), node.getType());
                break;
            case HiveLexer.TOK_FUNCTION:
                ValNode[] vns = new ValNode[node.getChildCount() - 1];
                for (int x = 1; x < node.getChildCount(); x++) {
                    vns[x - 1] = toValNode(node.getChild(x));
                }
                vn = new FunctionEvaNode(node.getChild(0).getText(), vns);
                break;
            default:
                vn = new DefaultValNode(node.getText(), node.getType());
                ;
        }
        return vn;
    }

}
