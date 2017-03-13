package swen221.assignment1;

import maze.Direction;

import java.util.Arrays;
import java.util.Collection;

/**
 * Created by Dylan on 14/03/17.
 *
 * Same as {@link Direction} but with useful methods.
 *
 * Now that I think about it, I could have just made helper
 * methods to exactly the same thing without making a new enum.
 */
public enum DirectionUseful {
    // Don't change the order of these
    // (they should be in clockwise order)
    NORTH(Direction.NORTH),
    EAST(Direction.EAST),
    SOUTH(Direction.SOUTH),
    WEST(Direction.WEST);

    private final Direction direction;

    DirectionUseful(Direction direction) {
        this.direction = direction;
    }

    public static DirectionUseful fromDirection(Direction direction) {
        return Arrays.stream(values())
                .filter(dirUseful -> dirUseful.direction == direction)
                .findAny()
                .orElseThrow(RuntimeException::new);
    }

    public Direction asDirection() {
        return direction;
    }

    public DirectionUseful turnLeft90() {
        return turnRight90().turnRight90().turnRight90();
    }

    public DirectionUseful turnRight90() {
        int newOrdinal = (ordinal() + 1) % 4;
        return values()[newOrdinal];
    }

    /**
     * Turns right to the closest {@link DirectionUseful} in availableDirections
     * (the closest {@link DirectionUseful} does not include this/itself).
     *
     * @param availableDirections
     * @return
     */
    public DirectionUseful turnRight90(Collection<DirectionUseful> availableDirections) {
        if (availableDirections.isEmpty()) throw new AssertionError();
        DirectionUseful nextDirection = this;

        while (true) {
            nextDirection = nextDirection.turnRight90();
            if (availableDirections.contains(nextDirection)) break;
        }

        return nextDirection;
    }
}
