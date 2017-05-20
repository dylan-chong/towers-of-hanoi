import java.util.HashMap;
import java.util.Map;

/**
 * Created by Dylan on 20/05/17.
 */
public interface VariableScopeNode {
    int DEFAULT_VALUE = 0;

    static VariableScopeNode getClosestScopeNode(ParsableNode<?> node) {
        if (node == null)
            throw new NullPointerException("Null argument not allowed");

        ParsableNode<?> nodeOrParent = node;
        while (true) {
            if (nodeOrParent == null) return null;
            if (nodeOrParent instanceof VariableScopeNode)
                return (VariableScopeNode) nodeOrParent;

            nodeOrParent = nodeOrParent.getParentNode();
        }
    }

    Scope getScope();

    class Scope {
        private final Map<String, Integer> variableNameToValue = new HashMap<>();

        public Integer getValue(String variableName) {
            return variableNameToValue.getOrDefault(variableName, DEFAULT_VALUE);
        }

        public Integer putValue(String variableName, Integer value) {
            return variableNameToValue.put(variableName, value);
        }
    }
}
