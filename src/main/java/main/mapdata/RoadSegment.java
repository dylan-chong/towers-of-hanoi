package main.mapdata;

import main.LatLong;

import java.util.Collections;
import java.util.List;

/**
 * Created by Dylan on 15/03/17.
 *
 * Represents the segment of road between two {@link Node} objects. The name
 * is slightly misleading - although it is called a 'segment', it can be broken
 * down into smaller segments. See {@link RoadInfo} for more info.
 */
public class RoadSegment {

    /**
     * There can be multiple {@link RoadSegment} objects where they have the
     * same name, are connected to each other, and have different ids.
     */
    public final long roadId;
    public final double length;

    /**
     * Start and end nodes
     */
    public final long node1ID, node2ID; // TODO point to actual nodes?

    /**
     * The road makes up straight lines between these latLongs
     */
    public final List<LatLong> points;

    public RoadSegment(long roadId,
                       double length,
                       long node1ID,
                       long node2ID,
                       List<LatLong> points) {
        this.roadId = roadId;
        this.length = length;
        this.node1ID = node1ID;
        this.node2ID = node2ID;
        this.points = Collections.unmodifiableList(points);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;

        if (!(obj instanceof RoadSegment)) {
            return false;
        }

        RoadSegment otherRoadSegment = (RoadSegment) obj;

        return otherRoadSegment.roadId == roadId &&
                otherRoadSegment.length == length &&
                otherRoadSegment.node1ID == node1ID &&
                otherRoadSegment.node2ID == node2ID &&
                otherRoadSegment.points.equals(points);
    }

    @Override
    public int hashCode() {
        return (int) roadId;
    }
}
