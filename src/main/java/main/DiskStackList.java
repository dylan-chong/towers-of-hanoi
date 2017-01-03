package main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Dylan on 27/11/16.
 */
public class DiskStackList {
    private static final int DEFAULT_NUM_STACKS = 3;
    private static final int DEFAULT_NUM_DISKS = 3;

    private final List<DiskStack> diskStacks;

    public DiskStackList(int numStacks, int numDisks) {
        diskStacks = createStartingDiskStacks(numStacks, numDisks);
    }

    public DiskStackList() {
        this(DEFAULT_NUM_STACKS, DEFAULT_NUM_DISKS);
    }

    private static List<DiskStack> createStartingDiskStacks(int numStacks,
                                                            int numDisks) {
        assert numStacks >= 2;
        assert numDisks >= 1;

        List<DiskStack> stacks = new ArrayList<>();
        stacks.add(DiskStack.buildFullStack(numDisks));

        for (int s = 1; s < numStacks; s++) {
            stacks.add(DiskStack.buildEmptyStack());
        }

        return Collections.unmodifiableList(stacks);
    }

    public int numberOfStacks() {
        return diskStacks.size();
    }

    public List<DiskStack> getDiskStacks() {
        return Collections.unmodifiableList(diskStacks);
    }

    /**
     * @return A sorted {@link List<Disk>} where the disk with the smallest
     * radius is at index 0
     */
    public List<Disk> getAllDisks() {
        List<Disk> disks = new ArrayList<>();

        diskStacks.forEach(diskStack ->
                diskStack.getDiskStack().forEach(disks::add));

        return disks;
    }

    @Override
    public String toString() {
        String[][] stacksStrings = toGridOfStrings();
        StringBuilder sb = new StringBuilder();

        // Iterate the top of each stack first, then the next layer down, etc ...
        for (int y = stacksStrings[0].length - 1; y >= 0; y--) {
            for (String[] stacksString : stacksStrings) {

                String diskString = stacksString[y];
                if (diskString == null) diskString = Disk.toString(null);
                sb.append(diskString);
            }

            if (y > 0) sb.append('\n');
        }

        return sb.toString();
    }

    /**
     * @param fromStackIndex
     * @param toStackIndex
     * @return The radius of the disk
     * @throws DiskMoveException If moving the disk wasn't possible
     */
    public int moveDisk(int fromStackIndex, int toStackIndex) throws DiskMoveException {
        Disk diskToMove = diskStacks.get(fromStackIndex).pop();

        try {
            diskStacks.get(toStackIndex).push(diskToMove);
            return diskToMove.getRadius();
        } catch (DiskMoveException e) {
            // Put diskToMove back where it was before
            diskStacks.get(fromStackIndex).push(diskToMove);
            throw e;
        }
    }

    private int getTotalDisks() {
        return diskStacks.stream()
                .mapToInt(DiskStack::size)
                .reduce((size1, size2) -> size1 + size2)
                .orElseThrow(RuntimeException::new);
    }

    private String[][] toGridOfStrings() {
        int maxStackSize = getTotalDisks();

        // stacksStrings[0] gives a stack where stacksStrings[0][0] is the
        // bottom of the stack.
        String[][] stacksStrings = new String[diskStacks.size()][maxStackSize];

        for (int s = 0; s < stacksStrings.length; s++) {
            DiskStack stack = diskStacks.get(s);
            List<String> diskStrings = stack.toStrings();
            Collections.reverse(diskStrings);

            diskStrings.toArray(stacksStrings[s]);
        }

        return stacksStrings;
    }
}
