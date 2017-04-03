package swen221.shapedrawer.shapes;

/**
 * Created by Dylan on 3/04/17.
 */
public class UnionShape extends CompositeShape {
	public UnionShape(Shape shape1, Shape shape2) {
		super(shape1, shape2);
	}

	@Override
	public boolean contains(int x, int y) {
		return shape1.contains(x, y) || shape2.contains(x, y);
	}

	@Override
	public Rectangle boundingBox() {
		return findBoundingBox(BoundingBoxStrategy.REQUIRE_ONE);
	}
}
