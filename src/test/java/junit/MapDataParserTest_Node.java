package junit;

import main.LatLong;
import main.mapdata.Node;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

/**
 * Created by Dylan on 3/01/17.
 */
public class MapDataParserTest_Node {

    @Test
    public void parseNodes_emptyScanner_returnsEmpty() {
        assertEquals(Collections.emptyList(), TestUtils.getNodesFromString(""));
    }

    @Test
    public void parseNodes_singleLine_returnsOneNode() {
        assertEquals(
                Collections.singletonList(
                        new Node(5, new LatLong(-7, 9))
                ),
                TestUtils.getNodesFromString("5\t-7\t9\n")
        );
    }

    @Test
    public void parseNodes_singleLineWithDecimals_returnsOneNode() {
        assertEquals(
                Collections.singletonList(
                        new Node(2, new LatLong(-99, 0.123))
                ),
                TestUtils.getNodesFromString("2\t-99\t0.123\n")
        );
    }

    @Test
    public void parseNodes_2Lines_returns2Nodes() {
        assertEquals(
                Arrays.asList(
                        new Node(2, new LatLong(-99, 0.123)),
                        new Node(123, new LatLong(456, 7890))
                ),
                TestUtils.getNodesFromString("2\t-99\t0.123\n" +
                        "123\t456\t7890")
        );
    }

    @Test
    public void parseNodes_2LinesEndingWithLineSpace_returns2Nodes() {
        assertEquals(
                Arrays.asList(
                        new Node(2, new LatLong(-99, 0.123)),
                        new Node(123, new LatLong(456, 7890))
                ),
                TestUtils.getNodesFromString("2\t-99\t0.123\n" +
                        "123\t456\t7890\n")
        );
    }

}
