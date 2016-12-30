package main;

import java.util.Scanner;

/**
 * Created by Dylan on 27/11/16.
 */
public class Main {

    public static void main(String[] args) {
        TowersOfHanoiGame game = new TowersOfHanoiGame(
                new WrappedPrinter(System.out), new DiskStackList()
        );

        Scanner userIn = new Scanner(System.in);
        while (userIn.hasNext()) {
            game.onUserInputtedLine(userIn.nextLine());
        }
    }
}
