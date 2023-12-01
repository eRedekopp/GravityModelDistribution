import java.util.List;
import java.util.Random;

public class SimpleGravityModelDistribution<T> extends GravityModelDistribution<T> {

    private final List<Body<T>> bodies;

    private final Random rng;

    public SimpleGravityModelDistribution(List<Body<T>> bodies) {
        if (bodies.isEmpty()) throw new IllegalArgumentException("No bodies");
        this.bodies = bodies;
        this.rng = new Random();
    }

    @Override
    public Body<T> getRandomBody(double x, double y) {
        if (this.isInvalidDoubleArg(x)) throw new IllegalArgumentException("Invalid x: " + x);
        if (this.isInvalidDoubleArg(y)) throw new IllegalArgumentException("Invalid y: " + y);
        double[] forces = this.bodies.parallelStream()
                .mapToDouble(b -> b.computeGravityWeight(x, y))
                .toArray();
        int i = Utils.chooseRandomIndexByWeight(forces, rng.nextDouble());
        return this.bodies.get(i);
    }
}
