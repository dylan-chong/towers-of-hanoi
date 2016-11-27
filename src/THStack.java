import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * Created by Dylan on 27/11/16.
 */
public class THStack {
    private ArrayDeque<THDisk> diskStack = new ArrayDeque<>();

    private THStack() {
    }

    static THStack buildEmptyStack() {
        THStack stack = new THStack();
        stack.invariant();
        return stack;
    }

    static THStack buildFullStack(int disks) {
        assert disks >= 1;

        THStack stack = new THStack();
        for (int d = disks; d >= 1; d--) {
            stack.push(new THDisk(d));
        }

        stack.invariant();
        return stack;
    }

    /**
     * @param disk Disk to add
     * @return true iff disk is smaller than others on the stack
     */
    void push(THDisk disk) {
        invariant();

        if (diskStack.size() != 0 && diskStack.peek().radius < disk.radius)
            throw new RuntimeException("Disk is bigger than the one at the " +
                    "top of the stack");
        diskStack.push(disk);

        invariant();
    }

    THDisk pop() {
        if (diskStack.isEmpty())
            throw new NoSuchElementException("There is no disk to remove");
        return diskStack.pop();
    }

    int size() {
        return diskStack.size();
    }

    List<String> toStrings() {
        Object[] stack = diskStack.toArray();
        List<String> diskStrings = Arrays.stream(stack)
            .map(Object::toString)
            .collect(Collectors.toList());

        return diskStrings;
    }

    private void invariant() {
        boolean isAssertOn = false;
        // noinspection AssertWithSideEffects
        assert isAssertOn = true;
        if (!isAssertOn) return;

        // ***

        if (diskStack.size() <= 1) return; // nothing to check

        THDisk smallerDisk = null;
        for (THDisk disk : diskStack) { // Iterates in popping order
            if (smallerDisk == null) {
                smallerDisk = disk;
                continue;
            }

            assert disk.radius > smallerDisk.radius;
        }
    }

}
