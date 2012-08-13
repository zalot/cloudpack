package org.sourceopen.hadoop.hive.resources.parser.analyze.ast;

import java.util.List;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.Token;
import org.antlr.runtime.TokenRewriteStream;
import org.antlr.runtime.tree.CommonTreeAdaptor;
import org.antlr.runtime.tree.Tree;
import org.antlr.runtime.tree.TreeAdaptor;
import org.sourceopen.hadoop.hive.resources.parser.node.ast.ASTNode;

public class ASTParser {

    public static final TreeAdaptor adaptor = new CommonTreeAdaptor() {

                                                @Override
                                                public Object create(Token payload) {
                                                    return new ASTNode(payload);
                                                }
                                            };

    public static class ANTLRNoCaseStringStream extends ANTLRStringStream {

        public ANTLRNoCaseStringStream(String input){
            super(input);
        }

        @Override
        public int LA(int i) {

            int returnChar = super.LA(i);
            if (returnChar == CharStream.EOF) {
                return returnChar;
            } else if (returnChar == 0) {
                return returnChar;
            }

            return Character.toUpperCase((char) returnChar);
        }
    }

    public static ASTNode parser(String sr) throws RecognitionException {
        HiveLexer lexer = new HiveLexer(new ANTLRNoCaseStringStream(sr));
        TokenRewriteStream tokens = new TokenRewriteStream(lexer);
        HiveParser parser = new HiveParser(tokens);
        parser.setTreeAdaptor(adaptor);
        HiveParser.statement_return r = null;
        r = parser.statement();
        ASTNode tree = (ASTNode) r.getTree();

        while ((tree.getToken() == null) && (tree.getChildCount() > 0)) {
            tree = (ASTNode) tree.getChild(0);
        }

        return tree;
    }

    public static void printTree(Tree ast, int c) {
        String cp = "";
        for (int x = 0; x < c; x++)
            cp += "  ";
        System.out.println(cp + "|_" + ast + "[" + ast.getType() + "]");
        if (ast.getChildCount() > 0) {
            for (int x = 0; x < ast.getChildCount(); x++)
                printTree(ast.getChild(x), c + 1);
        }
    }

    public static Tree findWhereTag(Tree parent, Tree child, String ptCol, List<Tree> finds) {
        if (child.getText().equalsIgnoreCase(ptCol)) {
            if (parent != null && parent.getType() == HiveLexer.TOK_TABLE_OR_COL) return parent;
        }
        if (child.getChildCount() > 0) {
            for (int x = 0; x < child.getChildCount(); x++) {
                Tree t = ASTParser.findWhereTag(child, child.getChild(x), ptCol, finds);
                if (t != null) finds.add(parent);
            }
        }
        return null;
    }
}
