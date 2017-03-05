package main;

/**
 * Created by Dylan on 27/11/16.
 */
public class Main {

    public static void main(String[] args) {
        GameGui gameGui = new GameGui();
        gameGui.setTowersOfHanoiGame(new TowersOfHanoiGame(
                new GameInfoPrinter(
                        new TextAreaPrinter(gameGui.getGameOut())
                ),
                new DiskStackList()
        ));
    }
}
