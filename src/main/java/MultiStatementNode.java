import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Dylan on 12/05/17.
 *
 * Shouldn't be instantiated directly because this doesn't directly represent
 * something in the grammar.
 */
public abstract class MultiStatementNode extends ParsableNode<Void> {
    private final int minStatements;
    private final String startPattern; // e.g. "\\{"
    private final String endPattern;

    /**
     * These are set when parsing
     */
    private String startMatch = "";
    private String endMatch = "";

    private List<StatementNode> statements = new ArrayList<>();

    public MultiStatementNode(int minStatements) {
        this.minStatements = minStatements;
        this.startPattern = "";
        this.endPattern = "";
    }

    public MultiStatementNode(int minStatements,
                              String startPattern,
                              String endPattern) {
        this.minStatements = minStatements;
        this.startPattern = startPattern;
        this.endPattern = endPattern;
    }

    @Override
    public void execute(Robot robot) {
        for (StatementNode expression : statements) {
            expression.execute(robot);
        }
    }

    @Override
    protected void privateDoParse(Scanner scanner) {
        if (!startPattern.isEmpty()) {
            startMatch = require(
                    startPattern, scanner, ParserFailureType.WRONG_NODE_START
            );
        }

        for (int i = 0; i < minStatements; i++) {
            if (endPattern.isEmpty() || scanner.hasNext(endPattern)) {
                throwParseError(
                        String.format(
                                "Expected more than %d statements (%d)",
                                i, minStatements
                        ),
                        scanner,
                        ParserFailureType.WRONG_NUMBER_OF_STATEMENTS
                );
            }
            parseOneExpression(scanner);
        }

        while (scanner.hasNext()) {
            if (!endPattern.isEmpty() && scanner.hasNext(endPattern)) {
                break;
            }
            parseOneExpression(scanner);
        }

        if (!endPattern.isEmpty()) {
            endMatch = require(
                    endPattern, scanner, ParserFailureType.WRONG_MIDDLE_OR_END_OF_NODE
            );
        }
    }

    @Override
    public String privateToCode() {
        StringBuilder code = new StringBuilder(startMatch);
        for (StatementNode expression : statements) {
            code.append(expression.toString());
        }
        code.append(endMatch);
        return code.toString();
    }

    @Override
    public Void evaluate() {
        return null;
    }

    private void parseOneExpression(Scanner scanner) {
        StatementNode.NodeFactory factory = new StatementNode.NodeFactory();
        StatementNode statementNode = factory.create(scanner);
        statementNode.parse(scanner);
        statements.add(statementNode);
    }
}
