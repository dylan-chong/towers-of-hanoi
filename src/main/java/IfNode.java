import java.util.Scanner;

/**
 * Created by Dylan on 18/05/17.
 *
 * Includes Else and Elif nodes
 */
public class IfNode extends ConditionBlockNode {
    private static final String IF_KEYWORD = "if";

    private ElseNode elseNode; // optional

    public IfNode() {
        super(IF_KEYWORD, true);
    }

    @Override
    protected void privateDoParse(Scanner scanner, Logger logger) {
        super.privateDoParse(scanner, logger);
        if (scanner.hasNext(ElseNode.ELSE_KEYWORD)) {
            elseNode = new ElseNode();
            elseNode.parse(scanner, logger);
        }
    }

    @Override
    protected void privateExecute(Robot robot,
                                  ConditionNode conditionNode,
                                  BlockNode blockNode) {
        if (conditionNode.execute(robot)) {
            blockNode.execute(robot);
        } else {
            if (elseNode != null) elseNode.execute(robot);
        }
    }

    @Override
    public String privateToCode() {
        String string = super.privateToCode();
        if (elseNode != null) string += elseNode.toString();
        return string;
    }

    public static class NodeFactory implements Factory<IfNode> {
        @Override
        public IfNode create(Scanner scannerNotToBeModified) {
            return new IfNode();
        }

        @Override
        public boolean canStartWith(Scanner scannerNotToBeModified) {
            return scannerNotToBeModified.hasNext(IF_KEYWORD);
        }
    }

    public static class ElseNode extends ConditionBlockNode {
        private static final String ELSE_KEYWORD = "else";

        public ElseNode() {
            super(ELSE_KEYWORD, false);
        }

        @Override
        protected void privateExecute(Robot robot,
                                      ConditionNode conditionNode,
                                      BlockNode blockNode) {
            blockNode.execute(robot);
        }
    }
}
