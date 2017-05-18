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

    private final Map.Entry<String, BiFunction<Integer, Integer, Boolean>> nameToFunction;
    private List<ExpressionNode> params;

    public ComparisonNode(Map.Entry<String, BiFunction<Integer, Integer, Boolean>>
                                  nameToFunction) {
        this.nameToFunction = nameToFunction;
    }

    @Override
    public void execute(Robot robot) {
        // Do nothing
    }

    @Override
    protected void privateDoParse(Scanner scanner, Logger logger) {
        require(nameToFunction.getKey(), scanner, ParserFailureType.WRONG_NODE_START);
        require("\\(", scanner, ParserFailureType.WRONG_MIDDLE_OR_END_OF_NODE);

        params = new ArrayList<>();
        parseOneExpression(scanner, logger);
        require(",", scanner, ParserFailureType.WRONG_MIDDLE_OR_END_OF_NODE);

        parseOneExpression(scanner, logger);

        require("\\)", scanner, ParserFailureType.WRONG_MIDDLE_OR_END_OF_NODE);
    }

    @Override
    protected String privateToCode() {
        return String.format("%s(%s,%s)",
                nameToFunction.getKey(), params.get(0), params.get(1)
        );
    }

    @Override
    public Boolean evaluate() {
        return nameToFunction
                .getValue()
                .apply(params.get(0).evaluate(), params.get(1).evaluate());
    }

    private void parseOneExpression(Scanner scanner, Logger logger) {
        ExpressionNode.NodeFactory factory = new ExpressionNode.NodeFactory();
        ExpressionNode node = factory.create(scanner);
        node.parse(scanner, logger);
        params.add(node);
    }

    public static class NodeFactory implements Factory<ComparisonNode> {
        @Override
        public ComparisonNode create(Scanner scannerNotToBeModified) {
            List<Map.Entry<String, BiFunction<Integer, Integer, Boolean>>> matches
                    = FUNCTIONS.entrySet()
                    .stream()
                    .filter(entry -> scannerNotToBeModified.hasNext(entry.getKey()))
                    .collect(Collectors.toList());
            requireOnlyOne(matches, scannerNotToBeModified);
            return new ComparisonNode(matches.get(0));
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
