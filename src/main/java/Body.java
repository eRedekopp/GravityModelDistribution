public abstract class Body<T> {

    /**
     * The mass of this Body, in whichever units are being used
     */
    public final double mass;

    /**
     * The value which this Body represents
     */
    public final T value;

    public Body(double mass, T value) {
        if (mass < 0 || Utils.isInvalidArg(mass)) throw new IllegalArgumentException("Illegal mass " + mass);
        this.mass = mass;
        this.value = value;
    }

    /**
     * @param other Another Body of the same type
     * @return A new Body representing the centre of mass of this body and the other one. The returned body will have
     *         a null value.
     */
    public abstract Body<T> plus(Body<T> other);

    /**
     * @param other Another body of the same type
     * @return The distance to the other body, in whichever units are being used.
     */
    public abstract double distanceTo(Body<T> other);

    /**
     * @param other Another body of the same type
     * @return The gravitational force between the two bodies with G factored out
     */
    public double computeGravForce(Body<T> other) {
        if (this.mass == 0 || other.mass == 0) return 0.0;
        double r = this.distanceTo(other);
        if (r == 0) return 0;
        return this.mass * other.mass / (r*r);
    }
}
