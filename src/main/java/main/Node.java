package main;

/**
 * Created by Dylan on 14/03/17.
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
        if (!(obj instanceof Node)) {
            return false;
        }

        Node otherNode = (Node) obj;
        return otherNode.latLong.equals(latLong) &&
                otherNode.id == id;
    }
}

