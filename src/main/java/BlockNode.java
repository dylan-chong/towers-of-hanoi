/**
 * Created by Dylan on 12/05/17.
 */
public class BlockNode extends MultiStatementNode {
    public BlockNode(ParsableNode<?> parentNode) {
        super(parentNode, 1, "\\{", "\\}");
    }
}
