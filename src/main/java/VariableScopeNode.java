import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by Dylan on 20/05/17.
 */
public interface VariableScopeNode<T> extends ParsableNode<T> {

    static VariableScopeNode<?> getClosestScopeNode(ParsableNode<?> node)
            throws ScopeNotFoundException {
        if (node == null)
            throw new NullPointerException("Null argument not allowed");

        ParsableNode<?> nodeOrParent = node;
        while (true) {
            if (nodeOrParent == null)
                throw new ScopeNotFoundException();

            nodeOrParent = nodeOrParent.getParentNode();

            if (nodeOrParent instanceof VariableScopeNode<?>)
                return (VariableScopeNode<?>) nodeOrParent;
        }
    }

    static <ScopeT extends Scope> ScopeT getClosestScope(
            ParsableNode<?> node,
            String variableName,
            Function<VariableScopeNode<?>, ScopeT> scopeGetter)
            throws ScopeNotFoundException {

        VariableScopeNode<?> scopeNode = getClosestScopeNode(node);

        while (true) {
            ScopeT scope = scopeGetter.apply(scopeNode);
            if (scope.containsKey(variableName))
                return scope;

            scopeNode = getClosestScopeNode(scopeNode);
        }
    }

    CompilationScope getCompilationScope();

    ExecutionScope getExecutionScope();

    abstract class Scope {
        private final Map<String, Integer> variableNameToValue = new HashMap<>();

        public void declareVariable(String variableName) {
            if (variableNameToValue.containsKey(variableName)) {
                throwError(variableName, ParserFailureType.ALREADY_DEFINED_VARIABLE);
                return;
            }
            variableNameToValue.put(variableName, null);
        }

        public Integer getValue(String variableName) {
            if (!variableNameToValue.containsKey(variableName)) {
                throwError(
                        variableName,
                        ParserFailureType.UNDEFINED_VARIBLE_ACCESS
                );
                return null;
            }

            // TODO unassigned?
            Integer value = variableNameToValue.get(variableName);
            if (value == null) throw new NullPointerException(
                    "Accessing unassigned variable: " + variableName
            );
            return value;
        }

        public Integer putValue(String variableName, Integer value) {
            if (!variableNameToValue.containsKey(variableName)) {
                throwError(
                        variableName,
                        ParserFailureType.UNDEFINED_VARIBLE_ASSIGNMENT
                );
                return null;
            }

            return variableNameToValue.put(variableName, value);
        }

        public boolean containsKey(String variableName) {
            return variableNameToValue.containsKey(variableName);
        }

        protected abstract void throwError(String variableName,
                                           ParserFailureType type);
    }

    class CompilationScope extends Scope {
        public static void throwScopeError(String variableName,
                                           ParserFailureType type) {
            throw new ParserFailureException(
                    "Error for variable '" + variableName + "'",
                    null,
                    type
            );
        }

        @Override
        protected void throwError(String variableName, ParserFailureType type) {
            throwScopeError(variableName, type);
        }
    }

    class ExecutionScope extends Scope {
        public static void throwScopeError(String variableName,
                                           ParserFailureType type) {
            throw new IllegalStateException(String.format(
                    "Error for variable '%s' (%s)",
                    variableName, type.toString()
            ));
        }

        @Override
        protected void throwError(String variableName, ParserFailureType type) {
            throwScopeError(variableName, type);
        }
    }

    class ScopeNotFoundException extends Exception {
    }

}
