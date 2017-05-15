import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Dylan on 10/05/17.
 */

public abstract class ExpressionNode extends ParsableNode<Integer> {

    @Override
    public final void execute(Robot robot) {
        // Do nothing
    }

    public static class NumberNode extends ExpressionNode {
        private int number;

        @Override
        protected void privateDoParse(Scanner scanner) {
            number = Integer.parseInt(require("\\s*-?\\d+",
                    scanner,
                    ParserFailureType.NUMBER_FORMAT
            ));
        }

        @Override
        public String privateToCode() {
            return String.valueOf(number);
        }

        @Override
        public Integer evaluate() {
            return number;
        }
    }

    public static abstract class OperationNode extends ExpressionNode {
        private final String functionNamePattern;
        private List<ParsableNode<Integer>> params = new ArrayList<>(2);

        public OperationNode(String functionNamePattern) {
            this.functionNamePattern = functionNamePattern;
        }

        @Override
        protected void privateDoParse(Scanner scanner) {
            require(functionNamePattern, scanner, ParserFailureType.WRONG_NODE_START);
            require("\\(", scanner, ParserFailureType.WRONG_MIDDLE_OR_END_OF_NODE);

            // TODO pick nright numberable
            params.add(new NumberNode());
            params.get(0).parse(scanner);

            require(",", scanner, ParserFailureType.WRONG_MIDDLE_OR_END_OF_NODE);

            params.add(new NumberNode());
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

        protected abstract Integer applyOperation(List<ParsableNode<Integer>> params);

        // TODO remove, replace with enum
        public static class AddNode extends OperationNode {
            public AddNode() {
                super("add");
            }

            @Override
            protected Integer applyOperation(List<ParsableNode<Integer>> params) {
                return params.stream()
                        .map(ParsableNode::evaluate)
                        .reduce((a, b) -> a + b)
                        .orElse(0);
            }
        }
    }
}
