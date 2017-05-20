import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * Created by Dylan on 18/05/17.
 */
public class ComparisonNode extends ConditionNode {

    private static final Map<String, BiFunction<Integer, Integer, Boolean>> FUNCTIONS;

    static {
        FUNCTIONS = new HashMap<>();
        FUNCTIONS.put("lt", (n1, n2) -> n1 < n2);
        FUNCTIONS.put("gt", (n1, n2) -> n1 > n2);
        FUNCTIONS.put("eq", Objects::equals);
    }

    private final DecorableFunctionNode<ExpressionNode, Boolean> subNode;

    public ComparisonNode(ParsableNode<?> parentNode,
                          Map.Entry<String, BiFunction<Integer, Integer, Boolean>>
                                  nameToFunction) {
        super(parentNode);
        subNode = new DecorableFunctionNode<>(
                parentNode,
                nameToFunction.getKey(),
                2,
                adaptFunction(nameToFunction.getValue()),
                new ExpressionNode.NodeFactory()
        );
    }

    private static BiFunction<Robot, List<ExpressionNode>, Boolean> adaptFunction(
            BiFunction<Integer, Integer, Boolean> function) {

        return (robot, paramNodes) -> {
            List<Integer> params = paramNodes.stream()
                    .map(node -> node.execute(robot))
                    .collect(Collectors.toList());
            return function.apply(params.get(0), params.get(1));
        };
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

    public static class NodeFactory implements Factory<ComparisonNode> {
        @Override
        public ComparisonNode create(Scanner scannerNotToBeModified,
                                     ParsableNode<?> parentNode) {
            List<Map.Entry<String, BiFunction<Integer, Integer, Boolean>>> matches
                    = FUNCTIONS.entrySet()
                    .stream()
                    .filter(entry -> scannerNotToBeModified.hasNext(entry.getKey()))
                    .collect(Collectors.toList());
            requireOnlyOne(matches, scannerNotToBeModified);
            return new ComparisonNode(parentNode, matches.get(0));
        }

        @Override
        public boolean canStartWith(Scanner scannerNotToBeModified) {
            return FUNCTIONS.keySet()
                    .stream()
                    .filter(scannerNotToBeModified::hasNext)
                    .collect(Collectors.toList())
                    .size() > 0;
        }
    }
}
