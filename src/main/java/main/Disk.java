package main;

/**
 * Created by Dylan on 27/11/16.
 */
public class Disk {
    private final int radius;

    Disk(int radius) {
        assert radius >= 1;
        this.radius = radius;
    }

    static String toString(Disk disk) {
        String radius;
        if (disk == null) radius = " ";
        else radius = disk.radius + "";

        return "[" + radius + "]"; // TODO LATER make it look better
    }

    public int getRadius() {
        return radius;
    }

    @Override
    public String toString() {
        return toString(this);
    }
}
