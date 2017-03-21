package swen221.assignment1;

import org.junit.Test;

import java.awt.*;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;

/**
 * Created by Dylan on 21/03/17.
 */
public class PositionRecorderTest {

    @Test
    public void getCurrentPoint_new_equalsInitialisedPoint() {
        Point p = new Point(1, 2);
        assertEquals(p, new PositionRecorder(p).getCurrentPoint());
    }

    /**
     * Other tests depend on this test being true
     */
    @Test
    public void getCurrentPoint_newWithoutArgs_equalsZeroZero() {
        assertEquals(new Point(0, 0), new PositionRecorder().getCurrentPoint());
    }

    @Test
    public void getCurrentPoint_moveWest_equalsPointToLeft() {
        PositionRecorder positionRecorder = new PositionRecorder();
        positionRecorder.move(DirectionUseful.WEST);
        assertEquals(new Point(-1, 0), positionRecorder.getCurrentPoint());
    }

    @Test
    public void getCurrentPoint_moveNorth_equalsPointToLeft() {
        PositionRecorder positionRecorder = new PositionRecorder();
        positionRecorder.move(DirectionUseful.NORTH);
        assertEquals(new Point(0, 1), positionRecorder.getCurrentPoint());
    }

    @Test
    public void getCurrentPoint_moveTwoPoints_equalsPointNorthEast() {
        PositionRecorder positionRecorder = new PositionRecorder();
        positionRecorder.move(DirectionUseful.NORTH);
        positionRecorder.move(DirectionUseful.EAST);
        assertEquals(new Point(1, 1), positionRecorder.getCurrentPoint());
    }

    @Test
    public void getUsedDirs_noMoves_allPointsShouldHaveNoMoves() {
        PositionRecorder positionRecorder = new PositionRecorder();
        assertEquals(new HashSet<>(), new HashSet<>(positionRecorder.getUsedDirs()));
        // Stream.of(
        //         new Point(0, 0),
        //         new Point(1, 0),
        //         new Point(1, 1),
        //         new Point(0, 1),
        //         new Point(-1, 1),
        //         new Point(-1, 0),
        //         new Point(-1, -1),
        //         new Point(0, -1)
        // ).forEach(point -> {
        // });
    }

    // @Test
    // public void getUsedDirs_afterMovingTwoPoints_onlyTwoPointsShouldBeMoved() {
    //     PositionRecorder positionRecorder = new PositionRecorder();
    //     positionRecorder.move(DirectionUseful.NORTH);
    //     positionRecorder.move(DirectionUseful.EAST);
    //     assertEquals(new Point(1, 1), positionRecorder.getCurrentPoint());
    // }
}
