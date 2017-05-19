import java.util.Scanner;

/**
 * Created by Dylan on 18/05/17.
 */
public abstract class ConditionBlockNode extends StatementNode {

    private final BlockNode blockNode = new BlockNode();
    private final String keyword;

    private ConditionNode conditionNode;

    public ConditionBlockNode(String keyword) {
        this.keyword = keyword;
    }

    @Override
    protected void privateDoParse(Scanner scanner, Logger logger) {
        require(keyword, scanner, ParserFailureType.WRONG_NODE_START);

        require("\\(", scanner, ParserFailureType.WRONG_MIDDLE_OR_END_OF_NODE);
        conditionNode = new ConditionNode.NodeFactory().create(scanner);
        conditionNode.parse(scanner, logger);
        require("\\)", scanner, ParserFailureType.WRONG_MIDDLE_OR_END_OF_NODE);

        blockNode.parse(scanner, logger);
    }

    @Override
    public String privateToCode() {
        return String.format("%s(%s)%s",
                keyword, conditionNode.toString(), blockNode.toString()
        );
    }

    @Override
    public final Void execute(Robot robot) {
        privateExecute(robot, conditionNode, blockNode);
        return null;
    }

    protected abstract void privateExecute(Robot robot,
                                           ConditionNode conditionNode,
                                           BlockNode blockNode);
}
