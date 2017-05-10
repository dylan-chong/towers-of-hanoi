import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * Created by Dylan on 8/05/17.
 */
public class ProgramNode extends ParsableNode<Void> {
    private static Collection<Factory<StatementNode>> statementFactories;

    private List<StatementNode> expressions = new ArrayList<>();

    @Override
    public void execute(Robot robot) {

    }

    @Override
    protected void privateDoParse(Scanner scanner) {
        //todo expression
        List<Factory<StatementNode>> validFactories = statementFactories
                .stream()
                .filter(factory -> factory.canStartWith(scanner))
                .collect(Collectors.toList());
        if (validFactories.size() != 1) throw new ParserFailureException(
                String.format("Invalid number of valid factories (%d): %s",
                        validFactories.size(),
                        validFactories
                ),
                ParserFailureType.NON_ONE_MATCHES
        );
    }

    @Override
    public String toCode() {
        return null;
    }

    @Override
    public Void evaluate() {
        return null;
    }

}
