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
    private PositionRecorder positionRecorder = new PositionRecorder();
    private boolean lastMoveHadWallToLeft = false;

    public static void main(String[] args) {
    }

    public LeftWalker() {
        super("Left Walker");
    }

    @Override
    protected Direction move(View v) {
        DirectionUseful newDirection = moveReturnUseful(v);
        positionRecorder.move(newDirection);
        lastMoveHadWallToLeft = hasWallToLeft(v);
        return newDirection.asDirection();
    }

    private boolean hasWallToLeft(View v) {
        List<DirectionUseful> possibleDirs =
                determinePossibleDirections(v)
                        .stream()
                        .map(DirectionUseful::fromDirection)
                        .collect(Collectors.toList());
        DirectionUseful left = currentDirection.turnLeft90();
        return !possibleDirs.contains(left);
    }

    private DirectionUseful moveReturnUseful(View v) {
        // Convert Direction to DirectionUseful
        List<DirectionUseful> possibleDirs =
                determinePossibleDirections(v)
                        .stream()
                        .map(DirectionUseful::fromDirection)
                        .collect(Collectors.toList());

        if (currentDirection == null) {
            if (possibleDirs.size() == 4) {
                // Just go NORTH if there are no walls
                currentDirection = DirectionUseful.NORTH;
                return currentDirection;
            }
            currentDirection = possibleDirs.get(0);
        }

        // If the only way you can't go is forwards, then turn right, but only
        // if there was no left wall on the previous move
        if (possibleDirs.size() == 3 &&
                !possibleDirs.contains(currentDirection) &&
                !lastMoveHadWallToLeft) {
            currentDirection = currentDirection.turnRight90();
            return currentDirection;
        }

        // Try to avoid infinite loop by avoiding going the same way we have
        // gone before
        Collection<DirectionUseful> usedDirs = positionRecorder.getUsedDirs();
        if (!usedDirs.isEmpty() &&
                possibleDirs.size() - usedDirs.size() > 0) {
            // Only do this when not empty because the LeftWalker may run out
            // of directions use, but running out of directions may be a
            // required consequence of the memorisation trick

            possibleDirs.removeAll(usedDirs);
        }

        // Otherwise, prefer turning left, then forward, then right, then backwards
        currentDirection = currentDirection
                .turnLeft90()
                // Turn right 0 or more times
                .turnLeft90()
                .turnRight90(possibleDirs);

        return currentDirection;
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

