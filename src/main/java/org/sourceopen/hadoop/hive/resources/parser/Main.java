package org.sourceopen.hadoop.hive.resources.parser;

import org.sourceopen.hadoop.hive.resources.parser.analyze.ast.ASTParser;
import org.sourceopen.hadoop.hive.resources.parser.analyze.eva.EvaNodeParser;
import org.sourceopen.hadoop.hive.resources.parser.node.ast.ASTNode;
import org.sourceopen.hadoop.hive.resources.parser.node.eva.ValNode;

public class Main {

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        EvaNodeParser main = new EvaNodeParser();
        String[] srs = new String[] {
                "select te,abc from abc where pt=dateCompare(\"2012-12-13\",\"20120-12-13\",0)"};
        for (String sr : srs) {
            ASTNode ast = ASTParser.parser(sr);
            for (ValNode vn : EvaNodeParser.findWhereTag(ast, "pt")) {
                System.out.println("pt=" + vn.get());
            }
        }
    }
}
