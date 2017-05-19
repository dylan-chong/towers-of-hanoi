import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * Created by Dylan on 19/05/17.
 * <p>
 * Doesn't represent an actual load in the grammar, so this should be wrapped
 * by another {@link ParsableNode} class. This is used for reducing
 * code duplication.
 */
public class DecorableOptionalParamNode<ParamT extends ParsableNode<?>, EvalT>
        extends ParsableNode<EvalT> {

    private final Map<String, FunctionWrapper<ParamT, EvalT>> keyToFunction;
    private final Factory<? extends ParamT> nodeFactory;

    private String functionName;
    private FunctionWrapper<ParamT, EvalT> functionWrapper;
    private ParamT param;

    public DecorableOptionalParamNode(Map<String, FunctionWrapper<ParamT, EvalT>> keyToFunction,
                                      Factory<? extends ParamT> nodeFactory) {
        this.keyToFunction = keyToFunction;
        this.nodeFactory = nodeFactory;
    }

    @Override
    protected void privateDoParse(Scanner scanner, Logger logger) {
        List<Map.Entry<String, FunctionWrapper<ParamT, EvalT>>> matches = keyToFunction
                .entrySet()
                .stream()
                .filter(entry -> scanner.hasNext(entry.getKey()))
                .collect(Collectors.toList());
        requireOnlyOne(matches, scanner);

        Map.Entry<String, FunctionWrapper<ParamT, EvalT>> match = matches.get(0);
        functionName = match.getKey();
        functionWrapper = match.getValue();

        require(functionName, scanner, ParserFailureType.WRONG_NODE_START);

        // Optional param (valid syntax: 'name(EXPR)' or 'name')
        if (functionWrapper.hasOptionalParam && scanner.hasNext("\\(")) {
            require("\\(", scanner, ParserFailureType.WRONG_MIDDLE_OR_END_OF_NODE);
            param = nodeFactory.create(scanner);
            param.parse(scanner, logger);
            require("\\)", scanner, ParserFailureType.WRONG_MIDDLE_OR_END_OF_NODE);
        }
    }

    @Override
    protected String privateToCode() {
        StringBuilder stringBuilder = new StringBuilder(functionName);

        if (param == null) return stringBuilder.toString();

        return stringBuilder
                .append('(')
                .append(param.toString())
                .append(')')
                .toString();
    }

    @Override
    public EvalT execute(Robot robot) {
        return functionWrapper.function.apply(robot, param);
    }
}
