package main.mapdata;

/**
 * Created by Dylan on 16/03/17.
 *
 * Adapter to allow filtering {@link RoadInfo} duplicates by name
 */
public class RoadInfoByName {
    /**
     * The road name
     */
    public final String label;
    public final String city;

    public RoadInfoByName(String label, String city) {
        this.label = label;
        this.city = city;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;

        if (!(obj instanceof RoadInfoByName)) {
            return false;
        }

        RoadInfoByName other = (RoadInfoByName) obj;
        return other.label.equals(label) &&
                other.city.equals(city);
    }

    @Override
    public String toString() {
        return label + ", " + city;
    }

    @Override
    public int hashCode() {
        return city.hashCode() + 13 * label.hashCode();
    }
}
