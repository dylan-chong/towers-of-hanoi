import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * Created by Dylan on 16/05/17.
 */
public class BooleanOperationNode extends ConditionNode {

    private final DecorableFunctionNode<ConditionNode, Boolean> subNode;

    public BooleanOperationNode(ParsableNode<?> parentNode,
                                BoolOperation operation) {
        super(parentNode);
        subNode = new DecorableFunctionNode<>(
                parentNode,
                operation.functionName,
                operation.numberOfArgs,
                operation.function,
                new ConditionNode.NodeFactory()
        );
    }

    @Override
    public Boolean execute(Robot robot) {
        return subNode.execute(robot);
    }

    @Override
    protected void privateDoParse(Scanner scanner, Logger logger) {
        subNode.parse(scanner, logger);
    }

    @Override
    protected String privateToCode() {
        return subNode.toString();
    }

    public static class NodeFactory implements Factory<BooleanOperationNode> {
        @Override
        public BooleanOperationNode create(Scanner scannerNotToBeModified,
                                           ParsableNode<?> parentNode) {
            List<BoolOperation> matches = Arrays
                    .stream(BoolOperation.values())
                    .filter(operation ->
                            scannerNotToBeModified.hasNext(operation.functionName))
                    .collect(Collectors.toList());
            requireOnlyOne(matches, scannerNotToBeModified);
            return matches.get(0).create(parentNode);
        }

        @Override
        public boolean canStartWith(Scanner scannerNotToBeModified) {
            return Arrays.stream(BoolOperation.values())
                    .filter(operation ->
                            scannerNotToBeModified.hasNext(operation.functionName))
                    .count() > 0;
        }
    }

    public enum BoolOperation {
        AND("and", 2, (robot, params) ->
                params.get(0).execute(robot) && params.get(1).execute(robot)),
        OR("or", 2, (robot, params) ->
                params.get(0).execute(robot) || params.get(1).execute(robot)),
        NOT("not", 1, (robot, params) -> !params.get(0).execute(robot));

        public final String functionName;
        public final BoolOperationFunction function;
        public final int numberOfArgs;

        BoolOperation(String functionName,
                      int numberOfArgs,
                      BoolOperationFunction function) {
            this.functionName = functionName;
            this.function = function;
            this.numberOfArgs = numberOfArgs;
        }

        public BooleanOperationNode create(ParsableNode<?> parentNode) {
            return new BooleanOperationNode(parentNode, this);
        }
    }

    /**
     * Type alias
     */
    private interface BoolOperationFunction
            extends BiFunction<Robot, List<ConditionNode>, Boolean> {
    }
}

