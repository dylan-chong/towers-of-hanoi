import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Created by Dylan on 10/05/17.
 */
public abstract class StatementNode extends ParsableNode<Void> {

    /**
     * All statement nodes (non-recursive)
     */
    private static final Collection<Factory<? extends StatementNode>> ALL_NODES = Arrays.asList(
            new ActionNode.NodeFactory(),
            new LoopNode.NodeFactory()
    );

    public static class NodeFactory implements Factory<StatementNode> {
        @Override
        public StatementNode create(Scanner scannerNotToBeModified) {
            Collection<Factory<? extends StatementNode>> matches = getMatches(scannerNotToBeModified);
            requireOnlyOne(matches, scannerNotToBeModified);
            return matches.stream()
                    .findAny()
                    .orElseThrow(AssertionError::new)
                    .create(scannerNotToBeModified);
        }

        @Override
        public boolean canStartWith(Scanner scannerNotToBeModified) {
            return getMatches(scannerNotToBeModified).size() > 0;
        }

        private Collection<Factory<? extends StatementNode>> getMatches(
                Scanner scannerNotToBeModified) {
            return ALL_NODES.stream()
                    .filter(factory -> factory.canStartWith(scannerNotToBeModified))
                    .collect(Collectors.toList());
        }
    }

    public static class ActionNode extends StatementNode {
        /**
         * Map of Pattern -> Robot function with no return value or params
         */
        private static final Map<String, Consumer<Robot>> ALL_ACTIONS;

        // TODO move and wait params
        static {
            ALL_ACTIONS = new HashMap<>();
            ALL_ACTIONS.put("turnL", Robot::turnLeft);
            ALL_ACTIONS.put("turnR", Robot::turnRight);
            ALL_ACTIONS.put("turnAround", Robot::turnAround);
            ALL_ACTIONS.put("shieldOn", (robot) -> robot.setShield(true));
            ALL_ACTIONS.put("shieldOff", (robot) -> robot.setShield(false));
            ALL_ACTIONS.put("takeFuel", Robot::takeFuel);
        }

        private Map.Entry<String, Consumer<Robot>> action;

        @Override
        protected void privateDoParse(Scanner scanner) {
            List<Map.Entry<String, Consumer<Robot>>> validActions = ALL_ACTIONS
                    .entrySet()
                    .stream()
                    .filter(entry -> scanner.hasNext(entry.getKey()))
                    .collect(Collectors.toList());
            requireOnlyOne(validActions, scanner);

            action = validActions.get(0);
            require(action.getKey(), scanner, ParserFailureType.WRONG_NODE_START);
            require(";", scanner, ParserFailureType.WRONG_MIDDLE_OR_END_OF_NODE);
        }

        @Override
        public String privateToCode() {
            return action.getKey() + ";";
        }

        @Override
        public Void evaluate() {
            return null; // returns nothing
        }

        @Override
        public void execute(Robot robot) {
            action.getValue().accept(robot);
        }

        public static class NodeFactory implements Factory<ActionNode> {

            @Override
            public ActionNode create(Scanner scannerNotToBeModified) {
                return new ActionNode();
            }

            @Override
            public boolean canStartWith(Scanner scanner) {
                return ALL_ACTIONS.entrySet()
                        .stream()
                        .anyMatch(entry -> scanner.hasNext(entry.getKey()));
            }
        }
    }

    // todo copy paste program node

    public static class LoopNode extends StatementNode {

        private static final String LOOP_KEYWORD = "loop";

        @Override
        public void execute(Robot robot) {

        }

        @Override
        protected void privateDoParse(Scanner scanner) {

        }

        @Override
        public String privateToCode() {
            return null;
        }

        @Override
        public Void evaluate() {
            return null;
        }

        public static class NodeFactory implements Factory<LoopNode> {
            @Override
            public LoopNode create(Scanner scannerNotToBeModified) {
                return new LoopNode();
            }

            @Override
            public boolean canStartWith(Scanner scannerNotToBeModified) {
                return scannerNotToBeModified.hasNext(LOOP_KEYWORD);
            }
        }
    }
}
