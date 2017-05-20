import java.util.Scanner;

/**
 * Created by Dylan on 20/05/17.
 */
public class ElifNode extends IffableNode implements ElsableNode {
    private static final String ELIF_KEYWORD = "elif";

    public ElifNode(ParsableNode<?> parentNode) {
        super(parentNode, ELIF_KEYWORD);
    }

    public static class NodeFactory implements Factory<ElifNode> {
        @Override
        public ElifNode create(Scanner scannerNotToBeModified,
                               ParsableNode<?> parentNode) {
            return new ElifNode(parentNode);
        }

        @Override
        public boolean canStartWith(Scanner scannerNotToBeModified) {
            return scannerNotToBeModified.hasNext(ELIF_KEYWORD);
        }
    }
}
