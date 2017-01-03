package junit;

import main.Disk;
import main.DiskMoveException;
import main.DiskStackList;
import main.Move;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Created by Dylan on 3/01/17.
 */
public class DiskStackListTest {

    private static final int DEFAULT_NUM_DISKS = 3;
    private static final int NUM_STACKS = 3;


    private List<Disk> getAllDisksFromNewDiskStackList() {
        return getAllDisksFromNewDiskStackList(DEFAULT_NUM_DISKS);
    }

    private List<Disk> getAllDisksFromNewDiskStackList(int numDisks) {
        return getNewDiskStackList(numDisks).getAllDisks();
    }

    private DiskStackList getNewDiskStackList(int numDisks) {
        return new DiskStackList(NUM_STACKS, numDisks);
    }

    private boolean areDisksSorted(List<Disk> disks) {
        for (int d = 0; d < disks.size() - 1; d++) {
            if (disks.get(d).getRadius() > disks.get(d + 1).getRadius()) {
                return false;
            }
        }

        return true;
    }

    private boolean checkAllDisksCorrectSize(int numDisks) {
        List<Disk> disks = getAllDisksFromNewDiskStackList(numDisks);
        return numDisks == disks.size();
    }

    @Test
    public void getAllDisks_startingGameSetupWith3Disks_returnsCorrectSizeList() {
        Assert.assertTrue(checkAllDisksCorrectSize(3));
    }

    @Test
    public void getAllDisks_startingGameSetupWith5Disks_returnsCorrectSizeList() {
        Assert.assertTrue(checkAllDisksCorrectSize(5));
    }

    @Test
    public void getAllDisks_disksMovedToDifferentStacks_returnsCorrectSizeList()
            throws DiskMoveException {
        final int numDisks = 5;
        DiskStackList diskStackList = getNewDiskStackList(numDisks);

        // Move 3 disks out of starting stack
        diskStackList.moveDisk(new Move(0, 1));
        diskStackList.moveDisk(new Move(0, 2));
        diskStackList.moveDisk(new Move(1, 2));
        diskStackList.moveDisk(new Move(0, 1));

        int size = diskStackList.getAllDisks().size();
        Assert.assertEquals(size, numDisks);
    }

    @Test
    public void getAllDisks_startingGameSetup_returnsSortedList() {
        List<Disk> disks = getAllDisksFromNewDiskStackList();
        Assert.assertTrue(areDisksSorted(disks));
    }

    @Test
    public void getAllDisks_disksMovedToDifferentStacks_returnsSortedList() throws DiskMoveException {
        final int numDisks = 5;
        DiskStackList diskStackList = getNewDiskStackList(numDisks);

        // Move 3 disks out of starting stack
        diskStackList.moveDisk(new Move(0, 1));
        diskStackList.moveDisk(new Move(0, 2));
        diskStackList.moveDisk(new Move(1, 2));
        diskStackList.moveDisk(new Move(0, 1));

        Assert.assertTrue(areDisksSorted(diskStackList.getAllDisks()));
    }
}
