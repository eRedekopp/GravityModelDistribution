package com.github.eRedekopp.GravityModelDistribution;

/**
 * A Body2D object specifically for GIS coordinates. This gives a more accurate measure of the distance between
 * points using the Haversine formula, but comes at the cost of substantially increased computational cost due
 * to the many trigonometric functions involved. Unless you need highly precise computations, it is probably a
 * better idea to use a plain Body2D with points projected by a map projection
 */
public class GISBody<T> extends Body<T> {

    private static final double EARTH_RADIUS_KM = 6371.0;

    public final double lat;

    public final double lon;

    public GISBody(double mass, double lat, double lon, T value) {
        super(mass, value);
        this.lat = lat;
        this.lon = lon;
    }

    @Override
    public GISBody<T> plus(Body<T> other) {
        if (!(other instanceof GISBody)) {
            throw new IllegalArgumentException("Can't combine GISBody with other Body type: " + other.getClass());
        }
        GISBody<T> o = ((GISBody<T>) other);
        double newWeight = this.mass + o.mass;
        if (newWeight == 0) return new GISBody<>(0, 0, 0, null);
        return new GISBody<>(
                newWeight,
                (this.lat * this.mass + o.lat * o.mass) / newWeight,
                (this.lon * this.mass + o.lon * o.mass) / newWeight,
                null
        );
    }

    /**
     * @param other Another body of the same type
     * @return The distance to the other body in kilometers
     */
    @Override
    public double distanceTo(Body<T> other) {
        if (!(other instanceof GISBody)) {
            throw new IllegalArgumentException("Can't combine GISBody with other Body type: " + other.getClass());
        }
        GISBody<T> o = ((GISBody<T>) other);
        // Haversine formula for great circle distance between two points
        double latDistance = Math.toRadians(o.lat - this.lat);
        double lonDistance = Math.toRadians(o.lon - this.lon);
        double a = Math.pow(Math.sin(latDistance / 2), 2)
                + Math.cos(Math.toRadians(o.lat))
                * Math.cos(Math.toRadians(this.lat))
                * Math.pow(Math.sin(lonDistance / 2), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return EARTH_RADIUS_KM * c;
    }
}
