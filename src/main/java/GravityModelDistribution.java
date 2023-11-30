import java.util.List;

public class GravityModelDistribution<T> {

    private final Node<T> root;

    private final double theta;

    /**
     * @param bodies The bodies to be inserted into the tree
     * @param theta The threshold value for when nodes are considered "far enough" to be considered as a combined
     *              unit rather than considering each body individually. Smaller theta is more accurate but more
     *              computationally intensive, and vice versa
     */
    public GravityModelDistribution(List<Body<T>> bodies, double theta) {
        if (bodies.isEmpty()) {
            throw new IllegalArgumentException("No bodies");
        }
        if (theta < 0 || Double.isNaN(theta) || Double.isInfinite(theta)) {
            throw new IllegalArgumentException("Invalid theta: " + theta);
        }

        this.theta = theta;
        Square bounds = this.getBoundingSquare(bodies);
        this.root = new Node<>(bodies.get(0), bounds);
        for (Body<T> b: bodies.subList(1, bodies.size())) {
            this.root.insert(b);
        }
    }

    /**
     * @param x The x coordinate of the reference point
     * @param y The y coordinate of the reference point
     * @return A random body weighted by the amount of gravity that each body exerts on the reference point
     */
    public Body<T> getRandomBody(double x, double y) {
        if (isInvalidDoubleArg(x)) throw new IllegalArgumentException("Invalid x: " + x);
        if (isInvalidDoubleArg(y)) throw new IllegalArgumentException("Invalid y: " + y);
        return this.root.getRandomBody(x, y, this.theta);
    }

    private boolean isInvalidDoubleArg(double arg) {
        return Double.isInfinite(arg) || Double.isNaN(arg);
    }

    private Square getBoundingSquare(List<Body<T>> bodies) {
        double minX = bodies.stream().map(b -> b.x).min(Double::compare).orElseThrow();
        double maxX = bodies.stream().map(b -> b.x).max(Double::compare).orElseThrow();
        double minY = bodies.stream().map(b -> b.y).min(Double::compare).orElseThrow();
        double maxY = bodies.stream().map(b -> b.y).max(Double::compare).orElseThrow();
        double lenX = maxX - minX;
        double lenY = maxY - minY;
        double midX = minX + lenX / 2;
        double midY = minY + lenY / 2;
        return new Square(midX, midY, Double.max(lenX, lenY));
    }
}
