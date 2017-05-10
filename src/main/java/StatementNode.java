import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Created by Dylan on 10/05/17.
 */
public abstract class StatementNode extends ParsableNode<Void> {

    public static class ActionNode extends StatementNode {
        /**
         * Map of Pattern -> Robot function with no return value or params
         */
        private static final Map<String, Consumer<Robot>> actions;

        // TODO move and wait params
        static {
            actions = new HashMap<>();
            actions.put("turnL", Robot::turnLeft);
            actions.put("turnR", Robot::turnRight);
            actions.put("turnAround", Robot::turnAround);
            actions.put("shieldOn", (robot) -> robot.setShield(true));
            actions.put("shieldOff", (robot) -> robot.setShield(false));
            actions.put("takeFuel", Robot::takeFuel);
        }

        private Map.Entry<String, Consumer<Robot>> action;

        @Override
        protected void privateDoParse(Scanner scanner) {
            List<Map.Entry<String, Consumer<Robot>>> validActions = actions
                    .entrySet()
                    .stream()
                    .filter(entry -> scanner.hasNext(entry.getKey()))
                    .collect(Collectors.toList());
            if (validActions.size() != 1) throwParseError(
                    String.format("Invalid number of valid actions (%d): %s",
                            validActions.size(),
                            validActions
                    ),
                    scanner,
                    ParserFailureType.NON_ONE_MATCHES
            );

            action = validActions.get(0);
            require(action.getKey(), scanner, ParserFailureType.WRONG_NODE_START);
            require(";", scanner, ParserFailureType.WRONG_MIDDLE_OR_END_OF_NODE);
        }

        @Override
        public String toCode() {
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

    }
}
