import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Dylan on 21/05/17.
 */
public class DeclarationNode extends StatementNode {
    private static final String VARS_KEYWORD = "vars";

    private List<String> variableNames;

    public DeclarationNode(ParsableNode<?> parentNode) {
        super(parentNode);
    }

    @Override
    public Void execute(Robot robot) {
        for (String variableName : variableNames) {
            try {
                VariableScopeNode.getClosestScopeNode(this)
                        .getExecutionScope()
                        .declareVariable(variableName);
            } catch (VariableScopeNode.ScopeNotFoundException e) {
                throw new IllegalStateException("No VariableScopeNode exists");
            }
        }
        return null;
    }

    @Override
    protected void privateDoParse(Scanner scanner, Logger logger) {
        require(VARS_KEYWORD, scanner, ParserFailureType.WRONG_NODE_START);
        variableNames = new ArrayList<>();
        parseOneVariable(scanner);

        while (scanner.hasNext(",")) {
            require(",", scanner, ParserFailureType.WRONG_MIDDLE_OR_END_OF_NODE);
            parseOneVariable(scanner);
        }

        require(";", scanner, ParserFailureType.WRONG_MIDDLE_OR_END_OF_NODE);
    }

    @Override
    protected String privateToCode() {
        StringBuilder stringBuilder = new StringBuilder(VARS_KEYWORD);
        stringBuilder.append(' ');

        for (int i = 0; i < variableNames.size(); i++) {
            stringBuilder.append(variableNames.get(i));
            if (i < variableNames.size() - 1) stringBuilder.append(',');
        }

        return stringBuilder
                .append(';')
                .toString();
    }

    private void parseOneVariable(Scanner scanner) {
        String variableName = require(
                VariableAccessNode.VARIABLE_PATTERN,
                scanner,
                ParserFailureType.VARIABLE_FORMAT
        );
        try {
            VariableScopeNode.getClosestScopeNode(this)
                    .getCompilationScope()
                    .declareVariable(variableName);
        } catch (VariableScopeNode.ScopeNotFoundException e) {
            throw new IllegalStateException("No VariableScopeNode exists");
        }

        variableNames.add(variableName);
    }

    public static class NodeFactory implements Factory<DeclarationNode> {

        @Override
        public DeclarationNode create(Scanner scannerNotToBeModified,
                                      ParsableNode<?> parentNode) {
            return new DeclarationNode(parentNode);
        }

        @Override
        public boolean canStartWith(Scanner scannerNotToBeModified) {
            return scannerNotToBeModified.hasNext(VARS_KEYWORD);
        }
    }
}
