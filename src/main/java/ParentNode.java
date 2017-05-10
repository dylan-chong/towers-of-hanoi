import java.util.Collection;

/**
 * Created by Dylan on 10/05/17.
 *
 * @param <NodeT> The type of node that this node is a parent of
 */
public interface ParentNode<NodeT extends ParsableNode<?>> {
    Collection<ParsableNode.Factory<NodeT>> getPossibleChildrenFactories();
}
