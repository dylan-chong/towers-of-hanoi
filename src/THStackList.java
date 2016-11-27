import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Dylan on 27/11/16.
 */
public class THStackList {
    private final List<THStack> stacks;

    THStackList(int numStacks, int numDisks) {
        stacks = createStartingDiskStacks(numStacks, numDisks);
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
}
