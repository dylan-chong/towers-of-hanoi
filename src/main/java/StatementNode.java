import java.util.Arrays;
import java.util.Collection;

/**
 * Created by Dylan on 10/05/17.
 */
public abstract class StatementNode extends AbstractParsableNode<Void> {

    public static class NodeFactory extends DelegatorFactory<StatementNode> {
        @Override
        protected Collection<Factory<? extends StatementNode>> getPossibilities() {
            return Arrays.asList(
                    new ActionNode.NodeFactory(),
                    new LoopNode.NodeFactory(),
                    new IfNode.NodeFactory(),
                    new WhileNode.NodeFactory()
            );
        }
    }

}
