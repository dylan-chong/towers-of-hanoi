package swen221.shapedrawer.shapes;

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
}
