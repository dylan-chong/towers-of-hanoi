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
    }


    private void printWelcome() {
        System.out.println();
        System.out.println();
        System.out.println();
        printSectionLine();
        System.out.println();
        System.out.println(TITLE);
        System.out.println();
        printSectionLine();
        System.out.println();
        System.out.println();
        System.out.println();
    }

    private void printSectionLine() {
        System.out.println(
                new String(new char[TITLE.length()]).replace("\0", "*")
        );
    }
}
