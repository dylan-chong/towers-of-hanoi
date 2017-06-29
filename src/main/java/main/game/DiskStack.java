package main.game;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Dylan on 27/11/16.
 */
public class DiskStack {
    private Deque<Disk> diskStack = new ArrayDeque<>();

    /**
     * Use static build methods instead
     */
    public DiskStack() {
        invariant();
    }

    public int getHeight() {
        return diskStack.size();
    }

    public List<Disk> getDiskStack() {
        Disk[] diskArray = diskStack.toArray(new Disk[diskStack.size()]);
        List<Disk> diskList = Arrays.asList(diskArray);
        return Collections.unmodifiableList(diskList);
    }

    /**
     * @param disk Disk to add
     * @returns true iff disk is smaller than others on the stack
     */
    public void push(Disk disk) throws DiskMoveException {
        invariant();

        if (diskStack.size() != 0 &&
                diskStack.peek().getRadius() < disk.getRadius()) {
            throw new DiskMoveException("Disk is bigger than the one at the " +
                    "top of the stack");
        }
        diskStack.push(disk);

        invariant();
    }

    public Disk pop() throws DiskMoveException {
        if (diskStack.isEmpty())
            throw new DiskMoveException("There is no disk to remove");
        return diskStack.pop();
    }

    public int size() {
        return diskStack.size();
    }

    public List<String> toStrings() {
        Object[] stack = diskStack.toArray();
        List<String> diskStrings = Arrays.stream(stack)
                .map(Object::toString)
                .collect(Collectors.toList());

        return diskStrings;
    }

    private void invariant() {
        boolean isAssertOn = false;
        assert isAssertOn = true;
        if (!isAssertOn) return;

        // ***

        if (diskStack.size() <= 1) return; // nothing to check

        Disk smallerDisk = null;
        for (Disk disk : diskStack) { // Iterates in popping order
            if (smallerDisk == null) {
                smallerDisk = disk;
                continue;
            }

            assert disk.getRadius() > smallerDisk.getRadius();
        }
    }

}
