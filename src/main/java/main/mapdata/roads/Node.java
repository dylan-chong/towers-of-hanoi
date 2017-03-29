package main.mapdata.roads;

import main.mapdata.location.LatLong;

/**
 * Created by Dylan on 14/03/17.
 *
 * Represents an intersection or the dead end of a road. Not to be confused
 * with {@link main.datastructures.Graph.Node} (so is sometimes called NodeInfo)
 */
public class Node {
    public final LatLong latLong;
    public final long id;

    public Node(long id, LatLong latLong) {
        this.latLong = latLong;
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;

        if (!(obj instanceof Node)) {
            return false;
        }

        Node otherNode = (Node) obj;
        return otherNode.latLong.equals(latLong) &&
                otherNode.id == id;
    }

    @Override
    public int hashCode() {
        return (int) id;
    }

    @Override
    public String toString() {
        return "{id: " + id + ", latLong: " + latLong.toString() + "}";
    }
}

