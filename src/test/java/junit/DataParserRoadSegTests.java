package junit;

import main.LatLong;
import main.RoadSegment;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

/**
 * Created by Dylan on 15/03/17.
 * <p>
 * Tests the loading of road segments
 */
public class DataParserRoadSegTests {
    @Test
    public void parseRoadSegments_withOnlyHeaders_returnsEmpty() {
        Assert.assertEquals(
                Collections.emptyList(),
                TestUtils.getRoadSegmentsFromString(
                        "roadID\tlength\tnodeID1\tnodeID2\tcoords"
                )
        );
    }

    @Test
    public void parseRoadSegments_oneSegmentWithTwoPoints_returnsOneSegment() {
        Assert.assertEquals(
                Collections.singletonList(
                        new RoadSegment(1, 2, 3, 4, Arrays.asList(
                                new LatLong(5, 6), new LatLong(7, 8))
                        )
                ),
                TestUtils.getRoadSegmentsFromString(
                       "roadID\tlength\tnodeID1\tnodeID2\tcoords\n" +
                                "1\t2\t3\t4\t5\t6\t7\t8"
                )
        );
    }

    @Test
    public void parseRoadSegments_oneSegmentWithThreePoints_returnsOneSegment() {
        Assert.assertEquals(
                Collections.singletonList(
                        new RoadSegment(1, 2, 3, 4, Arrays.asList(
                                new LatLong(5, 6),
                                new LatLong(7, 8),
                                new LatLong(9, 10)
                        ))
                ),
                TestUtils.getRoadSegmentsFromString(
                        "roadID\tlength\tnodeID1\tnodeID2\tcoords\n" +
                                "1\t2\t3\t4\t5\t6\t7\t8\t9\t10"
                )
        );
    }

    @Test
    public void parseRoadSegments_twoSegments_returnsTwoSegments() {
        Assert.assertEquals(
                Arrays.asList(
                        new RoadSegment(1, 2, 3, 4, Arrays.asList(
                                new LatLong(5, 6),
                                new LatLong(7, 8)
                        )),
                        new RoadSegment(9, 8, 7, 6, Arrays.asList(
                                new LatLong(5, 4),
                                new LatLong(3, 2)
                        ))

                ),
                TestUtils.getRoadSegmentsFromString(
                        "roadID\tlength\tnodeID1\tnodeID2\tcoords\n" +
                                "1\t2\t3\t4\t5\t6\t7\t8\n" +
                                "9\t8\t7\t6\t5\t4\t3\t2"
                )
        );
    }
}
