import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Dylan on 10/05/17.
 */
public class NumberNode extends ParsableNode<Integer> {
    private int number;

    @Override
    protected void privateDoParse(Scanner scanner) {
        number = Integer.parseInt(require("\\s*-?\\d+",
                scanner,
                ParserFailureType.NUMBER_FORMAT
        ));
    }

    @Override
    public void execute(Robot robot) {
        // Do nothing
    }

    @Override
    public String toCode() {
        return String.valueOf(number);
    }

    @Override
    public Integer evaluate() {
        return number;
    }

    // TODO parentNode
    public static abstract class OperationNode extends ParsableNode<Integer> {
        private List<ParsableNode<Integer>> params = new ArrayList<>(2);

        @Override
        protected void privateDoParse(Scanner scanner) {
            require(getFunctionName(), scanner, ParserFailureType.WRONG_NODE_START);
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
        public String toCode() {
            return String.format("%s(%s,%s)",
                    getFunctionName(),
                    params.get(0), params.get(1)
            );
        }

        @Override
        public Integer evaluate() {
            return params.get(0).evaluate() + params.get(1).evaluate();
        }

        @Override
        public void execute(Robot robot) {
            // Do nothing
        }

        protected abstract String getFunctionName();
    }

    public static class AddNode extends OperationNode {
        @Override
        protected String getFunctionName() {
            return "add";
        }
    }
}

