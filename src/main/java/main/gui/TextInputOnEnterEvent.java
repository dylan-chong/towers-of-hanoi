package main.gui;

/**
 * Created by Dylan on 7/03/17.
 */
public class TextInputOnEnterEvent {
    private String userEnteredString;

    public TextInputOnEnterEvent(String userEnteredString) {
        this.userEnteredString = userEnteredString;
    }

    public String getUserEnteredLine() {
        return userEnteredString;
    }
}
