import java.util.Scanner;

/**
 * Created by Dylan on 20/05/17.
 */
public class VariableAccessNode extends ExpressionNode {
    public static final String VARIABLE_PATTERN = "\\$[A-Za-z][A-Za-z0-9]*";

    private String variableName;

    public VariableAccessNode(ParsableNode<?> parentNode) {
        super(parentNode);
    }

    @Override
    public Integer execute(Robot robot) {
        try {
            return VariableScopeNode.getClosestScope(
                    this, variableName, VariableScopeNode::getExecutionScope
            ).getValue(variableName);
        } catch (VariableScopeNode.ScopeNotFoundException e) {
            VariableScopeNode.ExecutionScope.throwScopeError(
                    variableName,
                    ParserFailureType.UNDEFINED_VARIBLE_ACCESS
            );
            return null; // unreachable
        }
    }

    @Override
    protected void privateDoParse(Scanner scanner, Logger logger) {
        variableName = require(
                VARIABLE_PATTERN, scanner, ParserFailureType.VARIABLE_FORMAT
        );
        try {
            VariableScopeNode.getClosestScope(
                    this, variableName, VariableScopeNode::getCompilationScope
            ).getValue(variableName);
        } catch (VariableScopeNode.ScopeNotFoundException e) {
            VariableScopeNode.CompilationScope.throwScopeError(
                    variableName,
                    ParserFailureType.UNDEFINED_VARIBLE_ACCESS
            );
        }
    }

    @Override
    protected String privateToCode() {
        return variableName;
    }

    public static class NodeFactory implements Factory<VariableAccessNode> {
        @Override
        public VariableAccessNode create(Scanner scannerNotToBeModified,
                                         ParsableNode<?> parentNode) {
            return new VariableAccessNode(parentNode);
        }

        @Override
        public boolean canStartWith(Scanner scannerNotToBeModified) {
            return scannerNotToBeModified.hasNext(VARIABLE_PATTERN);
        }
    }
}
