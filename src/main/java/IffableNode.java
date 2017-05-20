import java.util.Scanner;

/**
 * Created by Dylan on 20/05/17.
 *
 * Elif or If
 */
public abstract class IffableNode extends ConditionBlockNode {

    private ElsableNode elsableNode; // optional

    public IffableNode(ParsableNode<?> parentNode,
                       String keyword) {
        super(parentNode, keyword, true);
    }

    @Override
    protected void privateDoParse(Scanner scanner, Logger logger) {
        super.privateDoParse(scanner, logger);
        ElsableNode.NodeFactory factory = new ElsableNode.NodeFactory();
        if (factory.canStartWith(scanner)) {
            elsableNode = factory.create(scanner, this);
            elsableNode.parse(scanner, logger);
        }
    }

    @Override
    protected void privateExecute(Robot robot,
                                  ConditionNode conditionNode,
                                  BlockNode blockNode) {
        if (conditionNode.execute(robot)) {
            blockNode.execute(robot);
        } else {
            if (elsableNode != null) elsableNode.execute(robot);
        }
    }

    @Override
    public String privateToCode() {
        String string = super.privateToCode();
        if (elsableNode != null) string += elsableNode.toString();
        return string;
    }

}

