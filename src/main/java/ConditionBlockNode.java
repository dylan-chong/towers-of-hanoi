import java.util.Scanner;

/**
 * Created by Dylan on 18/05/17.
 */
public abstract class ConditionBlockNode extends StatementNode {

    private final BlockNode blockNode = new BlockNode();
    private final String keyword;
    private final boolean hasCondition;

    private ConditionNode conditionNode;

    public ConditionBlockNode(String keyword, boolean hasCondition) {
        this.keyword = keyword;
        this.hasCondition = hasCondition;
    }

    @Override
    protected void privateDoParse(Scanner scanner, Logger logger) {
        require(keyword, scanner, ParserFailureType.WRONG_NODE_START);

        if (hasCondition) {
            require("\\(", scanner, ParserFailureType.WRONG_MIDDLE_OR_END_OF_NODE);
            conditionNode = new ConditionNode.NodeFactory().create(scanner);
            conditionNode.parse(scanner, logger);
            require("\\)", scanner, ParserFailureType.WRONG_MIDDLE_OR_END_OF_NODE);
        }

        blockNode.parse(scanner, logger);
    }

    @Override
    public String privateToCode() {
        StringBuilder stringBuilder = new StringBuilder(keyword);
        if (hasCondition) stringBuilder
                .append('(')
                .append(conditionNode.toString())
                .append(')');
        return stringBuilder
                .append(blockNode.toString())
                .toString();
    }

    @Override
    public final Void execute(Robot robot) {
        privateExecute(robot, conditionNode, blockNode);
        return null;
    }

    public ConditionNode getConditionNode() {
        return conditionNode;
    }

    public BlockNode getBlockNode() {
        return blockNode;
    }

    public String getKeyword() {
        return keyword;
    }

    protected abstract void privateExecute(Robot robot,
                                           ConditionNode conditionNode,
                                           BlockNode blockNode);
}
