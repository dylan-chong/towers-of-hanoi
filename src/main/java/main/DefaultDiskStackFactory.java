package main;

/**
 * Created by Dylan on 7/03/17.
 */
public class DefaultDiskStackFactory implements DiskStackFactory {
    @Override
    public DiskStack createFullStack(int numDisks) {
        assert numDisks >= 1;

        DiskStack stack = new DiskStack();
        for (int d = numDisks; d >= 1; d--) {
            try {
                stack.push(new Disk(d));
            } catch (DiskMoveException ignored) {}
        }

        return stack;
    }

    @Override
    public DiskStack createEmptyStack() {
        return new DiskStack();
    }
}
