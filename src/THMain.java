import java.util.ArrayDeque;

/**
 * Created by Dylan on 27/11/16.
 */
public class THMain {

    private static final String TITLE = "*** WELCOME TO TOWERS OF HANOI ***";

    public static void main(String[] args) {
        THMain game = new THMain();
    }

    THMain() {
        printWelcome();
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

    private static class THStack {
        private ArrayDeque<THDisk> diskStack = new ArrayDeque<>();

        THStack() {

        }
    }

    private static class THDisk {

    }
}
