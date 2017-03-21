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

    public static void main(String[] args) {
    }

    public LeftWalker() {
        super("Left Walker");
    }

    @Override
    protected Direction move(View v) {
        List<Direction> possibleDirs = determinePossibleDirections(v);

        if (currentDirection == null) {
            if (possibleDirs.size() == 4) {
                // Just go NORTH if there are no walls
                currentDirection = DirectionUseful.NORTH;
                positionRecorder.move(currentDirection);
                return currentDirection.asDirection();
            }
            currentDirection = DirectionUseful.fromDirection(possibleDirs.get(0));
        }

        // Convert Direction to DirectionUseful
        Collection<DirectionUseful> possibleUsefulDirs = possibleDirs
                        .stream()
                        .map(DirectionUseful::fromDirection)
                        .collect(Collectors.toList());

        if (possibleUsefulDirs.size() == 3 &&
                !possibleUsefulDirs.contains(currentDirection) &&
                currentDirection == DirectionUseful.SOUTH) {
            // If we go directly up to a wall, we should turn right, not left
            currentDirection = currentDirection.turnRight90();
            positionRecorder.move(currentDirection);
            return currentDirection.asDirection();
        }

        // Try to avoid infinite loop by avoiding going the same way we have
        // gone before
        Collection<DirectionUseful> usedDirs = positionRecorder.getUsedDirs();
        if (!usedDirs.isEmpty() &&
                possibleUsefulDirs.size() - usedDirs.size() > 0) {
            // Only do this when not empty because the LeftWalker may run out
            // of directions use, but running out of directions may be a
            // required consequence of the memorisation trick
            // possibleUsefulDirs = unusedDirs;

            possibleUsefulDirs.removeAll(usedDirs);
        }

        // Otherwise, prefer turning left, then forward, then right, then backwards
        currentDirection = currentDirection
                .turnLeft90()
                // Turn right 0 or more times
                .turnLeft90()
                .turnRight90(possibleUsefulDirs);

        positionRecorder.move(currentDirection);
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
            // Board board = (Board) v;
            // Coordinate position;
            // int sizeX, sizeY;
            // try {
            //     Field positionField = board.getClass().getDeclaredField("position");
            //     positionField.setAccessible(true);
            //     position = (Coordinate) positionField.get(board);
            //
            //     Field sizeXField = board.getClass().getDeclaredField("sizeX");
            //     sizeXField.setAccessible(true);
            //     sizeX = (int) sizeXField.get(board);
            //
            //     Field sizeYField = board.getClass().getDeclaredField("sizeY");
            //     sizeYField.setAccessible(true);
            //     sizeY = (int) sizeYField.get(board);
            // } catch (NoSuchFieldException e) {
            //     e.printStackTrace();
            //     return null;
            // } catch (IllegalAccessException e) {
            //     e.printStackTrace();
            //     return null;
            // }
            // if (d == Direction.WEST && position.getX() == 0) {
            //     continue; // can't move
            // }
            // else if (d == Direction.EAST && position.getX() == sizeX - 1) {
            //     continue; // can't move
            // }
            // else if (d == Direction.NORTH && position.getY() == 0) {
            //     continue; // can't move
            // }
            // else if (d == Direction.SOUTH && position.getY() == sizeY - 1) {
            //     continue; // can't move
            // }

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

