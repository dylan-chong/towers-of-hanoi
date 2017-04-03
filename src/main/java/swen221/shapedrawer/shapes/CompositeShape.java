package swen221.shapedrawer.shapes;

import java.util.function.BiFunction;

/**
 * Created by Dylan on 3/04/17.
 */
public abstract class CompositeShape implements Shape {

	protected final Shape shape1;
	protected final Shape shape2;

	public CompositeShape(Shape shape1,
						  Shape shape2) {
		this.shape1 = shape1;
		this.shape2 = shape2;
	}

	public interface Factory {
		CompositeShape create(Shape shape1, Shape shape2);
	}

	protected Rectangle findBoundingBox(BoundingBoxStrategy strategy) {
		Rectangle box1 = shape1.boundingBox();
		Rectangle box2 = shape2.boundingBox();

		BiFunction<Integer, Integer, Integer> minOrdinateFinder = Math::min;
		BiFunction<Integer, Integer, Integer> maxOrdinateFinder = Math::max;
		if (strategy == BoundingBoxStrategy.REQUIRE_BOTH) {
			BiFunction<Integer, Integer, Integer> temp = maxOrdinateFinder;
			maxOrdinateFinder = minOrdinateFinder;
			minOrdinateFinder = temp;
		}

		int minX = minOrdinateFinder.apply(box1.x, box2.x);
		int minY = minOrdinateFinder.apply(box1.y, box2.y);

		int maxX = maxOrdinateFinder.apply(
				box1.x + box1.width - 1,
				box2.x + box2.width - 1
		);
		int maxY = maxOrdinateFinder.apply(
				box1.y + box1.height - 1,
				box2.y + box2.height - 1
		);

		if (maxX < minX) maxX = minX;
		if (maxY < minY) maxY = minY;

		return new Rectangle(minX, minY, maxX - minX + 1, maxY - minY + 1);
	}

	protected enum BoundingBoxStrategy {
		/**
		 * Requires only one of the shapes at a point to consider that point
		 * part of the bounds
		 */
		REQUIRE_ONE, // default
		REQUIRE_BOTH
	}

}
