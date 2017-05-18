import java.util.Scanner;

/**
 * Created by Dylan on 18/05/17.
 */
public class IfNode extends StatementNode {
    private static final String IF_KEYWORD = "if";

    private final BlockNode blockNode = new BlockNode();
    private ConditionNode conditionNode;

    @Override
    public void execute(Robot robot) {
        if (conditionNode.evaluate()) blockNode.execute(robot);
    }

    @Override
    protected void privateDoParse(Scanner scanner, Logger logger) {
        require(IF_KEYWORD, scanner, ParserFailureType.WRONG_NODE_START);

        require("\\(", scanner, ParserFailureType.WRONG_MIDDLE_OR_END_OF_NODE);
        conditionNode = new ConditionNode.NodeFactory().create(scanner);
        conditionNode.parse(scanner, logger);
        require("\\)", scanner, ParserFailureType.WRONG_MIDDLE_OR_END_OF_NODE);

        blockNode.parse(scanner, logger);
    }

    @Override
    public String privateToCode() {
        return String.format("%s(%s)%s",
                IF_KEYWORD, conditionNode.toString(), blockNode.toString()
        );
    }

    @Override
    public Void evaluate() {
        return null;
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
