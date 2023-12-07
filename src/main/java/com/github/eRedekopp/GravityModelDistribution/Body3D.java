package com.github.eRedekopp.GravityModelDistribution;

public class Body3D<T> extends Body<T> {

    public final double x;

    public final double y;

    public final double z;

    public Body3D(double mass, double x, double y, double z, T value) {
        super(mass, value);
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public Body<T> plus(Body<T> other) {
        if (!(other instanceof Body3D)) {
            throw new IllegalArgumentException("Can't combine Body3D with other Body type: " + other.getClass());
        }
        Body3D<T> o = (Body3D<T>) other;
        double newMass = this.mass + o.mass;
        if (newMass == 0) return new Body3D<>(0, 0, 0, 0, null);
        return new Body3D<>(
                newMass,
                (this.x * this.mass + o.x * o.mass) / newMass,
                (this.y * this.mass + o.y * o.mass) / newMass,
                (this.z * this.mass + o.z * o.mass) / newMass,
                null
        );
    }

    @Override
    public double distanceTo(Body<T> other) {
        if (!(other instanceof Body3D)) {
            throw new IllegalArgumentException("Can't get distance to other Body type: " + other.getClass());
        }
        Body3D<T> o = (Body3D<T>) other;
        double dx = o.x - this.x;
        double dy = o.y - this.y;
        double dz = o.z - this.z;
        return Math.sqrt(dx*dx + dy*dy + dz*dz);
    }
}
