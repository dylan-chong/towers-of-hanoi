import java.util.Scanner;

/**
 * Created by Dylan on 20/05/17.
 */
public class VariableNode extends ExpressionNode {
    private static final String VARIABLE_PATTERN = "\\$[A-Za-z][A-Za-z0-9]*";

    private String variableName;

    public VariableNode(ParsableNode<?> parentNode) {
        super(parentNode);
    }

    @Override
    public Integer execute(Robot robot) {
        return getValue();
    }

    public Integer getValue() {
        return getScopeNode()
                .getScope()
                .getValue(variableName);
    }

    public Integer putValue(Integer value) {
        return getScopeNode()
                .getScope()
                .putValue(variableName, value);
    }

    @Override
    protected void privateDoParse(Scanner scanner, Logger logger) {
        variableName = require(
                VARIABLE_PATTERN, scanner, ParserFailureType.VARIABLE_FORMAT
        );
    }

    @Override
    protected String privateToCode() {
        return variableName;
    }

    private VariableScopeNode getScopeNode() {
        VariableScopeNode closestScopeNode = VariableScopeNode.getClosestScopeNode(this);
        if (closestScopeNode == null)
            throw new IllegalStateException("No parent VariableScopeNode");
        return closestScopeNode;
    }

    public static class NodeFactory implements Factory<VariableNode> {
        @Override
        public VariableNode create(Scanner scannerNotToBeModified,
                                   ParsableNode<?> parentNode) {
            return new VariableNode(parentNode);
        }

        @Override
        public boolean canStartWith(Scanner scannerNotToBeModified) {
            return scannerNotToBeModified.hasNext(VARIABLE_PATTERN);
        }
    }
}
