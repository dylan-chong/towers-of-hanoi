package swen221.assignment1;

import maze.Direction;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 * Created by Dylan on 14/03/17.
 *
 * Tests follow the naming convention:
 * methodName_stateOrInput_expectation
 */
public class DirectionUsefulTest {

    // *************** Equal directions ***************

    @Test
    public void asDirection_north_returnsNorth() {
        assertEquals(Direction.NORTH, DirectionUseful.NORTH.asDirection());
    }
    @Test
    public void asDirection_east_returnsEast() {
        assertEquals(Direction.EAST, DirectionUseful.EAST.asDirection());
    }
    @Test
    public void asDirection_south_returnsSouth() {
        assertEquals(Direction.SOUTH, DirectionUseful.SOUTH.asDirection());
    }
    @Test
    public void asDirection_west_returnsWest() {
        assertEquals(Direction.WEST, DirectionUseful.WEST.asDirection());
    }

    @Test
    public void fromDirection_west_returnsWest() {
        assertEquals(DirectionUseful.WEST,
                DirectionUseful.fromDirection(Direction.WEST));
    }

    @Test
    public void fromDirection_east_returnsEast() {
        assertEquals(DirectionUseful.WEST,
                DirectionUseful.fromDirection(Direction.WEST));
    }

    // *************** Turning ***************

    @Test
    public void turnRight90_north_returnsWest() {
        assertEquals(DirectionUseful.EAST, DirectionUseful.NORTH.turnRight90());
    }

    @Test
    public void turnRight90_east_returnsNorth() {
        assertEquals(DirectionUseful.SOUTH, DirectionUseful.EAST.turnRight90());
    }

    @Test
    public void turnRight90_south_returnsEast() {
        assertEquals(DirectionUseful.WEST, DirectionUseful.SOUTH.turnRight90());
    }

    @Test
    public void turnRight90_west_returnsSouth() {
        assertEquals(DirectionUseful.NORTH, DirectionUseful.WEST.turnRight90());
    }

    // *************** Turning (with restrictions) ***************

    @Test
    public void turnRight90_westWithOneAvailableDir_returnsAvailableDir() {
        assertEquals(
                DirectionUseful.EAST,
                DirectionUseful.WEST.turnRight90(Arrays.asList(
                        DirectionUseful.EAST
                ))
        );
    }

    @Test
    public void turnRight90_westWithOneAvailableDir2_returnsAvailableDir() {
        assertEquals(
                DirectionUseful.NORTH,
                DirectionUseful.WEST.turnRight90(Arrays.asList(
                        DirectionUseful.NORTH
                ))
        );
    }

    @Test
    public void turnRight90_northWithTwoAvailableDirs_returnsClosestDir() {
        assertEquals(
                DirectionUseful.EAST,
                DirectionUseful.NORTH.turnRight90(Arrays.asList(
                        DirectionUseful.WEST,
                        DirectionUseful.EAST
                ))
        );
    }

    @Test
    public void turnRight90_northWithTwoAvailableDirs2_returnsClosestDir() {
        assertEquals(
                DirectionUseful.EAST,
                DirectionUseful.NORTH.turnRight90(Arrays.asList(
                        DirectionUseful.SOUTH,
                        DirectionUseful.EAST
                ))
        );
    }

    @Test
    public void turnRight90_southWithTwoAvailableDirs_returnsClosestDir() {
        assertEquals(
                DirectionUseful.NORTH,
                DirectionUseful.SOUTH.turnRight90(Arrays.asList(
                        DirectionUseful.NORTH,
                        DirectionUseful.EAST
                ))
        );
    }

    @Test
    public void turnRight90_southWithAllAvailableDirs_returnsClosestDir() {
        assertEquals(
                DirectionUseful.WEST,
                DirectionUseful.SOUTH.turnRight90(Arrays.asList(
                        DirectionUseful.WEST,
                        DirectionUseful.EAST,
                        DirectionUseful.SOUTH,
                        DirectionUseful.NORTH
                ))
        );
    }

    @Test
    public void turnRight90_northWithAllAvailableDirs_returnsClosestDir() {
        assertEquals(
                DirectionUseful.EAST,
                DirectionUseful.NORTH.turnRight90(Arrays.asList(
                        DirectionUseful.WEST,
                        DirectionUseful.EAST,
                        DirectionUseful.SOUTH,
                        DirectionUseful.NORTH
                ))
        );
    }
}
