import javax.lang.model.element.ElementVisitor;
import java.util.Collection;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by Dylan on 8/05/17.
 *
 * @param <EvalT> The type that this node returns (or {@link Void})
 */
public interface ParsableNode<EvalT> extends RobotProgramNode<EvalT> {

    void parse(Scanner scanner, Logger logger);

    /**
     * Every {@link ParsableNode} should have a static factory class (that
     * overrides this). Don't implement this directly if you have a factory
     * that delegates to other factories; extend the {@link DelegatorFactory}
     *
     * @param <NodeT> The type of node to produce
     */
    interface Factory<NodeT extends ParsableNode<?>> {

        /**
         * Create a new {@link ParsableNode}. Pick a node by choosing what
         * can be created using
         */
        NodeT create(Scanner scannerNotToBeModified);

        /**
         * @param scanner Scanner to call hasNext(pattern) on. Do not modify
         *                the scanner. This is to be used for factories that use
         *                other factories to create nodes to decide which to use
         */
        boolean canStartWith(Scanner scannerNotToBeModified);
    }
}
