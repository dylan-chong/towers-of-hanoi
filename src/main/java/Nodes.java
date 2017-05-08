import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Dylan on 8/05/17.
 */
public class Nodes {
    private Nodes() {
    }

    public static class NumberNode extends ParsableNode<Integer> {
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


    }

    public static class AddNode extends ParsableNode<Integer> {
        private List<ParsableNode<Integer>> params = new ArrayList<>(2);

        @Override
        protected void privateDoParse(Scanner scanner) {
            require("add", scanner, ParserFailureType.WRONG_NODE_START);
            require("\\(", scanner, ParserFailureType.SYNTAX_ERROR);

            // TODO pick nright numberable
            params.add(new NumberNode());
            params.get(0).parse(scanner);

            require(",", scanner, ParserFailureType.SYNTAX_ERROR);

            params.add(new NumberNode());
            params.get(1).parse(scanner);

            require("\\)", scanner, ParserFailureType.SYNTAX_ERROR);
        }

        @Override
        public String toCode() {
            return String.format("add(%s,%s)", params.get(0), params.get(1));
        }

        @Override
        public Integer evaluate() {
            return params.get(0).evaluate() + params.get(1).evaluate();
        }

        @Override
        public void execute(Robot robot) {
            // Do nothing
        }
    }
}
