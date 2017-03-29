package junit.mapdata.location;

import main.mapdata.location.LatLong;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by Dylan on 29/03/17.
 */
public class LatLongTest {
    @Test
    public void estimatedDistanceInKmTo_samePoint_returns0() {
        assertEquals(
                new LatLong(10, 15).estimatedDistanceInKmTo(new LatLong(10, 15)),
                0,
                0
        );
    }

    @Test
    public void estimatedDistanceInKmTo_halfwayAroundEarth_returns20000() {
        assertEquals(
                new LatLong(0, 0).estimatedDistanceInKmTo(new LatLong(180, 0)),
                20000,
                300
        );
    }
}
