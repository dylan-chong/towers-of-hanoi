package renderer;

/**
 * Struct to represent a smallest and largest value
 */
public class MinMax {
    private float min = Float.POSITIVE_INFINITY;
    private float max = Float.NEGATIVE_INFINITY;

    void update(float value) {
        if (value < min) min = value;
        if (value > max) max = value;
    }

    public float getMin() {
        return min;
    }

    public float getMax() {
        return max;
    }
}
