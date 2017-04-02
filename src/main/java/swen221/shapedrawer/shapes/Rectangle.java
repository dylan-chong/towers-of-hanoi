package swen221.shapedrawer.shapes;

/**
 * Created by Dylan on 2/04/17.
 */
public class Rectangle implements Shape {
	public final int x, y, width, height;

	public Rectangle(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	@Override
	public boolean contains(int pointX, int pointY) {
		if (pointX < this.x) return false;
		if (pointX >= this.x + width) return false;
		if (pointY < this.y) return false;
		if (pointY >= this.y + height) return false;
		return true;
	}

	@Override
	public Rectangle boundingBox() {
		return this;
	}
}
