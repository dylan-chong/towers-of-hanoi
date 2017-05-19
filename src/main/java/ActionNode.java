import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by Dylan on 16/05/17.
 */
public class ActionNode extends StatementNode {

    /**
     * Map of Pattern -> Robot function with no return value or params
     *
     * This is only public for tests.
     */
    public static final Map<String, FunctionWrapper<ExpressionNode, Void>> ALL_ACTIONS;

    static {
        ALL_ACTIONS = new HashMap<>();
        ALL_ACTIONS.put("turnL", new FunctionWrapper<>((robot, param) -> {
            robot.turnLeft();
            return null;
        }));
        ALL_ACTIONS.put("turnR", new FunctionWrapper<>((robot, param) -> {
            robot.turnRight();
            return null;
        }));
        ALL_ACTIONS.put("turnAround", new FunctionWrapper<>((robot, param) -> {
            robot.turnAround();
            return null;
        }));
        ALL_ACTIONS.put("shieldOn", new FunctionWrapper<>((robot, param) -> {
            robot.setShield(true);
            return null;
        }));
        ALL_ACTIONS.put("shieldOff", new FunctionWrapper<>((robot, param) -> {
            robot.setShield(false);
            return null;
        }));
        ALL_ACTIONS.put("takeFuel", new FunctionWrapper<>((robot, param) -> {
            robot.takeFuel();
            return null;
        }));

        ALL_ACTIONS.put("move", new FunctionWrapper<>(true, (robot, param) -> {
            int times = param == null ? 1 : param.execute(robot);
            for (int i = 0; i < times; i++) robot.takeFuel();
            return null;
        }));
        ALL_ACTIONS.put("wait", new FunctionWrapper<>(true, (robot, param) -> {
            int times = param == null ? 1 : param.execute(robot);
            for (int i = 0; i < times; i++) robot.idleWait();
            return null;
        }));
    }

    private final DecorableOptionalParamNode<ExpressionNode, Void> subnode =
            new DecorableOptionalParamNode<>(
                    ALL_ACTIONS, new ExpressionNode.NodeFactory()
            );

    @Override
    public Void execute(Robot robot) {
        return subnode.execute(robot);
    }

    @Override
    protected void privateDoParse(Scanner scanner, Logger logger) {
        subnode.parse(scanner, logger);
        require(";", scanner, ParserFailureType.WRONG_MIDDLE_OR_END_OF_NODE);
    }

    @Override
    protected String privateToCode() {
        return subnode.toString() + ";";
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

