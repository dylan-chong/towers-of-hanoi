import java.util.Scanner;

/**
 * Created by Dylan on 20/05/17.
 *
 * For tests only
 */
public class AssignmentContainerTestNode extends AbstractParsableNode<Void>
        implements VariableScopeNode {

    private final Scope scope = new Scope();

    private AssignmentNode assignmentNode;

    public AssignmentContainerTestNode(ParsableNode<?> parentNode) {
        super(parentNode);
    }

    @Override
    public Void execute(Robot robot) {
        return assignmentNode.execute(robot);
    }

    @Override
    public Scope getScope() {
        return scope;
    }

    @Override
    protected void privateDoParse(Scanner scanner, Logger logger) {
        assignmentNode = new AssignmentNode(this);
        assignmentNode.parse(scanner, logger);
    }

    @Override
    protected String privateToCode() {
        return assignmentNode.toString();
    }
}
