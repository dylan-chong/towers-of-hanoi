package junit;

import main.LatLong;
import main.Node;
import main.DataParser;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.util.*;

import static org.junit.Assert.assertEquals;

/**
 * Created by Dylan on 3/01/17.
 *
 * Use naming format methodName_state_expectedResult for tests
 */
public class DataParserTest {
    @Test
    public void parse_emptyScanner_returnsEmpty() {
        assertEquals(Collections.emptyList(), getNodesFromString(""));
    }

    @Test
    public void parse_singleLine_returnsOneNode() {
        assertEquals(
                Arrays.asList(
                        new Node(5, new LatLong(-7, 9))
                ),
                getNodesFromString("5\t-7\t9\n")
        );
    }

    @Test
    public void parse_singleLineWithDecimals_returnsOneNode() {
        assertEquals(
                Arrays.asList(
                        new Node(2, new LatLong(-99, 0.123))
                ),
                getNodesFromString("2\t-99\t0.123\n")
        );
    }

    @Test
    public void parse_2Lines_returns2Nodes() {
        assertEquals(
                Arrays.asList(
                        new Node(2, new LatLong(-99, 0.123)),
                        new Node(123, new LatLong(456, 7890))
                ),
                getNodesFromString("2\t-99\t0.123\n" +
                        "123\t456\t7890")
        );
    }

    @Test
    public void parse_2LinesEndingWithLineSpace_returns2Nodes() {
        assertEquals(
                Arrays.asList(
                        new Node(2, new LatLong(-99, 0.123)),
                        new Node(123, new LatLong(456, 7890))
                ),
                getNodesFromString("2\t-99\t0.123\n" +
                        "123\t456\t7890\n")
        );
    }

    private List<Node> getNodesFromString(String input) {
        Scanner scanner = new Scanner(
                new ByteArrayInputStream(input.getBytes())
        );
        DataParser parser = new DataParser();
        return new ArrayList<>(parser.parseNodes(scanner));
    }
}
