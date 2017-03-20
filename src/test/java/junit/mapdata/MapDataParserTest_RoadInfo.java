package junit.mapdata;

import junit.TestUtils;
import main.mapdata.RoadInfo;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

/**
 * Created by Dylan on 15/03/17.
 */
public class MapDataParserTest_RoadInfo {

    @Test
    public void parseRoadInfo_emptyScanner_returnsEmpty() {
        Assert.assertEquals(Collections.emptyList(), TestUtils.getRoadInfoFromString("" ));
    }

    @Test
    public void parseRoadInfo_onlyWithHeaders_returnsEmpty() {
        assertEquals(
                Collections.emptyList(),
                TestUtils.getRoadInfoFromString(
                        "roadid\ttype\tlabel    \tcity    \toneway\tspeed\troadclass\tnotforcar\tnotforpede\tnotforbicy\n"
                )
        );
    }

    @Test
    public void parseRoadInfo_singleRoadInfoWithoutSpaces_returnsOneRoadInfo() {
        assertEquals(
                Collections.singletonList(
                        new RoadInfo(
                                1,
                                2,
                                "Label", "City",
                                false,
                                RoadInfo.SpeedLimit.values()[3],
                                RoadInfo.RoadClass.values()[4],
                                false, false, false
                        )
                ),
                TestUtils.getRoadInfoFromString(
                        "roadid\ttype\tlabel    \tcity    \toneway\tspeed\troadclass\tnotforcar\tnotforpede\tnotforbicy\n" +
                                "1\t2\tLabel\tCity\t0\t3\t4\t0\t0\t0"
                )
        );
    }

    @Test
    public void parseRoadInfo_singleRoadInfoWithSpaces_returnsOneRoadInfo() {
        assertEquals(
                Collections.singletonList(
                        new RoadInfo(
                                1,
                                2,
                                "Label Word2", "City Another Word",
                                false,
                                RoadInfo.SpeedLimit.values()[3],
                                RoadInfo.RoadClass.values()[4],
                                false, false, false
                        )
                ),
                TestUtils.getRoadInfoFromString(
                        "roadid\ttype\tlabel    \tcity    \toneway\tspeed\troadclass\tnotforcar\tnotforpede\tnotforbicy\n" +
                                "1\t2\tLabel Word2\tCity Another Word\t0\t3\t4\t0\t0\t0"
                )
        );
    }

    @Test
    public void parseRoadInfo_twoRoadInfos_returnsTwoRoadInfo() {
        assertEquals(
                Arrays.asList(
                        new RoadInfo(
                                1,
                                2,
                                "Label Word2", "City Another Word",
                                false,
                                RoadInfo.SpeedLimit.values()[3],
                                RoadInfo.RoadClass.values()[4],
                                false, false, false
                        ),
                        new RoadInfo(
                                9,
                                8,
                                "Second Label", "Second City",
                                true,
                                RoadInfo.SpeedLimit.values()[7],
                                RoadInfo.RoadClass.values()[4],
                                true, true, true
                        )

                ),
                TestUtils.getRoadInfoFromString(
                        "roadid\ttype\tlabel    \tcity    \toneway\tspeed\troadclass\tnotforcar\tnotforpede\tnotforbicy\n" +
                                "1\t2\tLabel Word2\tCity Another Word\t0\t3\t4\t0\t0\t0\n" +
                                "9\t8\tSecond Label\tSecond City\t1\t7\t4\t1\t1\t1\n"
                )
        );
    }
}
