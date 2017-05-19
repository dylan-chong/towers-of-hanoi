import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.function.BiFunction;

/**
 * Created by Dylan on 19/05/17.
 * <p>
 * Doesn't represent an actual load in the grammar, so this should be wrapped
 * by another {@link ParsableNode} class. This is used for reducing
 * code duplication.
 */
public class DecorableOptionalParamNode<EvalT> extends ParsableNode<EvalT> {

    private final Map<String, FunctionWrapper<EvalT>> keyToFunction;

    private FunctionWrapper<EvalT> functionWrapper;
    private List<ExpressionNode> params;

    public DecorableOptionalParamNode(Map<String, FunctionWrapper<EvalT>> keyToFunction) {
        this.keyToFunction = keyToFunction;
    }

    @Override
    protected void privateDoParse(Scanner scanner, Logger logger) {

    }

    @Override
    protected String privateToCode() {
        return null;
    }

    @Override
    public EvalT execute(Robot robot) {
        return functionWrapper.function.apply(robot, params);
    }

    public static class FunctionWrapper<EvalT> {
        public final BiFunction<Robot, List<ExpressionNode>, EvalT> function;
        public final boolean hasOptionalParam;

        public FunctionWrapper(BiFunction<Robot, List<ExpressionNode>, EvalT> function) {
            this(false, function);
        }

        public FunctionWrapper(
                boolean hasOptionalParam,
                BiFunction<Robot, List<ExpressionNode>, EvalT> function) {
            this.function = function;
            this.hasOptionalParam = hasOptionalParam;
        }
    }
}
