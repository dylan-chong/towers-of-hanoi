import java.util.Scanner;

/**
 * Created by Dylan on 20/05/17.
 */
public class AssignmentNode extends StatementNode {

    private VariableNode variableNode;
    private ExpressionNode expressionNode;

    public AssignmentNode(ParsableNode<?> parentNode) {
        super(parentNode);
    }

    @Override
    public Void execute(Robot robot) {
        Integer expressionValue = expressionNode.execute(robot);
        variableNode.putValue(expressionValue);
        return null;
    }

    @Override
    protected void privateDoParse(Scanner scanner, Logger logger) {
        variableNode = new VariableNode(this);
        variableNode.parse(scanner, logger);

        require("=", scanner, ParserFailureType.WRONG_MIDDLE_OR_END_OF_NODE);

        expressionNode = new ExpressionNode.NodeFactory().create(scanner, this);
        expressionNode.parse(scanner, logger);

        require(";", scanner, ParserFailureType.WRONG_MIDDLE_OR_END_OF_NODE);
    }

    @Override
    protected String privateToCode() {
        return String.format("%s=%s;",
                variableNode.toString(), expressionNode.toString());
    }

    public static class NodeFactory implements Factory<AssignmentNode> {

        @Override
        public AssignmentNode create(Scanner scannerNotToBeModified,
                                     ParsableNode<?> parentNode) {
            return new AssignmentNode(parentNode);
        }

        @Override
        public boolean canStartWith(Scanner scannerNotToBeModified) {
            return new VariableNode.NodeFactory()
                    .canStartWith(scannerNotToBeModified);
        }
    }
}
