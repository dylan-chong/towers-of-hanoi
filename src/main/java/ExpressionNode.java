import java.util.Arrays;
import java.util.Collection;

/**
 * Created by Dylan on 10/05/17.
 */

public abstract class ExpressionNode extends AbstractParsableNode<Integer> {

    public ExpressionNode(ParsableNode<?> parentNode) {
        super(parentNode);
    }

    public static class NodeFactory extends DelegatorFactory<ExpressionNode> {
        @Override
        protected Collection<Factory<? extends ExpressionNode>> getPossibilities() {
            return Arrays.asList(
                    new OperationNode.NodeFactory(),
                    new NumberNode.NodeFactory(),
                    new SensorNode.NodeFactory(),
                    new VariableNode.NodeFactory()
            );
        }
    }
}
