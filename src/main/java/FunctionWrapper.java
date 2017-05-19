import java.util.function.BiFunction;

/**
 * Created by Dylan on 19/05/17.
 */

public class FunctionWrapper<ParamT extends ParsableNode<?>, EvalT> {
    public final BiFunction<Robot, ParamT, EvalT> function;
    public final boolean hasOptionalParam;

    public FunctionWrapper(BiFunction<Robot, ParamT, EvalT> function) {
        this(false, function);
    }

    public FunctionWrapper(boolean hasOptionalParam,
                           BiFunction<Robot, ParamT, EvalT> function) {
        this.function = function;
        this.hasOptionalParam = hasOptionalParam;
    }
}

