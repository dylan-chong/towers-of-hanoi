import org.junit.Test;

import java.io.StringReader;
import java.util.Scanner;

import static org.junit.Assert.assertEquals;

/**
 * Created by Dylan on 8/05/17.
 */
public class TestParsableNode {

    @Test
    public void parseNumber_withPositiveInteger_evaluatessToSameNumber() {
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
    public void parseNumber_withNegativeInteger_getsInteger() {
        testParseNumber("-9", "-9");
    }

    @Test
    public void parseProgram_withAdd1And2NoSpaces_parsesCorrectly() {
    }

    private void testParseNumber(String input,
                                 String expectedToStringValue) {
        Scanner scanner = new Scanner(new StringReader(input));
        ParsableNode.NumberNode node = new ParsableNode.NumberNode();
        node.parseFrom(scanner);
        assertEquals(expectedToStringValue, node.toString());
    }
}



