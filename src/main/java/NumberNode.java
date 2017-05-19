import java.util.Scanner;

/**
 * Created by Dylan on 16/05/17.
 */
public class NumberNode extends ExpressionNode {
    private static final String NUMBER_PATTERN = "\\s*-?\\d+";
    private int number;

    @Override
    protected void privateDoParse(Scanner scanner, Logger logger) {
        number = Integer.parseInt(require(NUMBER_PATTERN,
                scanner,
                ParserFailureType.NUMBER_FORMAT
        ));
    }

    @Override
    public String privateToCode() {
        return String.valueOf(number);
    }

    @Override
    public Integer evaluate(Robot robot) {
        return number;
    }

    public static class NodeFactory implements Factory<NumberNode> {
        @Override
        public NumberNode create(Scanner scannerNotToBeModified) {
            return new NumberNode();
        }

        @Override
        public boolean canStartWith(Scanner scannerNotToBeModified) {
            return scannerNotToBeModified.hasNext(NUMBER_PATTERN);
        }
    }
}


