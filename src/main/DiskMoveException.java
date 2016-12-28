package main;

/**
 * Created by Dylan on 27/11/16.
 */
class DiskMoveException extends Exception {
    DiskMoveException(String reason) {
        super(reason);
    }
}
