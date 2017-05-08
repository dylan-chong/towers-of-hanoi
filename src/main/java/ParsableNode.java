import java.util.Scanner;

/**
 * Created by Dylan on 8/05/17.
 */
public interface ParsableNode extends RobotProgramNode {
    void parseFrom(Scanner scanner);

    /**
     * A node that evaluates to a number
     */
    abstract class NumberableNode implements ParsableNode {
        public abstract int evaluateNumber();
    }

    class NumberNode extends NumberableNode {
        private int number;

        @Override
        public void parseFrom(Scanner scanner) {
            number = scanner.nextInt();
        }

        @Override
        public void execute(Robot robot) {
            // TODO: 8/05/17
        }

        @Override
        public int evaluateNumber() {
            return number;
        }

        @Override
        public String toString() {
            return String.valueOf(number);
        }
    }

    // class AddNode implements RobotProgramNode {
    //
    //     /**
    //      * @param scanner Should have 'add' next
    //      */
    //     public AddNode(Scanner scanner) {
    //         scanner.next("add\\s+\\(");
    //     }
    //
    //     @Override
    //     public void execute(Robot robot) {
    //         // TODO: 8/05/17
    //     }
    // }
}
