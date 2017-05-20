/**
 * Created by Dylan on 8/05/17.
 */
public class ProgramNode extends MultiStatementNode implements VariableScopeNode {

    private final Scope scope = new Scope();

    public ProgramNode(ParsableNode<?> parentNode) {
        super(parentNode, 0);
    }

    @Override
    public Scope getScope() {
        return scope;
    }
}
