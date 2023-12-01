public class Body<T> {

    /**
     * The mass of this Body, in whichever units are being used
     */
    public final double mass;

    /**
     * The X value of this body's centre of mass
     */
    public final double x;

    /**
     * The Y value of this body's centre of mass
     */
    public final double y;

    /**
     * The value which this Body represents
     */
    public final T value;

    /**
     * @param mass The mass of this body
     * @param x The X value of this body's centre of mass
     * @param y The Y value of this body's centre of mass
     * @param value The value which this body represents
     */
    public Body(double mass, double x, double y, T value) {
        if (mass < 0 || this.isInvalidArg(mass)) throw new IllegalArgumentException("Illegal mass " + mass);
        if (this.isInvalidArg(x)) throw new IllegalArgumentException("Illegal x " + x);
        if (this.isInvalidArg(y)) throw new IllegalArgumentException("Illegal y " + y);

        this.mass = mass;
        this.x = x;
        this.y = y;
        this.value = value;
    }

    private boolean isInvalidArg(double arg) {
        return Double.isInfinite(arg) || Double.isNaN(arg);
    }

    /**
     * @param other Another body
     * @return A new Body representing the combined centre of mass of both bodies. The returned
     *         body will have 'null' as its value.
     */
    public Body<T> plus(Body<T> other) {
        double newWeight = this.mass + other.mass;
        if (newWeight == 0) return new Body<>(0, 0, 0, null);
        return new Body<>(
                newWeight,
                (this.x * this.mass + other.x * other.mass) / newWeight,
                (this.y * this.mass + other.y * other.mass) / newWeight,
                null
        );
    }

    public double distanceTo(Body<T> other) {
        return this.distanceTo(other.x, other.y);
    }

    public double distanceTo(double x, double y) {
        double dx = x - this.x;
        double dy = y - this.y;
        return Math.sqrt(dx*dx + dy*dy);
    }

    /**
     * Compute the amount of gravity (with G factored out) that this body exerts on a point
     * mass centred at (x,y)
     */
    public double computeGravityWeight(double x, double y) {
        if (this.mass == 0) return 0.0;
        double r = this.distanceTo(x, y);
        if (r == 0) r = 1; // TODO does this make sense?
        return this.mass / (r*r);
    }

    @Override
    public String toString() {
        return "Body{" +
                "mass=" + mass +
                ", x=" + x +
                ", y=" + y +
                ", value=" + value +
                '}';
    }
}
