public abstract class GravityModelDistribution<T> {
    /**
     * @param x The x coordinate of the reference point
     * @param y The y coordinate of the reference point
     * @return A random body weighted by the amount of gravity that each body exerts on the reference point
     */
    public abstract Body<T> getRandomBody(double x, double y);

    protected boolean isInvalidDoubleArg(double arg) {
        return Double.isInfinite(arg) || Double.isNaN(arg);
    }
}
