/**
 * Created by Dylan on 27/11/16.
 */
public class THDisk {
    final int radius;

    THDisk(int radius) {
        assert radius >= 1;
        this.radius = radius;
    }

    static String toString(THDisk disk) {
        String radius;
        if (disk == null) radius = " ";
        else radius = disk.radius + "";

        return "(" + radius + ")";// TODO LATER make it look better
    }

    @Override
    public String toString() {
        return toString(this);
    }
}
