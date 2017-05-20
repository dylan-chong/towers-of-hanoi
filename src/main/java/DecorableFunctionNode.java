import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.BiFunction;

/**
 * Created by Dylan on 19/05/17.
 *
 * Represents 'functionNamePattern(arg1,arg2)`
 */
public class DecorableFunctionNode<ParamT extends ParsableNode<?>, EvalT>
        extends AbstractParsableNode<EvalT> {

    private final String functionNamePattern;
    private final int numParams;
    private final BiFunction<Robot, List<ParamT>, EvalT> executor;
    private final Factory<? extends ParamT> paramFactory;

    private List<ParamT> params;

    public DecorableFunctionNode(
            String functionNamePattern,
            int numParams,
            BiFunction<Robot, List<ParamT>, EvalT> executor,
            Factory<? extends ParamT> paramFactory) {

        if (functionNamePattern.isEmpty())
            throw new IllegalArgumentException("functionNamePattern cannot be empty");

        this.functionNamePattern = functionNamePattern;
        this.numParams = numParams;
        this.executor = executor;
        this.paramFactory = paramFactory;
    }

    @Override
    public EvalT execute(Robot robot) {
        return executor.apply(robot, params);
    }

    @Override
    protected void privateDoParse(Scanner scanner, Logger logger) {
        require(functionNamePattern, scanner, ParserFailureType.WRONG_NODE_START);
        require("\\(", scanner, ParserFailureType.WRONG_MIDDLE_OR_END_OF_NODE);

        params = new ArrayList<>();
        for (int i = 0; i < numParams; i++) {
            ParamT node = paramFactory.create(scanner);
            node.parse(scanner, logger);
            params.add(node);

            if (i < numParams - 1) require(
                    ",", scanner, ParserFailureType.WRONG_MIDDLE_OR_END_OF_NODE
            );
        }

        require("\\)", scanner, ParserFailureType.WRONG_MIDDLE_OR_END_OF_NODE);
    }

    @Override
    protected String privateToCode() {
        StringBuilder stringBuilder = new StringBuilder(functionNamePattern);
        stringBuilder.append('(');
        for (int i = 0; i < numParams; i++) {
            stringBuilder.append(params.get(i).toString());
            if (i < numParams - 1) stringBuilder.append(',');
        }
        stringBuilder.append(')');
        return stringBuilder.toString();
    }
}
