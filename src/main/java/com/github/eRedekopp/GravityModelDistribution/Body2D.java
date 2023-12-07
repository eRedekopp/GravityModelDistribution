package com.github.eRedekopp.GravityModelDistribution;

public class Body2D<T> extends Body<T> {

    /**
     * The X value of this body's centre of mass
     */
    public final double x;

    /**
     * The Y value of this body's centre of mass
     */
    public final double y;


    /**
     * @param mass The mass of this body
     * @param x The X value of this body's centre of mass
     * @param y The Y value of this body's centre of mass
     * @param value The value which this body represents
     */
    public Body2D(double mass, double x, double y, T value) {
        super(mass, value);
        if (Utils.isInvalidArg(x)) throw new IllegalArgumentException("Illegal x " + x);
        if (Utils.isInvalidArg(y)) throw new IllegalArgumentException("Illegal y " + y);

        this.x = x;
        this.y = y;
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
        double dx = o.x - this.x;
        double dy = o.y - this.y;
        return Math.sqrt(dx*dx + dy*dy);
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
