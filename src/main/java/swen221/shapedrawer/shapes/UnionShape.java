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
		Rectangle box1 = shape1.boundingBox();
		Rectangle box2 = shape2.boundingBox();
		int minX = Math.min(box1.x, box2.x);
		int minY = Math.min(box1.y, box2.y);

		int maxX = Math.max(box1.x + box1.width, box2.x + box2.width);
		int maxY = Math.max(box1.y + box1.height, box2.y + box2.height);

		return new Rectangle(minX, minY, maxX - minX, maxY - minY);
	}
}
