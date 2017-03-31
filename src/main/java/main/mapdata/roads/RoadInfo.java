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
     */
    public enum SpeedLimit {
        // The ordering and position of these must be maintained
        SL_005(5),
        SL_020(20),
        SL_040(40),
        SL_060(60),
        SL_080(80),
        SL_100(100),
        SL_110(110),
        // Just use the fastest possible one for admissible estimates
        SL_NOT_DEFINED(getFastest().speedKMpH);

        public final double speedKMpH;

        SpeedLimit(double speedKMpH) {
            this.speedKMpH = speedKMpH;
        }

        public static SpeedLimit getFastest() {
            return SL_110;
        }
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
