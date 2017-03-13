package swen221.assignment1;

import maze.Direction;
import maze.View;
import maze.Walker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * An implementation of the left walker, which you need to complete according to
 * the specification given in the assignment handout.
 *
 */
public class LeftWalker extends Walker {

    private DirectionUseful currentDirection;

    public static void main(String[] args) {
    }

    public LeftWalker() {
        super("Left Walker");
    }

    @Override
    protected Direction move(View v) {
        List<Direction> possibleDirs = determinePossibleDirections(v);

        if (currentDirection == null) {
            currentDirection = DirectionUseful.fromDirection(
                    selectRandomDirection(possibleDirs)
            );
        }

        // Convert Direction to DirectionUseful
        Collection<DirectionUseful> possibleUsefulDirs =
                determinePossibleDirections(v)
                        .stream()
                        .map(DirectionUseful::fromDirection)
                        .collect(Collectors.toSet());

        // Prefer turning left, then forward, then right, then backwards
        currentDirection = currentDirection
                .turnLeft90()
                // Turn right 0 or more times
                .turnLeft90()
                .turnRight90(possibleUsefulDirs);

        return currentDirection.asDirection();
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

