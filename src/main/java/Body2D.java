public class Body2D<T> implements Body<T> {

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
    public Body2D(double mass, double x, double y, T value) {
        if (mass < 0 || Utils.isInvalidArg(mass)) throw new IllegalArgumentException("Illegal mass " + mass);
        if (Utils.isInvalidArg(x)) throw new IllegalArgumentException("Illegal x " + x);
        if (Utils.isInvalidArg(y)) throw new IllegalArgumentException("Illegal y " + y);

        this.mass = mass;
        this.x = x;
        this.y = y;
        this.value = value;
    }


    /**
     * @param other Another body
     * @return A new Body representing the combined centre of mass of both bodies. The returned
     *         body will have 'null' as its value.
     */
    public Body2D<T> plus(Body<T> other) {
        if (!(other instanceof Body2D)) {
            throw new IllegalArgumentException("Cannot combine Body2D with Body type: " + other.getClass());
        }
        Body2D<T> o = ((Body2D<T>) other);
        double newWeight = this.mass + o.mass;
        if (newWeight == 0) return new Body2D<>(0, 0, 0, null);
        return new Body2D<>(
                newWeight,
                (this.x * this.mass + o.x * o.mass) / newWeight,
                (this.y * this.mass + o.y * o.mass) / newWeight,
                null
        );
    }

    public double distanceTo(Body<T> other) {
        if (!(other instanceof Body2D)) {
            throw new IllegalArgumentException("Cannot compute distance to Body type: " + other.getClass());
        }
        Body2D<T> o = ((Body2D<T>) other);
        return this.distanceTo(o.x, o.y);
    }

    public double distanceTo(double x, double y) {
        double dx = x - this.x;
        double dy = y - this.y;
        return Math.sqrt(dx*dx + dy*dy);
    }

    public double computeGravForce(Body<T> other) {
        if (!(other instanceof Body2D)) {
            throw new IllegalArgumentException("Cannot compute force on Body type: " + other.getClass());
        }
        Body2D<T> o = ((Body2D<T>) other);
        if (this.mass == 0 || o.mass == 0) return 0.0;
        double r = this.distanceTo(o.x, o.y);
        if (r == 0) return 0;
        return this.mass * o.mass / (r*r);
    }

    @Override
    public String toString() {
        return "Body2D{" +
                "mass=" + mass +
                ", x=" + x +
                ", y=" + y +
                ", value=" + value +
                '}';
    }
}
