import java.util.Scanner;

/**
 * Created by Dylan on 18/05/17.
 */
public class IfNode extends ConditionBlockNode {
    private static final String IF_KEYWORD = "if";

    public IfNode() {
        super(IF_KEYWORD);
    }

    @Override
    protected void privateExecute(Robot robot,
                                  ConditionNode conditionNode,
                                  BlockNode blockNode) {
        if (conditionNode.evaluate(robot)) blockNode.execute(robot);
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
}
