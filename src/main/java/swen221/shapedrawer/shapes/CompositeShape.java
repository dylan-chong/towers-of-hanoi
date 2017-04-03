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

		int maxX = maxOrdinateFinder.apply(box1.x + box1.width, box2.x + box2.width);
		int maxY = maxOrdinateFinder.apply(box1.y + box1.height, box2.y + box2.height);

		return new Rectangle(minX, minY, maxX - minX, maxY - minY);
	}

	protected enum BoundingBoxStrategy {
		REQUIRE_ONE, // default
		REQUIRE_BOTH
	}

}
