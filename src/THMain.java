/**
 * Created by Dylan on 27/11/16.
 */
public class THMain {


    public static void main(String[] args) {
        THMain game = new THMain();
        // O.p("hello the fox jumped over the lazy world");
    }

    // *********************************************************


    static final String TITLE = "*** WELCOME TO TOWERS OF HANOI ***";
    private static final int DEFAULT_NUM_STACKS = 3;


    private THStackList diskStacks;


    private THMain() {
        printWelcome();
        diskStacks = new THStackList(DEFAULT_NUM_STACKS, 3);
        printStackState();

        // TODO LATER remove
        for (int i = 0; i < 3; i++) {
            int from = (int) (Math.random() * diskStacks.numberOfStacks());
            int to = (int) (Math.random() * diskStacks.numberOfStacks());
            moveDisk(from, to);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void printWelcome() {
        // Push everything to bottom of screen
        for (int l = 0; l < 300; l++) O.pln();

        O.pln();
        printSectionLine(0);
        O.pln(TITLE);
        printSectionLine(0);
    }

    private void moveDisk(int fromStackIndex, int toStackIndex) {
        try {
            int radius = diskStacks.moveDisk(fromStackIndex, toStackIndex);
            O.pf("MOVED DISK of size %d: from index %d to %d\n",
                    radius, fromStackIndex, toStackIndex);
        } catch (DiskMoveException e) {
            O.pln("CAN'T MOVE: " + e.getMessage());
        }

        printStackState();
    }

    /**
     *
     * @param importance 0 for most important
     */
    private void printSectionLine(int importance) {
        String c;
        switch (importance) {
            case 0:
                c = "*";
                break;
            case 1:
                c = "-";
                break;
            default:
                throw new IllegalArgumentException(
                        "Nothing set for " + importance + " importance level"
                );
        }
        O.pln(
                new String(new char[O.CONSOLE_WIDTH]).replace("\0", c)
        );
    }

    private void printStackState() {
        O.pln();
        printSectionLine(1);
        O.pln();
        O.pln(diskStacks.toString());
        O.pln();
        printSectionLine(1);
        O.pln();
    }
}
