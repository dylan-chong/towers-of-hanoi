import java.util.Scanner;

/**
 * Created by Dylan on 18/05/17.
 *
 * Includes Else and Elif nodes
 */
public class IfNode extends IffableNode {
    private static final String IF_KEYWORD = "if";

    public IfNode(ParsableNode<?> parentNode) {
        super(parentNode, IF_KEYWORD);
    }

    public static class NodeFactory implements Factory<IfNode> {
        @Override
        public IfNode create(Scanner scannerNotToBeModified,
                             ParsableNode<?> parentNode) {
            return new IfNode(parentNode);
        }

        @Override
        public boolean canStartWith(Scanner scannerNotToBeModified) {
            return scannerNotToBeModified.hasNext(IF_KEYWORD);
        }
    }
}
