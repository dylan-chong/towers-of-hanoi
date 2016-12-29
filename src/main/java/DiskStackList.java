

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Dylan on 27/11/16.
 */
public class DiskStackList {
    private final List<DiskStack> discStacks;

    DiskStackList(int numStacks, int numDisks) {
        discStacks = createStartingDiskStacks(numStacks, numDisks);
    }

    int numberOfStacks() {
        return discStacks.size();
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

    private int getTotalDisks() {
        return discStacks.stream()
                .mapToInt(DiskStack::size)
                .reduce((size1, size2) -> size1 + size2)
                .orElseThrow(RuntimeException::new);
    }

    private String[][] toGridOfStrings() {
        // int maxStackSize = discStacks.stream()
        //         .reduce((thStack, thStack2) ->
        //                 thStack.size() > thStack2.size() ? thStack : thStack2)
        //         .orElseThrow(() -> new RuntimeException("No discStacks available"))
        //         .size();

        int maxStackSize = getTotalDisks();

        // stacksStrings[0] gives a stack where stacksStrings[0][0] is the
        // bottom of the stack.
        String[][] stacksStrings = new String[discStacks.size()][maxStackSize];

        for (int s = 0; s < stacksStrings.length; s++) {
            DiskStack stack = discStacks.get(s);
            List<String> diskStrings = stack.toStrings();
            Collections.reverse(diskStrings);

            diskStrings.toArray(stacksStrings[s]);
        }

        return stacksStrings;
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
     * @return The radius of the disc
     * @throws DiskMoveException If moving the disk wasn't possible
     */
    int moveDisk(int fromStackIndex, int toStackIndex) throws DiskMoveException {
        try {
            Disk diskToMove = discStacks.get(fromStackIndex).pop();

            try {
                discStacks.get(toStackIndex).push(diskToMove);
                return diskToMove.radius;
            } catch (RuntimeException e) {
                discStacks.get(fromStackIndex).push(diskToMove);
                throw new DiskMoveException(e.getMessage());
            }
        } catch (RuntimeException e) {
            throw new DiskMoveException(e.getMessage());
        }

    }
}
