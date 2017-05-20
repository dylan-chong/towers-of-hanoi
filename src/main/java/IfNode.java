import java.util.Scanner;

/**
 * Created by Dylan on 18/05/17.
 *
 * Includes Else and Elif nodes
 */
public class IfNode extends IffableNode {
    private static final String IF_KEYWORD = "if";

    public IfNode() {
        super(IF_KEYWORD);
    }

    public static class NodeFactory implements Factory<IfNode> {
        @Override
        public IfNode create(Scanner scannerNotToBeModified) {
            return new IfNode();
        }

        @Override
        public boolean canStartWith(Scanner scannerNotToBeModified) {
            return scannerNotToBeModified.hasNext(IF_KEYWORD);
        }
    }
}
