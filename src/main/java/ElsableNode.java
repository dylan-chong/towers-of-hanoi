import java.util.Arrays;
import java.util.Collection;

/**
 * Created by Dylan on 20/05/17.
 *
 * Elif or Else
 */
public interface ElsableNode extends ParsableNode<Void> {
    class NodeFactory extends AbstractParsableNode.DelegatorFactory<ElsableNode> {
        @Override
        protected Collection<Factory<? extends ElsableNode>> getPossibilities() {
            return Arrays.asList(
                    new ElseNode.NodeFactory(),
                    new ElifNode.NodeFactory()
            );
        }
    }
}

