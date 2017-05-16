import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Dylan on 16/05/17.
 */
public class OperationNode extends ExpressionNode {
    private final String functionNamePattern;
    private final Function<List<ParsableNode<Integer>>, Integer> applyOperation;
    private List<ParsableNode<Integer>> params = new ArrayList<>(2);

    public OperationNode(String functionNamePattern,
                         Function<List<ParsableNode<Integer>>, Integer> applyOperation) {
        this.functionNamePattern = functionNamePattern;
        this.applyOperation = applyOperation;
    }

    @Override
    protected void privateDoParse(Scanner scanner) {
        require(functionNamePattern, scanner, ParserFailureType.WRONG_NODE_START);
        require("\\(", scanner, ParserFailureType.WRONG_MIDDLE_OR_END_OF_NODE);

        ExpressionNode.NodeFactory factory = new ExpressionNode.NodeFactory();

        params.add(factory.create(scanner));
        params.get(0).parse(scanner);

        require(",", scanner, ParserFailureType.WRONG_MIDDLE_OR_END_OF_NODE);

        params.add(factory.create(scanner));
        params.get(1).parse(scanner);

        require("\\)", scanner, ParserFailureType.WRONG_MIDDLE_OR_END_OF_NODE);
    }

    @Override
    public String privateToCode() {
        return String.format("%s(%s,%s)",
                functionNamePattern,
                params.get(0), params.get(1)
        );
    }

    @Override
    public Integer evaluate() {
        return applyOperation(params);
    }

    private Integer applyOperation(List<ParsableNode<Integer>> params) {
        return applyOperation.apply(params);
    }

    public enum Operations {
        ADD("add", params -> params.stream()
                .map(ParsableNode::evaluate)
                .reduce((a, b) -> a + b)
                .orElse(0)
        ),
        SUBTRACT("sub", params -> {
            int result = params.get(0).evaluate();
            for (int i = 1; i < params.size(); i++) {
                result -= params.get(i).evaluate();
            }
            return result;
        }),
        MULTIPLY("mul", params -> params.stream()
                .map(ParsableNode::evaluate)
                .reduce((a, b) -> a * b)
                .orElse(0)
        ),
        DIVIDE("div", params -> {
            int result = params.get(0).evaluate();
            for (int i = 1; i < params.size(); i++) {
                result /= params.get(i).evaluate();
            }
            return result;
        });

        public final String namePattern;
        public final Function<List<ParsableNode<Integer>>, Integer> applyOperation;

        Operations(String namePattern,
                   Function<List<ParsableNode<Integer>>, Integer> applyOperation) {

            this.namePattern = namePattern;
            this.applyOperation = applyOperation;
        }

        public OperationNode create() {
            return new OperationNode(namePattern, applyOperation);
        }
    }

    public static class NodeFactory implements Factory<OperationNode> {
        @Override
        public OperationNode create(Scanner scannerNotToBeModified) {
            List<Operations> matches = Arrays.stream(Operations.values())
                    .filter(operation ->
                            scannerNotToBeModified.hasNext(operation.namePattern)
                    )
                    .collect(Collectors.toList());
            requireOnlyOne(matches, scannerNotToBeModified);

            return matches.get(0).create();
        }

        @Override
        public boolean canStartWith(Scanner scannerNotToBeModified) {
            return Arrays.stream(Operations.values())
                    .anyMatch(operation ->
                            scannerNotToBeModified.hasNext(operation.namePattern)
                    );
        }
    }
}

