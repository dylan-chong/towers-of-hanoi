package swen221.assignment1;

import maze.Direction;
import maze.View;
import maze.Walker;

import java.util.ArrayList;
import java.util.List;

/**
 * An implementation of the left walker, which you need to complete according to
 * the specification given in the assignment handout.
 *
 */
public class LeftWalker extends Walker {

    public static void main(String[] args) {
    }

    public LeftWalker() {
        super("Left Walker");
    }

    @Override
    protected Direction move(View v) {
        // TODO: you need to implement this method according to the
        // specification given for the left walker!!

        // Use the pause() command to slow the simulation down so you can see
        // what's happening...
        pause(100);

        // Currently, the left walker just follows a completely random
        // direction. This is not what the specification said it should do, so
        // tests will fail ... but you should find it helpful to look at how it
        // works!
        return getRandomDirection(v);
    }

    /**
     * This simply returns a randomly chosen (valid) direction which the walker
     * can move in.
     *
     * @param v
     * @return
     */
    private Direction getRandomDirection(View v) {
        // The random walker first decides what directions it can move in. The
        // walker cannot move in a direction which is blocked by a wall.
        List<Direction> possibleDirections = determinePossibleDirections(v);

        // Second, the walker chooses a random direction from the list of
        // possible directions
        return selectRandomDirection(possibleDirections);
    }

    /**
     * Determine the list of possible directions. That is, the directions which
     * are not blocked by a wall.
     *
     * @param v
     *            The View object, with which we can determine which directions
     *            are possible.
     * @return
     */
    private List<Direction> determinePossibleDirections(View v) {
        Direction[] allDirections = Direction.values();
        ArrayList<Direction> possibleDirections = new ArrayList<Direction>();

        for (Direction d : allDirections) {
            if (v.mayMove(d)) {
                // Yes, this is a valid direction
                possibleDirections.add(d);
            }
        }

        return possibleDirections;
    }

    /**
     * Select a random direction from a list of possible directions.
     *
     * @param possibleDirections
     * @return
     */
    private Direction selectRandomDirection(List<Direction> possibleDirections) {
        int random = (int) (Math.random() * possibleDirections.size());
        return possibleDirections.get(random);
    }
}

