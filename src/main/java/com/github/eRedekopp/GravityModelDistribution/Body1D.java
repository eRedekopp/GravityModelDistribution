package com.github.eRedekopp.GravityModelDistribution;

public class Body1D<T> extends Body<T> {

    public final double x;

    public Body1D(double mass, double x, T value) {
        super(mass, value);
        this.x = x;
    }

    @Override
    public Body1D<T> plus(Body<T> other) {
        if (!(other instanceof Body1D)) {
            throw new IllegalArgumentException("Cannot compute distance to Body type: " + other.getClass());
        }
        return new Body1D<>(
                this.mass + other.mass,
                this.x + ((Body1D<T>) other).x,
                null
        );
    }

    @Override
    public double distanceTo(Body<T> other) {
        if (!(other instanceof Body1D)) {
            throw new IllegalArgumentException("Cannot compute distance to Body type: " + other.getClass());
        }
        return Math.abs(this.x - ((Body1D<T>) other).x);
    }
}
