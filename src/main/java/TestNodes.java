import org.junit.Test;

import java.io.StringReader;
import java.util.Scanner;
import java.util.function.Supplier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by Dylan on 8/05/17.
 */
public class TestNodes {

    /*
     ************************* Number *************************
     */

    @Test
    public void parseNumber_withPositiveInteger_evaluatesToSameNumber() {
        NodeTesters.NUMBER.testParseNode("3", "3");
    }

    @Test
    public void parseNumber_withNegativeInteger_evaluatesToSameNumber() {
        NodeTesters.NUMBER.testParseNode("-9", "-9");
    }

    @Test
    public void parseNumber_withSpaces_ignoresSpaces() {
        NodeTesters.NUMBER.testParseNode("  4  ", "4");
    }

    @Test
    public void parseNumber_withMultipleDigits_getsInteger() {
        NodeTesters.NUMBER.testParseNode("-987", "-987");
    }

    @Test
    public void parseNumber_withNegativeInteger_getsInteger() {
        NodeTesters.NUMBER.testParseNode("-9", "-9");
    }

    @Test
    public void parseNumber_withSpaceAfterMinus_fails() {
        NodeTesters.NUMBER.testParseNodeFails("- 9", ParserFailureType.NUMBER_FORMAT);
    }

    /*
     ************************* Add *************************
     */

    @Test
    public void parseAdd_withAdd1And2NoSpaces_parsesCorrectly() {
        NodeTesters.ADD.testParseNode("add(1,2)", "add(1,2)");
    }

    @Test
    public void parseAdd_withAdd1And2WithSpaces_parsesCorrectly() {
        NodeTesters.ADD.testParseNode("  add  (  1  ,  2  )  ", "add(1,2)");
    }

    @Test
    public void parseAdd_multipleDigitAndNegativeNums_parsesCorrectly() {
        NodeTesters.ADD.testParseNode("  add  (  10  ,  -2  )  ", "add(10,-2)");
    }

    @Test
    public void parseAdd_missingBracket_error() {
        NodeTesters.ADD.testParseNodeFails(
                "add 10,-2)", ParserFailureType.WRONG_MIDDLE_OR_END_OF_NODE
        );
    }

    /*
     ************************* Action *************************
     */

    @Test
    public void parseAction_turnL_actionIsRecognised() {
        NodeTesters.ACTION.testParseNode("turnL;", "turnL;");
    }

    @Test
    public void parseAction_turnR_actionIsRecognised() {
        NodeTesters.ACTION.testParseNode("turnR;", "turnR;");
    }

    @Test
    public void parseAction_unknownAction_error() {
        NodeTesters.ACTION.testParseNodeFails(
                "someUnknownAction;",
                ParserFailureType.NON_ONE_MATCHES
        );
    }

    @Test
    public void parseAction_noSemicolon_error() {
        NodeTesters.ACTION.testParseNodeFails(
                "turnL",
                ParserFailureType.WRONG_MIDDLE_OR_END_OF_NODE
        );
    }

    /*
     ************************* ProgramNode *************************
     */

    @Test
    public void parseProgram_programWith1Action_parsesCorrectly() {
        NodeTesters.PROGRAM.testParseNode("turnAround;", "turnAround;");
    }

    @Test
    public void parseProgram_programWith2Actions_parsesCorrectly() {
        NodeTesters.PROGRAM.testParseNode("turnL;turnR;", "turnL;turnR;");
    }

    @Test
    public void parseProgram_missingSemicolon_parsesCorrectly() {
        NodeTesters.PROGRAM.testParseNodeFails(
                "turnL turnR;",
                ParserFailureType.WRONG_MIDDLE_OR_END_OF_NODE
        );
    }

    /*
     ************************* BlockNode *************************
     */

    @Test
    public void parseBlock_noStatements_error() {
        NodeTesters.BLOCK.testParseNodeFails(
                "{}", ParserFailureType.WRONG_NUMBER_OF_STATEMENTS
        );
    }

    @Test
    public void parseBlock_oneStatement_parsesCorrectly() {
        NodeTesters.BLOCK.testParseNode("{turnL;}", "{turnL;}");
    }

    @Test
    public void parseBlock_twoStatements_parsesCorrectly() {
        NodeTesters.BLOCK.testParseNode("{turnL;turnR;}", "{turnL;turnR;}");
    }

    @Test
    public void parseBlock_missingSemicolon_parsesCorrectly() {
        NodeTesters.BLOCK.testParseNodeFails(
                "{turnL}", ParserFailureType.WRONG_MIDDLE_OR_END_OF_NODE
        );
    }

    @Test
    public void parseBlock_withSpaces_parsesCorrectly() {
        NodeTesters.BLOCK.testParseNode(" { turnL ; } ", "{turnL;}");
    }

    @Test
    public void parseBlock_missingBrace_parsesCorrectly() {
        NodeTesters.BLOCK.testParseNodeFails(
                "{turnL;", ParserFailureType.WRONG_MIDDLE_OR_END_OF_NODE
        );
    }

    /*
     ************************* Utils *************************
     */

    private enum NodeTesters {
        NUMBER(ExpressionNode.NumberNode::new),
        ACTION(StatementNode.ActionNode::new),
        PROGRAM(ProgramNode::new),
        BLOCK(BlockNode::new),
        ADD(ExpressionNode.OperationNode.AddNode::new);

        private final Supplier<ParsableNode<?>> nodeSupplier;

        NodeTesters(Supplier<ParsableNode<?>> nodeSupplier) {
            this.nodeSupplier = nodeSupplier;
        }

        public void testParseNode(String program, String expectedCode) {
            ParsableNode<?> node = newNodeWithInput(program);
            assertEquals(expectedCode, node.toString());
        }

        public void testParseNodeFails(String incorrectProgram,
                                       ParserFailureType errorType) {
            try {
                newNodeWithInput(incorrectProgram);
                fail();
            } catch (ParserFailureException exception) {
                assertEquals(errorType, exception.getType());
            }
        }

        private ParsableNode<?> newNodeWithInput(String program) {
            Scanner scanner = new Scanner(new StringReader(program));
            ParsableNode<?> node = nodeSupplier.get();
            node.parse(scanner);
            return node;
        }
    }
}
