import java.util.Arrays;
import java.util.Collection;

/**
 * Created by Dylan on 10/05/17.
 */
public abstract class StatementNode extends ParsableNode<Void> {

    public static class NodeFactory extends Factory.DelegatorFactory<StatementNode> {
        @Override
        protected Collection<Factory<? extends StatementNode>> getPossibilities() {
            return Arrays.asList(
                    new ActionNode.NodeFactory(),
                    new LoopNode.NodeFactory()
            );
        }
    }

}
