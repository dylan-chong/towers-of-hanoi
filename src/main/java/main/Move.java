package main;

/**
 * Created by Dylan on 3/01/17.
 */
public class Move {
    public final int fromStackIndex;
    public final int toStackIndex;

    public Move(int fromStackIndex, int toStackIndex) {
        this.fromStackIndex = fromStackIndex;
        this.toStackIndex = toStackIndex;
    }
}