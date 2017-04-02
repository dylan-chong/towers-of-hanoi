package junit.mapdata.model;

import main.mapdata.roads.Restriction;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by Dylan on 2/04/17.
 */
public class MapDataParserTest_Restriction {
    @Test
    public void parseRestrictions_justHeaderLine_returnsEmpty() {
        List<Restriction> restrictions = ParserUtils.getRestrictionsFromString(
                "NodeID\tRoadID\tNodeID\tRoadID\tNodeID\n"
        );
        assertEquals(Collections.emptyList(), restrictions);
    }

    @Test
    public void parseRestrictions_oneLine_returnsOneItem() {
        List<Restriction> restrictions = ParserUtils.getRestrictionsFromString(
                "NodeID\tRoadID\tNodeID\tRoadID\tNodeID\n" +
                        "1\t2\t3\t4\t5"
        );
        assertEquals(
                Collections.singletonList(new Restriction(1, 2, 3, 4, 5)),
                restrictions
        );
    }

    @Test
    public void parseRestrictions_oneItemWithEmptyLine_returnsOneItem() {
        List<Restriction> restrictions = ParserUtils.getRestrictionsFromString(
                "NodeID\tRoadID\tNodeID\tRoadID\tNodeID\n" +
                        "1\t2\t3\t4\t5\n"
        );
        assertEquals(
                Collections.singletonList(new Restriction(1, 2, 3, 4, 5)),
                restrictions
        );
    }
}
