/**
 * Created by Dylan on 27/11/16.
 */
public class THMain {

    private static final int DEFAULT_NUM_STACKS = 3;

    public static void main(String[] args) {
        THMain game = new THMain();
    }

    // *********************************************************

    private static final String TITLE = "*** WELCOME TO TOWERS OF HANOI ***";
    private THStackList diskStacks;

    private THMain() {
        printWelcome();
        diskStacks = new THStackList(DEFAULT_NUM_STACKS, 3);
        printStackState();
    }

    private void printWelcome() {
        // Push everything to bottom of screen
        for (int l = 0; l < 300; l++) System.out.println();

        System.out.println();
        printSectionLine(0);
        System.out.println(TITLE);
        printSectionLine(0);
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
        System.out.println(
                new String(new char[TITLE.length()]).replace("\0", c)
        );
    }

    private void printStackState() {
        System.out.println();
        printSectionLine(1);
        System.out.println();
        System.out.println(diskStacks.toString());
        System.out.println();
        printSectionLine(1);
        System.out.println();
    }
}
