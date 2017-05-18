import java.util.Collection;
import java.util.Collections;

/**
 * Created by Dylan on 18/05/17.
 */
public abstract class ConditionNode extends ParsableNode<Boolean> {
    public static class NodeFactory extends Factory.DelegatorFactory<ConditionNode> {
        @Override
        protected Collection<Factory<? extends ConditionNode>> getPossibilities() {
            return Collections.singletonList(
                    new ComparisonNode.NodeFactory()
            );
        }
    }
}
