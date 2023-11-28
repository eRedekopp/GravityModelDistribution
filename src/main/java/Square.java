public class Square {

    /**
     * X value of the middle of the square
     */
    public final double midX;

    /**
     * Y value of the middle of the square
     */
    public final double midY;

    /**
     * The side length of the square
     */
    public final double sideLength;


    public Square(double x, double y, double sideLength) {
        if (sideLength < 0 || this.isInvalidArgument(sideLength))
            throw new IllegalArgumentException("Illegal side length " + sideLength);
        if (this.isInvalidArgument(x)) throw new IllegalArgumentException("Illegal x " + x);
        if (this.isInvalidArgument(y)) throw new IllegalArgumentException("Illegal y " + y);

        this.midX = x;
        this.midY = y;
        this.sideLength = sideLength;
    }

    private boolean isInvalidArgument(double d) {
        return Double.isInfinite(d) || Double.isNaN(d);
    }

    /**
     * @return Does this square contain the given point?
     */
    public boolean contains(double x, double y) {
        if (this.isInvalidArgument(x)) throw new IllegalArgumentException("Illegal x " + x);
        if (this.isInvalidArgument(y)) throw new IllegalArgumentException("Illegal y " + y);
        double halfSide = sideLength / 2;
        return x >= this.midX - halfSide
                && x <= this.midX + halfSide
                && y >= this.midY - halfSide
                && y <= this.midY + halfSide;
    }

    /**
     * @return Which quadrant of the square contains the given point.
     * If the point lies on the axis, it counts as North and East.
     * Note: does not check whether the square contains the point
     */
    public Quadrant getQuadrant(double x, double y) {
        if (this.isInvalidArgument(x)) throw new IllegalArgumentException("Illegal x " + x);
        if (this.isInvalidArgument(y)) throw new IllegalArgumentException("Illegal y " + y);
        if (x < midX) {
            if (y < midY) {
                return Quadrant.SOUTHWEST;
            }
            else {
                return Quadrant.NORTHWEST;
            }
        }
        else {
            if (y < midY) {
                return Quadrant.SOUTHEAST;
            }
            else {
                return Quadrant.NORTHEAST;
            }
        }
    }

    /**
     * @return A new square representing the given quadrant of this square
     */
    public Square getSubSquare(Quadrant quadrant) {
        double halfSide = this.sideLength / 2;
        double quarterSide = this.sideLength / 4;
        switch (quadrant) {
            case NORTHWEST:
                return new Square(this.midX - quarterSide, this.midY + quarterSide, halfSide);
            case NORTHEAST:
                return new Square(this.midX + quarterSide, this.midY + quarterSide, halfSide);
            case SOUTHWEST:
                return new Square(this.midX - quarterSide, this.midY - quarterSide, halfSide);
            case SOUTHEAST:
                return new Square(this.midX + quarterSide, this.midY - quarterSide, halfSide);
            default:
                throw new IllegalArgumentException("Invalid quadrant: " + quadrant);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Square)) return false;
        Square other = (Square) o;
        return this.midX == other.midX
                && this.midY == other.midY
                && this.sideLength == other.sideLength;
    }

    @Override
    public String toString() {
        return "Square{" +
                "midX=" + midX +
                ", midY=" + midY +
                ", sideLength=" + sideLength +
                '}';
    }
}
