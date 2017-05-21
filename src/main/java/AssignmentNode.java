import java.util.Scanner;

/**
 * Created by Dylan on 20/05/17.
 */
public class AssignmentNode extends StatementNode {

    private String variableName;
    private ExpressionNode expressionNode;

    public AssignmentNode(ParsableNode<?> parentNode) {
        super(parentNode);
    }

    @Override
    public Void execute(Robot robot) {
        Integer expressionValue = expressionNode.execute(robot);
        try {
            VariableScopeNode.getClosestScope(this,
                    variableName,
                    VariableScopeNode::getExecutionScope
            ).putValue(variableName, expressionValue);
        } catch (VariableScopeNode.ScopeNotFoundException e) {
            VariableScopeNode.ExecutionScope.throwScopeError(
                    variableName, ParserFailureType.UNDEFINED_VARIBLE_ASSIGNMENT
            );
        }
        return null;
    }

    @Override
    protected void privateDoParse(Scanner scanner, Logger logger) {
        variableName = require(
                VariableAccessNode.VARIABLE_PATTERN,
                scanner,
                ParserFailureType.WRONG_NODE_START
        );
        try {
            VariableScopeNode.getClosestScope(
                    this,
                    variableName,
                    VariableScopeNode::getCompilationScope
            ).putValue(variableName, 1);
        } catch (VariableScopeNode.ScopeNotFoundException e) {
            VariableScopeNode.CompilationScope.throwScopeError(
                    variableName, ParserFailureType.UNDEFINED_VARIBLE_ASSIGNMENT
            );
        }

        require("=", scanner, ParserFailureType.WRONG_MIDDLE_OR_END_OF_NODE);

        expressionNode = new ExpressionNode.NodeFactory().create(scanner, this);
        expressionNode.parse(scanner, logger);

        require(";", scanner, ParserFailureType.WRONG_MIDDLE_OR_END_OF_NODE);
    }

    @Override
    protected String privateToCode() {
        return String.format("%s=%s;",
                variableName, expressionNode.toString());
    }

    public static class NodeFactory implements Factory<AssignmentNode> {

        @Override
        public AssignmentNode create(Scanner scannerNotToBeModified,
                                     ParsableNode<?> parentNode) {
            return new AssignmentNode(parentNode);
        }

        @Override
        public boolean canStartWith(Scanner scannerNotToBeModified) {
            return new VariableAccessNode.NodeFactory()
                    .canStartWith(scannerNotToBeModified);
        }
    }
}
