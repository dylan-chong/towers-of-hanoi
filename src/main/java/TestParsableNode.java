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
            testParseNumber("- 9", "We don't need an expected value for a fail");
            fail();
        } catch (ParserFailureException exception) {
            assertEquals(exception.getType(), ParserFailureType.NUMBER_FORMAT);
        }
    }

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

    @Test
    public void TEMP_TEST() {
        // TODO remove me
        // Scanner scanner = new Scanner(new StringReader("add(1,2)"));
        // scanner.useDelimiter("\\s+|(?=[(),])|(?<=[(),])");
    }


    private void testParseNumber(String input,
                                 String expectedToStringValue) {
        testParseNode(input, expectedToStringValue, Nodes.NumberNode::new);
    }

    private void testParseAdd(String input,
                              String expectedToStringValue) {
        testParseNode(input, expectedToStringValue, Nodes.AddNode::new);
    }

    private void testParseNode(String input,
                               String expectedToStringValue,
                               Supplier<ParsableNode<?>> nodeSupplier) {
        Scanner scanner = new Scanner(new StringReader(input));
        ParsableNode<?> node = nodeSupplier.get();
        node.parse(scanner);
        assertEquals(expectedToStringValue, node.toString());
    }
}



