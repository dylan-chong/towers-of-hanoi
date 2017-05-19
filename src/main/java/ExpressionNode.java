import java.util.Arrays;
import java.util.Collection;

/**
 * Created by Dylan on 10/05/17.
 */

public abstract class ExpressionNode extends ParsableNode<Integer> {

    @Override
    public final void execute(Robot robot) {
        // Do nothing
    }

    public static class NodeFactory extends Factory.DelegatorFactory<ExpressionNode> {
        @Override
        protected Collection<Factory<? extends ExpressionNode>> getPossibilities() {
            return Arrays.asList(
                    new OperationNode.NodeFactory(),
                    new NumberNode.NodeFactory(),
                    new SensorNode.NodeFactory()
            );
        }
    }
}
