package main.game;

import java.util.Comparator;

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

        return "[" + radius + "]";
    }

    public static Comparator<Disk> getRadiusComparator() {
        return (o1, o2) -> {
            if (o1.getRadius() < o2.getRadius()) return -1;
            if (o1.getRadius() > o2.getRadius()) return 1;
            return 0;
        };
    }

    public int getRadius() {
        return radius;
    }

    @Override
    public String toString() {
        return toString(this);
    }
}
