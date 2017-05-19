import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Dylan on 16/05/17.
 */
public class SensorNode extends ExpressionNode {



    // todo next make an optional param node decorable


    /**
     * Map of Pattern -> Robot function with no return value or params
     *
     * This is only public for tests.
     */
    public static final Map<String, Function<Robot, Integer>> ALL_SENSORS;

    static {
        ALL_SENSORS = new HashMap<>();
        ALL_SENSORS.put("fuelLeft", Robot::getFuel);
        ALL_SENSORS.put("oppLR", Robot::getOpponentLR);
        ALL_SENSORS.put("oppFB", Robot::getOpponentFB);
        ALL_SENSORS.put("numBarrels", Robot::numBarrels);
        // ALL_SENSORS.put("barrelLR", Robot::getBarrelLR);
        // TODO: NEXT above param?
    }

    private Map.Entry<String, Function<Robot, Integer>> actionEntry;

    @Override
    protected void privateDoParse(Scanner scanner, Logger logger) {
        List<Map.Entry<String, Function<Robot, Integer>>> validActions = ALL_SENSORS
                .entrySet()
                .stream()
                .filter(entry -> scanner.hasNext(entry.getKey()))
                .collect(Collectors.toList());
        requireOnlyOne(validActions, scanner);

        actionEntry = validActions.get(0);

        String sensorName = actionEntry.getKey();
        require(sensorName, scanner, ParserFailureType.WRONG_NODE_START);
    }

    @Override
    public String privateToCode() {
        return actionEntry.getKey() + ';';
    }

    @Override
    public Integer execute(Robot robot) {
        return actionEntry.getValue().apply(robot);
    }

    public static class NodeFactory implements Factory<SensorNode> {
        @Override
        public SensorNode create(Scanner scannerNotToBeModified) {
            return new SensorNode();
        }

        @Override
        public boolean canStartWith(Scanner scanner) {
            return ALL_SENSORS.entrySet()
                    .stream()
                    .anyMatch(entry -> scanner.hasNext(entry.getKey()));
        }
    }
}

