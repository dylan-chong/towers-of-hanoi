package main.structures;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Created by Dylan on 20/03/17.
 *
 * @param <DataT> The type of data to store
 */
public class QuadTree<DataT> {
    private static final double MIN_SEARCH_RADIUS = 0.01;
    private static final double SEARCH_RADIUS_INCREASE_FACTOR = 2;

    private final PreciseComparator<DataT> verticalComparator, horizontalComparator;
    private Section rootSection;

    public QuadTree(PreciseComparator<DataT> verticalComparator,
                    PreciseComparator<DataT> horizontalComparator) {
        this.verticalComparator = verticalComparator;
        this.horizontalComparator = horizontalComparator;
    }

    public void add(DataT data) {
        if (data == null) {
            throw new NullPointerException("QuadTree does not accept null");
        }

        if (rootSection == null) {
            rootSection = new Section(data);
            return;
        }

        rootSection.addData(data);
    }

    public boolean contains(DataT data) {
        return rootSection != null && rootSection.contains(data);
    }

    public DataT closestDataTo(DataT data) {
        if (rootSection == null) {
            return null;
        }

        return rootSection.closestDataTo(data);
    }

    private class Section {
        /**
         * topLeft has most negative coordinates
         */
        private Section topLeft, topRight, bottomLeft, bottomRight;
        /**
         * Compare to this to find which section to find/put data
         */
        private final DataT data;

        public Section(DataT data) {
            this.data = data;
        }

        public void addData(DataT newData) {
            SectionField sectionField = findSectionFromData(newData);

            if (sectionField.get() == null) {
                sectionField.set(new Section(newData));
                return;
            }
            sectionField.get().addData(newData);
        }

        public boolean contains(DataT compareData) {
            if (verticalComparator.compare(compareData, data) == 0 &&
                    horizontalComparator.compare(data, data) == 0) {
                return true;
            }

            SectionField sectionField = findSectionFromData(compareData);
            // noinspection SimplifiableIfStatement
            if (sectionField.get() == null) {
                return false;
            }

            return sectionField.get().contains(compareData);
        }

        public DataT closestDataTo(DataT compareData) {
            double searchRadius = MIN_SEARCH_RADIUS;

            List<DataT> closeDatas;
            do {
                closeDatas = datasNear(compareData, searchRadius);
                searchRadius *= SEARCH_RADIUS_INCREASE_FACTOR;
            } while (closeDatas.isEmpty());

            // Find closest item
            closeDatas.sort(Comparator.comparingDouble(dataA ->
                    distanceBetween(compareData, dataA)
            ));
            return closeDatas.get(0);
        }

        private List<DataT> datasNear(DataT compareData,
                                      double maxDistance) {
            double verticalComparison =
                    verticalComparator.compare(compareData, data);
            double horizontalComparison =
                    horizontalComparator.compare(compareData, data);

            // Sections that could contain close data points (a section could
            // contain close data points if it has some area within the bounding
            // box. The box has width and height {@code 2 * maxDistance} and the
            // center at compareData.
            List<Section> possibleSections = new ArrayList<>();
            if (verticalComparison - maxDistance <= 0) {
                if (horizontalComparison - maxDistance <= 0) {
                    possibleSections.add(topLeft);
                }
                if (horizontalComparison + maxDistance > 0) {
                    possibleSections.add(topRight);
                }
            }
            if (verticalComparison + maxDistance > 0) {
                if (horizontalComparison - maxDistance <= 0) {
                    possibleSections.add(bottomLeft);
                }
                if (horizontalComparison + maxDistance > 0) {
                    possibleSections.add(bottomRight);
                }
            }

            List<DataT> closeDatas = possibleSections.stream()
                    .filter(Objects::nonNull)
                    .map(section -> section.datasNear(compareData, maxDistance))
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());

            if (Math.hypot(verticalComparison, horizontalComparison) <= maxDistance) {
                closeDatas.add(data);
            }

            return closeDatas;
        }


        private double distanceBetween(DataT a, DataT b) {
            return Math.hypot(
                    verticalComparator.compare(a, b),
                    horizontalComparator.compare(a, b)
            );
        }

        public SectionField findSectionFromData(DataT compareData) {
            double verticalComparison =
                    verticalComparator.compare(compareData, data);
            double horizontalComparison =
                    horizontalComparator.compare(compareData, data);

            Supplier<Section> get;
            Consumer<Section> set;

            if (verticalComparison <= 0) {
                // noinspection Duplicates
                if (horizontalComparison <= 0) {
                    get = () -> topLeft;
                    set = (section) -> topLeft = section;
                } else {
                    get = () -> topRight;
                    set = (section) -> topRight = section;
                }
            } else {
                // noinspection Duplicates
                if (horizontalComparison <= 0) {
                    get = () -> bottomLeft;
                    set = (section) -> bottomLeft = section;
                } else {
                    get = () -> bottomRight;
                    set = (section) -> bottomRight = section;
                }
            }

            return new SectionField() {
                public Section get() {
                    return get.get();
                }

                public void set(Section section) {
                    set.accept(section);
                }
            };
        }

        @Override
        public String toString() {
            return "[data: " + data.toString() + "]";
        }

        private abstract class SectionField {
            abstract Section get();
            abstract void set(Section section);
        }
    }

    public interface PreciseComparator<DataT> {
        /**
         * Same as {@link Comparator#compare(Object, Object)} but the comparison
         * returns an exact difference
         * @param a
         * @param b
         * @return
         */
        double compare(DataT a, DataT b);
    }
}
