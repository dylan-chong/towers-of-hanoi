import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by Dylan on 16/05/17.
 */
public class SensorNode extends ExpressionNode {

    /**
     * Map of Pattern -> Robot function with no return value or params
     * <p>
     * This is only public for tests.
     */
    public static final Map<String, FunctionWrapper<ExpressionNode, Integer>> ALL_SENSORS;

    static {
        ALL_SENSORS = new HashMap<>();
        ALL_SENSORS.put("fuelLeft", new FunctionWrapper<>((robot, param) ->
                robot.getFuel()));
        ALL_SENSORS.put("oppLR", new FunctionWrapper<>((robot, param) ->
                robot.getOpponentLR()));
        ALL_SENSORS.put("oppFB", new FunctionWrapper<>((robot, param) ->
                robot.getOpponentFB()));
        ALL_SENSORS.put("numBarrels", new FunctionWrapper<>((robot, param) ->
                robot.numBarrels()));
        ALL_SENSORS.put("wallDist", new FunctionWrapper<>((robot, param) ->
                robot.getDistanceToWall()));
        ALL_SENSORS.put("barrelLR", new FunctionWrapper<>(true, (robot, param) -> {
            int n = param == null ? 0 : param.execute(robot);
            return robot.getBarrelLR(n);
        }));
        ALL_SENSORS.put("barrelFB", new FunctionWrapper<>(true, (robot, param) -> {
            int n = param == null ? 0 : param.execute(robot);
            return robot.getBarrelFB(n);
        }));
    }

    private final DecorableOptionalParamNode<ExpressionNode, Integer> subnode =
            new DecorableOptionalParamNode<>(
                    this,
                    ALL_SENSORS,
                    new ExpressionNode.NodeFactory()
            );

    public SensorNode(ParsableNode<?> parentNode) {
        super(parentNode);
    }

    @Override
    public Integer execute(Robot robot) {
        return subnode.execute(robot);
    }

    @Override
    protected void privateDoParse(Scanner scanner, Logger logger) {
        subnode.parse(scanner, logger);
    }

    @Override
    protected String privateToCode() {
        return subnode.toString();
    }

    public static class NodeFactory implements Factory<SensorNode> {
        @Override
        public SensorNode create(Scanner scannerNotToBeModified,
                                 ParsableNode<?> parentNode) {
            return new SensorNode(parentNode);
        }

        @Override
        public boolean canStartWith(Scanner scanner) {
            return ALL_SENSORS.entrySet()
                    .stream()
                    .anyMatch(entry -> scanner.hasNext(entry.getKey()));
        }
    }
}

