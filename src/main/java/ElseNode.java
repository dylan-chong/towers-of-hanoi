import java.util.Scanner;

/**
 * Created by Dylan on 20/05/17.
 */
public class ElseNode extends ConditionBlockNode implements ElsableNode {
    private static final String ELSE_KEYWORD = "else";

    public ElseNode(ParsableNode<?> parentNode) {
        super(parentNode, ELSE_KEYWORD, false);
    }

    @Override
    protected void privateExecute(Robot robot,
                                  ConditionNode conditionNode,
                                  BlockNode blockNode) {
        blockNode.execute(robot);
    }

    public static class NodeFactory implements Factory<ElseNode> {
        @Override
        public ElseNode create(Scanner scannerNotToBeModified,
                               ParsableNode<?> parentNode) {
            return new ElseNode(parentNode);
        }

        @Override
        public boolean canStartWith(Scanner scannerNotToBeModified) {
            return scannerNotToBeModified.hasNext(ELSE_KEYWORD);
        }
    }
}
