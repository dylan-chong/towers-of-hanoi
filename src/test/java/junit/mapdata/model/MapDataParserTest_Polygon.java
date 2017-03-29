package junit.mapdata.model;

import main.mapdata.location.LatLong;
import main.mapdata.Polygon;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by Dylan on 18/03/17.
 */
public class MapDataParserTest_Polygon {
    @Test
    public void parsePolygons_emptyInput_returnsEmpty() {
        List<Polygon> polygonsFromString = ParserUtils.getPolygonsFromString("");
        assertEquals(Collections.emptyList(), polygonsFromString);
    }

    @Test
    public void parsePolygons_singlePolygonWithMinimalInfo_returnsOne() {
        String input = "[POLYGON]\n" +
                "Type=0x7\n" +
                "EndLevel=2\n" +
                "CityIdx=32\n" +
                "Data0=(1,2)\n" +
                "[END]\n";
        List<Polygon> polygonsFromString = ParserUtils.getPolygonsFromString(input);
        assertEquals(
                Collections.singletonList(new Polygon(
                        "0x7",
                        null,
                        Collections.singletonList(new LatLong(1, 2)),
                        2,
                        32
                )),
                polygonsFromString
        );
    }

    @Test
    public void parsePolygons_singlePolygonWithLabel_returnsOne() {
        String input = "[POLYGON]\n" +
                "Type=0x7\n" +
                "EndLevel=2\n" +
                "Label=International stuff\n" +
                "CityIdx=32\n" +
                "Data0=(1,2)\n" +
                "[END]\n";
        List<Polygon> polygonsFromString = ParserUtils.getPolygonsFromString(input);
        assertEquals(
                Collections.singletonList(new Polygon(
                        "0x7",
                        "International stuff",
                        Collections.singletonList(new LatLong(1, 2)),
                        2,
                        32
                )),
                polygonsFromString
        );
    }

    @Test
    public void parsePolygons_singlePolygonWithDecimalCoords_returnsOne() {
        String input = "[POLYGON]\n" +
                "Type=0x7\n" +
                "EndLevel=2\n" +
                "Label=International stuff\n" +
                "CityIdx=32\n" +
                "Data0=(123.456,-987)\n" +
                "[END]\n";
        List<Polygon> polygonsFromString = ParserUtils.getPolygonsFromString(input);
        assertEquals(
                Collections.singletonList(new Polygon(
                        "0x7",
                        "International stuff",
                        Collections.singletonList(new LatLong(123.456, -987)),
                        2,
                        32
                )),
                polygonsFromString
        );
    }

    @Test
    public void parsePolygons_singlePolygonWithMultipleCoords_returnsOne() {
        String input = "[POLYGON]\n" +
                "Type=0x7\n" +
                "EndLevel=2\n" +
                "Label=International stuff\n" +
                "CityIdx=32\n" +
                "Data0=(1,2),(3,4)\n" +
                "[END]\n";
        List<Polygon> polygonsFromString = ParserUtils.getPolygonsFromString(input);
        assertEquals(
                Collections.singletonList(new Polygon(
                        "0x7",
                        "International stuff",
                        Arrays.asList(
                                new LatLong(1, 2),
                                new LatLong(3, 4),
                                new LatLong(1, 2)
                        ),
                        2,
                        32
                )),
                polygonsFromString
        );
    }

    @Test
    public void parsePolygons_twoPolygons_returnsTwo() {
        String input = "[POLYGON]\n" +
                "Type=0x1\n" +
                "EndLevel=2\n" +
                "Label=A\n" +
                "CityIdx=3\n" +
                "Data0=(4,5)\n" +
                "[END]\n" +
                "\n" +
                "[POLYGON]\n" +
                "Type=0x5\n" +
                "EndLevel=6\n" +
                "Label=B\n" +
                "CityIdx=7\n" +
                "Data0=(8,9)\n" +
                "[END]\n";
        List<Polygon> polygonsFromString = ParserUtils.getPolygonsFromString(input);
        assertEquals(
                Arrays.asList(
                        new Polygon(
                                "0x1",
                                "A",
                                Collections.singletonList(new LatLong(4, 5)),
                                2,
                                3
                        ),
                        new Polygon(
                                "0x5",
                                "B",
                                Collections.singletonList(new LatLong(8, 9)),
                                6,
                                7
                        )
                ),
                polygonsFromString
        );
    }

    @Test
    public void parsePolygons_oneMultipolygonWith_returnsOne() {
        String input = "[POLYGON]\n" +
                "Type=0x7\n" +
                "EndLevel=2\n" +
                "Label=International stuff\n" +
                "CityIdx=32\n" +
                "Data0=(1,2),(3,4)\n" +
                "[END]\n";
        List<Polygon> polygonsFromString = ParserUtils.getPolygonsFromString(input);
        assertEquals(
                Collections.singletonList(new Polygon(
                        "0x7",
                        "International stuff",
                        Arrays.asList(
                                new LatLong(1, 2),
                                new LatLong(3, 4),
                                new LatLong(1, 2)
                        ),
                        2,
                        32
                )),
                polygonsFromString
        );
    }
}
