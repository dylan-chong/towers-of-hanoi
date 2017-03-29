package junit.datastructures;

import main.datastructures.QuadTree;
import org.junit.Test;
import main.mapdata.location.Location;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.Assert.*;

/**
 * Created by Dylan on 20/03/17.
 * <p>
 * NOTE: Tests marked with "Behavioural test" will use the behavioural test
 * format "Given ... When ... Then ..." Method names will be
 * "testOrMethodName_someState"
 */
public class QuadTreeTest {

    /**
     * Behavioural test
     */
    @Test
    public void addAndContains_oneItem() {
        // Given
        LocationQuadTree tree = new LocationQuadTree();
        // ...
        testAddAndContains(
                tree,
                Arrays.asList(
                        new Location(0, 0)
                ),
                Arrays.asList(
                        new Location(1, 1),
                        new Location(-1, 1),
                        new Location(-1, -1),
                        new Location(1, -1)
                )
        );
    }

    /**
     * Behavioural test
     */
    @Test
    public void addAndContains_fiveItems() {
        // Given
        LocationQuadTree tree = new LocationQuadTree();
        // ...
        testAddAndContains(
                tree,
                Arrays.asList(
                        new Location(0, 0),
                        new Location(1, 1),
                        new Location(-1, 1),
                        new Location(-1, -1),
                        new Location(1, -1)
                ),
                Arrays.asList(
                        new Location(2, 2),
                        new Location(-2, 2),
                        new Location(-2, -2),
                        new Location(2, -2)
                )
        );
    }

    @Test
    public void closestDataTo_withOnlyOneItem_returnsItem() {
        LocationQuadTree tree = new LocationQuadTree();
        Location location = new Location(0, 0);
        tree.add(location);
        assertEquals(
                location,
                tree.closestDataTo(location)
        );
    }

    @Test
    public void closestDataTo_withEqualItem_returnsItem() {
        Location locationToCheck = new Location(0, 0);
        testClosestDataTo(
                Arrays.asList(
                        locationToCheck,
                        new Location(1, 1),
                        new Location(-1, 1),
                        new Location(-1, -1),
                        new Location(1, -1)
                ),
                locationToCheck,
                locationToCheck
        );
    }

    @Test
    public void closestDataTo_withTwoDifferentItemsInTree_returnsItem() {
        Location locationToCheck = new Location(1, 1);
        testClosestDataTo(
                Arrays.asList(
                        locationToCheck,
                        new Location(-1, -1)
                ),
                new Location(0.5, 0.5),
                locationToCheck
        );
    }

    @Test
    public void closestDataTo_withTwoDifferentItemsInTree2_returnsItem() {
        LocationQuadTree tree = new LocationQuadTree();
        Location locationToCheck = new Location(1, 1);
        Stream.of(
                new Location(-1, -1),
                locationToCheck
        ).forEach(tree::add);
        assertEquals(
                locationToCheck,
                tree.closestDataTo(new Location(0.5, 0.5))
        );
    }

    @Test
    public void closestDataTo_withTwoDifferentItemsInTree3_returnsItem() {
        LocationQuadTree tree = new LocationQuadTree();
        Location locationToCheck = new Location(-1, -1);
        Stream.of(
                new Location(1, 1),
                locationToCheck
        ).forEach(tree::add);
        assertEquals(
                locationToCheck,
                tree.closestDataTo(new Location(-0.5, -0.5))
        );
    }

    @Test
    public void closestDataTo_withTwoLevelsOfNodesAndInputCloseToSectionCenter1_returnsItem() {
        LocationQuadTree tree = new LocationQuadTree();
        Stream.of(
                new Location(0, 0),
                new Location(1, 1),
                new Location(-1, 1),
                new Location(-1, -1),
                new Location(1, -1)
        ).forEach(tree::add);
        assertEquals(
                new Location(1, 1),
                tree.closestDataTo(new Location(0.9, 0.9))
        );
    }

    @Test
    public void closestDataTo_withTwoLevelsOfNodesAndInputCloseToSectionCenter2_returnsItem() {
        LocationQuadTree tree = new LocationQuadTree();
        Stream.of(
                new Location(0, 0),
                new Location(1, 1),
                new Location(-1, 1),
                new Location(-1, -1),
                new Location(1, -1)
        ).forEach(tree::add);
        assertEquals(
                new Location(-1, 1),
                tree.closestDataTo(new Location(-0.9, 0.9))
        );
    }

    @Test
    public void closestDataTo_withTwoLevelsOfNodesAndInputCloseToSectionCenter3_returnsItem() {
        LocationQuadTree tree = new LocationQuadTree();
        Stream.of(
                new Location(0, 0),
                new Location(1, 1),
                new Location(-1, 1),
                new Location(-1, -1),
                new Location(1, -1)
        ).forEach(tree::add);
        assertEquals(
                new Location(-1, -1),
                tree.closestDataTo(new Location(-0.9, -0.9))
        );
    }

    @Test
    public void closestDataTo_withTwoLevelsOfNodesAndInputCloseToSectionCenter4_returnsItem() {
        LocationQuadTree tree = new LocationQuadTree();
        Stream.of(
                new Location(0, 0),
                new Location(1, 1),
                new Location(-1, 1),
                new Location(-1, -1),
                new Location(1, -1)
        ).forEach(tree::add);
        assertEquals(
                new Location(-1, 1),
                tree.closestDataTo(new Location(-0.9, 0.9))
        );
    }

    @Test
    public void closestDataTo_withTwoLevelsOfNodesAndInputCloseToCenter_returnsItem() {
        LocationQuadTree tree = new LocationQuadTree();
        Stream.of(
                new Location(0, 0),
                new Location(1, 1),
                new Location(-1, 1),
                new Location(-1, -1),
                new Location(1, -1)
        ).forEach(tree::add);
        assertEquals(
                new Location(0, 0),
                tree.closestDataTo(new Location(0.1, 0.1))
        );
    }

    @Test
    public void closestDataTo_withTwoLevelsOfNodesAndInputCloseToCenter2_returnsItem() {
        LocationQuadTree tree = new LocationQuadTree();
        Stream.of(
                new Location(0, 0),
                new Location(1, 1),
                new Location(-1, 1),
                new Location(-1, -1),
                new Location(1, -1)
        ).forEach(tree::add);
        assertEquals(
                new Location(0, 0),
                tree.closestDataTo(new Location(-0.1, 0.1))
        );
    }

    @Test
    public void closestDataTo_withTwoLevelsOfNodesAndInputCloseToCenter3_returnsItem() {
        LocationQuadTree tree = new LocationQuadTree();
        Stream.of(
                new Location(0, 0),
                new Location(1, 1),
                new Location(-1, 1),
                new Location(-1, -1),
                new Location(1, -1)
        ).forEach(tree::add);
        assertEquals(
                new Location(0, 0),
                tree.closestDataTo(new Location(-0.1, -0.1))
        );
    }

    @Test
    public void closestDataTo_withTwoLevelsOfNodesAndInputCloseToCenter4_returnsItem() {
        LocationQuadTree tree = new LocationQuadTree();
        Stream.of(
                new Location(0, 0),
                new Location(1, 1),
                new Location(-1, 1),
                new Location(-1, -1),
                new Location(1, -1)
        ).forEach(tree::add);
        assertEquals(
                new Location(0, 0),
                tree.closestDataTo(new Location(-0.1, 0.1))
        );
    }

    /**
     * BDD syntax continues from a real test
     */
    private void testAddAndContains(LocationQuadTree tree,
                                    List<Location> pointsToAdd,
                                    List<Location> fakePoints) {
        // And
        pointsToAdd.forEach(point -> assertFalse(tree.contains(point)));
        fakePoints.forEach(point -> assertFalse(tree.contains(point)));

        // When
        pointsToAdd.forEach(tree::add);

        // Then
        pointsToAdd.forEach(point -> assertTrue(tree.contains(point)));
        // And it should not contain any other points
        fakePoints.forEach(point -> assertFalse(tree.contains(point)));
    }

    private void testClosestDataTo(List<Location> locationsToAdd,
                                   Location locationToInput,
                                   Location idealLocation) {
        LocationQuadTree tree = new LocationQuadTree();
        locationsToAdd.forEach(tree::add);
        assertEquals(
                idealLocation,
                tree.closestDataTo(locationToInput)
        );
    }

    private static class LocationQuadTree extends QuadTree<Location> {
        public LocationQuadTree() {
            super((a, b) -> a.y - b.y, (a, b) -> a.x - b.x);
        }
    }
}
