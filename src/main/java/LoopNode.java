import java.util.Scanner;

/**
 * Created by Dylan on 16/05/17.
 */
public class LoopNode extends StatementNode {

    private static final String LOOP_KEYWORD = "loop";

    private final BlockNode blockNode = new BlockNode();

    @Override
    public void execute(Robot robot) {
        // noinspection InfiniteLoopStatement
        while (true) blockNode.execute(robot);
    }

    @Override
    protected void privateDoParse(Scanner scanner) {
        require(LOOP_KEYWORD, scanner, ParserFailureType.WRONG_NODE_START);
        blockNode.parse(scanner);
    }

    @Override
    public String privateToCode() {
        return "loop" + blockNode.toString();
    }

    @Override
    public Void evaluate() {
        return null;
    }

    public static class NodeFactory implements Factory<LoopNode> {
        @Override
        public LoopNode create(Scanner scannerNotToBeModified) {
            return new LoopNode();
        }

        @Override
        public boolean canStartWith(Scanner scannerNotToBeModified) {
            return scannerNotToBeModified.hasNext(LOOP_KEYWORD);
        }
    }
}

