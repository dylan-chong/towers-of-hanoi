import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Dylan on 27/11/16.
 */
public class THStackList {
    private final List<THStack> discStacks;

    THStackList(int numStacks, int numDisks) {
        discStacks = createStartingDiskStacks(numStacks, numDisks);
    }

    private static List<THStack> createStartingDiskStacks(int numStacks,
                                                          int numDisks) {
        assert numStacks >= 2;
        assert numDisks >= 1;

        List<THStack> stacks = new ArrayList<>();
        stacks.add(THStack.buildFullStack(numDisks));

        for (int s = 1; s < numStacks; s++) {
            stacks.add(THStack.buildEmptyStack());
        }

        return Collections.unmodifiableList(stacks);
    }

    private String[][] toGridOfStrings() {
        int maxStackSize = discStacks.stream()
                .reduce((thStack, thStack2) ->
                        thStack.size() > thStack2.size() ? thStack : thStack2)
                .orElseThrow(() -> new RuntimeException("No discStacks available"))
                .size();

        // stacksStrings[0] gives a stack where stacksStrings[0][0] is the
        // bottom of the stack.
        String[][] stacksStrings = new String[discStacks.size()][maxStackSize];

        for (int s = 0; s < stacksStrings.length; s++) {
            THStack stack = discStacks.get(s);
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
                if (diskString == null) diskString = THDisk.toString(null);
                sb.append(diskString);
            }

            if (y > 0) sb.append('\n');
        }

        return sb.toString();
    }
}
