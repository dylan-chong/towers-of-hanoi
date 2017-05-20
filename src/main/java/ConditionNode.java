import java.util.Arrays;
import java.util.Collection;

/**
 * Created by Dylan on 18/05/17.
 */
public abstract class ConditionNode extends AbstractParsableNode<Boolean> {
    public static class NodeFactory extends DelegatorFactory<ConditionNode> {
        @Override
        protected Collection<Factory<? extends ConditionNode>> getPossibilities() {
            return Arrays.asList(
                    new ComparisonNode.NodeFactory(),
                    new BooleanOperationNode.NodeFactory()
            );
        }
    }
}
