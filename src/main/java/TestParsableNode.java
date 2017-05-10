import org.junit.Test;

import java.io.StringReader;
import java.util.Scanner;
import java.util.function.Supplier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by Dylan on 8/05/17.
 */
public class TestParsableNode {

    /*
     ************************* Number *************************
     */

    @Test
    public void parseNumber_withPositiveInteger_evaluatesToSameNumber() {
        testParseNumber("3", "3");
    }

    @Test
    public void parseNumber_withNegativeInteger_evaluatesToSameNumber() {
        testParseNumber("-9", "-9");
    }

    @Test
    public void parseNumber_withSpaces_ignoresSpaces() {
        testParseNumber("  4  ", "4");
    }

    @Test
    public void parseNumber_withMultipleDigits_getsInteger() {
        testParseNumber("-987", "-987");
    }

    @Test
    public void parseNumber_withNegativeInteger_getsInteger() {
        testParseNumber("-9", "-9");
    }

    @Test
    public void parseNumber_withSpaceAfterMinus_fails() {
        try {
            testParseNumber("- 9", null);
            fail();
        } catch (ParserFailureException exception) {
            assertEquals(exception.getType(), ParserFailureType.NUMBER_FORMAT);
        }
    }

    private void testParseNumber(String input, String expectedToStringValue) {
        testParseNode(input, expectedToStringValue, NumberNode::new);
    }

    /*
     ************************* Add *************************
     */

    @Test
    public void parseAdd_withAdd1And2NoSpaces_parsesCorrectly() {
        testParseAdd("add(1,2)", "add(1,2)");
    }

    @Test
    public void parseAdd_withAdd1And2WithSpaces_parsesCorrectly() {
        testParseAdd("  add  (  1  ,  2  )  ", "add(1,2)");
    }

    @Test
    public void parseAdd_multipleDigitAndNegativeNums_parsesCorrectly() {
        testParseAdd("  add  (  10  ,  -2  )  ", "add(10,-2)");
    }

    private void testParseAdd(String input, String expectedToStringValue) {
        testParseNode(input, expectedToStringValue, NumberNode.AddNode::new);
    }

    /*
     ************************* Action *************************
     */

    @Test
    public void parseAction_turnL_actionIsRecognised() {
        testParseAction("turnL;", "turnL;");
    }

    @Test
    public void parseAction_turnR_actionIsRecognised() {
        testParseAction("turnR;", "turnR;");
    }

    @Test
    public void parseAction_unknownAction_error() {
        try {
            testParseAction("someUnknownAction;", null);
            fail();
        } catch (ParserFailureException exception) {
            assertEquals(exception.getType(), ParserFailureType.NON_ONE_MATCHES);
        }
    }

    @Test
    public void parseAction_noSemicolon_error() {
        try {
            testParseAction("turnL", null);
            fail();
        } catch (ParserFailureException exception) {
            assertEquals(exception.getType(), ParserFailureType.WRONG_MIDDLE_OR_END_OF_NODE);
        }
    }

    private void testParseAction(String input, String expectedToStringValue) {
        testParseNode(input, expectedToStringValue, StatementNode.ActionNode::new);
    }

    private void testParseNode(String input,
                               String expectedToStringValue,
                               Supplier<ParsableNode<?>> nodeSupplier) {
        Scanner scanner = new Scanner(new StringReader(input));
        ParsableNode<?> node = nodeSupplier.get();
        node.parse(scanner);
        assertEquals(expectedToStringValue, node.toString());
    }

    @Test
    public void TEMP_TEST() {
        // TODO remove me
        Scanner scanner = new Scanner(new StringReader("add(1,2)"));
        scanner.useDelimiter("(?=.)|(?<=.)");
        while (scanner.hasNext()) {
            System.out.println(scanner.next());
        }
        fail();
    }

}
