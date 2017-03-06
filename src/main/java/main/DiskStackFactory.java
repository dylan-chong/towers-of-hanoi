package main;

/**
 * Created by Dylan on 7/03/17.
 */
public interface DiskStackFactory {
    DiskStack createFullStack(int numDisks);

    DiskStack createEmptyStack();
}
