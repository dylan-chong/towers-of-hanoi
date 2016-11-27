import java.util.ArrayDeque;

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
        for (int d = disks; d >= 1; d++) {
            if (!stack.push(new THDisk(d))) assert false;
        }

        stack.invariant();
        return stack;
    }

    /**
     * @param disk Disk to add
     * @return true iff disk is smaller than others on the stack
     */
    private boolean push(THDisk disk) {
        invariant();

        if (diskStack.size() != 0 && diskStack.peek().radius < disk.radius)
            return false;
        diskStack.push(disk);

        invariant();
        return true;
    }

    private void invariant() {
        // TODO
    }

    private static class THDisk {
        final int radius;

        THDisk(int radius) {
            assert radius >= 1;
            this.radius = radius;
        }

        @Override
        public String toString() {
            return "(" + radius + ")";// TODO LATER make it look better
        }
    }
}
