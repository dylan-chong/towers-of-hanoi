import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Created by Dylan on 10/05/17.
 */
public abstract class StatementNode extends ParsableNode<Void> {

    public static class NodeFactory extends Factory.DelegatorFactory<StatementNode> {
        @Override
        protected Collection<Factory<? extends StatementNode>> getPossibilities() {
            return Arrays.asList(
                    new ActionNode.NodeFactory(),
                    new LoopNode.NodeFactory()
            );
        }
    }

    public static abstract class ActionNode extends StatementNode {

        public static class NodeFactory extends Factory.DelegatorFactory<SimpleActionNode> {
            @Override
            protected Collection<Factory<? extends SimpleActionNode>> getPossibilities() {
                return Arrays.asList(
                        new SimpleActionNode.NodeFactory()
                );
            }
        }

        public static class SimpleActionNode extends StatementNode {
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

            public static class NodeFactory implements Factory<SimpleActionNode> {
                @Override
                public SimpleActionNode create(Scanner scannerNotToBeModified) {
                    return new SimpleActionNode();
                }

                @Override
                public boolean canStartWith(Scanner scanner) {
                    return ALL_ACTIONS.entrySet()
                            .stream()
                            .anyMatch(entry -> scanner.hasNext(entry.getKey()));
                }
            }
        }
    }

    public static class LoopNode extends StatementNode {

        private static final String LOOP_KEYWORD = "loop";

        private final BlockNode blockNode = new BlockNode();

        @Override
        public void execute(Robot robot) {
            // noinspection InfiniteLoopStatement
            while (true) blockNode.execute(robot);
        }

        @Override
        protected void privateDoParse(Scanner scanner) {
            require(LOOP_KEYWORD, scanner, ParserFailureType.WRONG_NODE_START);
            blockNode.parse(scanner);
        }

        @Override
        public String privateToCode() {
            return "loop" + blockNode.toString();
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
