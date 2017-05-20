import java.util.Scanner;

/**
 * Created by Dylan on 18/05/17.
 */
public class WhileNode extends ConditionBlockNode {
    private static final String WHILE_KEYWORD = "while";

    public WhileNode(ParsableNode<?> parentNode) {
        super(parentNode, WHILE_KEYWORD, true);
    }

    @Override
    protected void privateExecute(Robot robot,
                                  ConditionNode conditionNode,
                                  BlockNode blockNode) {
        while (conditionNode.execute(robot)) {
            blockNode.execute(robot);
        }
    }

    public static class NodeFactory implements Factory<WhileNode> {
        @Override
        public WhileNode create(Scanner scannerNotToBeModified,
                                ParsableNode<?> parentNode) {
            return new WhileNode(parentNode);
        }

        @Override
        public boolean canStartWith(Scanner scannerNotToBeModified) {
            return scannerNotToBeModified.hasNext(WHILE_KEYWORD);
        }
    }
}
