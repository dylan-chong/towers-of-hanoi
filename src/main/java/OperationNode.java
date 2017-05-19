import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * Created by Dylan on 16/05/17.
 */
public class OperationNode extends ExpressionNode {

    private final DecorableFunctionNode<ParsableNode<Integer>, Integer> subNode;

    public OperationNode(
            String functionNamePattern,
            BiFunction<Robot, List<ParsableNode<Integer>>, Integer> applyOperation) {

        subNode = new DecorableFunctionNode<>(
                functionNamePattern,
                2,
                applyOperation,
                new ExpressionNode.NodeFactory()
        );
    }

    @Override
    public Integer execute(Robot robot) {
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

    public enum Operations {
        ADD("add", (robot, params) -> params.stream()
                .map(node -> node.execute(robot))
                .reduce((a, b) -> a + b)
                .orElse(0)
        ),
        SUBTRACT("sub", (robot, params) -> {
            int result = params.get(0).execute(robot);
            for (int i = 1; i < params.size(); i++) {
                result -= params.get(i).execute(robot);
            }
            return result;
        }),
        MULTIPLY("mul", (robot, params) -> params.stream()
                .map(node -> node.execute(robot))
                .reduce((a, b) -> a * b)
                .orElse(0)
        ),
        DIVIDE("div", (robot, params) -> {
            int result = params.get(0).execute(robot);
            for (int i = 1; i < params.size(); i++) {
                result /= params.get(i).execute(robot);
            }
            return result;
        });

        public final String namePattern;
        public final BiFunction<Robot, List<ParsableNode<Integer>>, Integer> applyOperation;

        Operations(String namePattern,
                   BiFunction<Robot, List<ParsableNode<Integer>>, Integer> applyOperation) {

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

