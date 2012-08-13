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
                "select te,abc from abc where pt=concat('a',lower(9),concat('de',3 + 2))+'def'+12 or pt=123 or pt=234 or pt=12.3",
                "select def from abc where pt=concat(1,2) and pt=sysdate('yyyy-DD-mm')",
                "select def from abc where pt=concat(sysdate('yyyy-DD-mm'),'-|-',123+234,'-|-',sqrt(2))", };
        for (String sr : srs) {
            ASTNode ast = ASTParser.parser(sr);
            for (ValNode vn : EvaNodeParser.findWhereTag(ast, "pt")) {
                System.out.println("pt=" + vn.get());
            }
        }
    }
}
