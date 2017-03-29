package main.mapdata.roads;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Dylan on 15/03/17.
 * <p>
 * Contains info about a road (or a section of a road) with one set of
 * information.
 * <p>
 * Different sections of the road may have different data, so they are
 * represented with multiple {@link RoadInfo} objects with different ids, but
 * the same label and city.
 * <p>
 * One {@link RoadInfo} implicitly can be made up of multiple
 * {@link RoadSegment} objects.
 */
public class RoadInfo {
    public final long id;
    public final int roadType;
    public final String label;
    public final String city;
    public final boolean isOneWay;
    public final SpeedLimit speedLimit;
    public final RoadClass roadClass;
    public final boolean notForCar;
    public final boolean notForPedestrians;
    public final boolean notForBicycles;

    public RoadInfo(long id,
                    int roadType,
                    String label,
                    String city,
                    boolean isOneWay,
                    SpeedLimit speedLimit,
                    RoadClass roadClass,
                    boolean notForCar,
                    boolean notForPedestrians,
                    boolean notForBicycles) {
        this.id = id;
        this.roadType = roadType;
        this.label = label;
        this.city = city;
        this.isOneWay = isOneWay;
        this.speedLimit = speedLimit;
        this.roadClass = roadClass;
        this.notForCar = notForCar;
        this.notForPedestrians = notForPedestrians;
        this.notForBicycles = notForBicycles;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;

        if (!(obj instanceof RoadInfo)) {
            return false;
        }

        RoadInfo other = (RoadInfo) obj;
        return other.id == id &&
                other.roadType == roadType &&
                other.label.equals(label) &&
                other.city.equals(city) &&
                other.isOneWay == isOneWay &&
                other.speedLimit == speedLimit &&
                other.roadClass == roadClass &&
                other.notForCar == notForCar &&
                other.notForPedestrians == notForPedestrians &&
                other.notForBicycles == notForBicycles;
    }

    @Override
    public String toString() {
        return new RoadInfoByName(label, city).toString();
    }

    public static List<RoadInfoByName> getDistinctByName(
            Collection<RoadInfo> roadInfos) {
        return roadInfos.stream()
                .map(roadInfo -> new RoadInfoByName(roadInfo.label, roadInfo.city))
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * Speed limit categories (sorted from slowest to fastest)
     * <p>
     * 0 = 5km/h
     * 1 = 20km/h
     * 2 = 40km/h
     * 3 = 60km/h
     * 4 = 80km/h
     * 5 = 100km/h
     * 6 = 110km/h
     * 7 = no limit
     */
    public enum SpeedLimit {
        // The ordering of these must be maintained
        SL_0,
        SL_1,
        SL_2,
        SL_3,
        SL_4,
        SL_5,
        SL_6,
        SL_7;
    }

    /**
     * Road class (sorted from slowest to fastest)
     */
    public enum RoadClass {
        // The ordering of these must be maintained
        RESIDENTIAL,
        COLLECTOR,
        ARTERIAL,
        PRINCIPAL_HIGHWAY,
        MAJOR_HIGHWAY;
    }
}
