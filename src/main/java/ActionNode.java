import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * Created by Dylan on 16/05/17.
 */
public class ActionNode extends StatementNode {

    /**
     * Map of Pattern -> Robot function with no return value or params
     *
     * This is only public for tests.
     */
    public static final Map<String, Action> ALL_ACTIONS;

    static {
        ALL_ACTIONS = new HashMap<>();
        ALL_ACTIONS.put("turnL", new Action((robot, params) -> robot.turnLeft()));
        ALL_ACTIONS.put("turnR", new Action((robot, params) -> robot.turnRight()));
        ALL_ACTIONS.put("turnAround", new Action((robot, params) -> robot.turnAround()));
        ALL_ACTIONS.put("shieldOn", new Action((robot, params) -> robot.setShield(true)));
        ALL_ACTIONS.put("shieldOff", new Action((robot, params) -> robot.setShield(false)));
        ALL_ACTIONS.put("takeFuel", new Action((robot, params) -> robot.takeFuel()));

        ALL_ACTIONS.put("move", new Action(true, (robot, params) -> {
            int times = params.size() > 0 ? params.get(0).execute(robot) : 1;
            for (int i = 0; i < times; i++) robot.move();
        }));
        ALL_ACTIONS.put("wait", new Action(true, (robot, params) -> {
            int times = params.size() > 0 ? params.get(0).execute(robot) : 1;
            for (int i = 0; i < times; i++) robot.idleWait();
        }));
    }

    private Map.Entry<String, Action> actionEntry;
    private List<ExpressionNode> params;

    @Override
    protected void privateDoParse(Scanner scanner, Logger logger) {
        List<Map.Entry<String, Action>> validActions = ALL_ACTIONS
                .entrySet()
                .stream()
                .filter(entry -> scanner.hasNext(entry.getKey()))
                .collect(Collectors.toList());
        requireOnlyOne(validActions, scanner);

        actionEntry = validActions.get(0);
        params = new ArrayList<>();
        String actionName = actionEntry.getKey();
        Action action = actionEntry.getValue();

        require(actionName, scanner, ParserFailureType.WRONG_NODE_START);

        // Optional param (valid syntax: 'action(EXPR);' or 'action;'
        if (action.hasOptionalParam && !scanner.hasNext(";")) {
            require("\\(", scanner, ParserFailureType.WRONG_MIDDLE_OR_END_OF_NODE);
            ExpressionNode expressionNode =
                    new ExpressionNode.NodeFactory().create(scanner);
            expressionNode.parse(scanner, logger);
            params.add(expressionNode);

            require("\\)", scanner, ParserFailureType.WRONG_MIDDLE_OR_END_OF_NODE);
        }
        require(";", scanner, ParserFailureType.WRONG_MIDDLE_OR_END_OF_NODE);
    }

    @Override
    public String privateToCode() {
        StringBuilder stringBuilder = new StringBuilder(actionEntry.getKey());

        if (!params.isEmpty())
            stringBuilder
                    .append('(')
                    .append(params.get(0))
                    .append(')');

        return stringBuilder
                .append(';')
                .toString();
    }

    @Override
    public Void execute(Robot robot) {
        actionEntry.getValue()
                .function
                .accept(robot, params);
        return null;
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

    public static class Action {
        public final BiConsumer<Robot, List<ExpressionNode>> function; // robot, params
        public final boolean hasOptionalParam;

        public Action(BiConsumer<Robot, List<ExpressionNode>> function) {
            this(false, function);
        }

        public Action(boolean hasOptionalParam,
                      BiConsumer<Robot, List<ExpressionNode>> function) {
            this.function = function;
            this.hasOptionalParam = hasOptionalParam;
        }
    }
}

