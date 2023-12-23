package com.github.eRedekopp.GravityModelDistribution;

import java.util.List;
import java.util.Random;

public class QuadtreeGravityModelDistribution<T> implements GravityModelDistribution<T, Body2D<T>> {

    private final Node<T> root;

    private final double theta;

    private final Random rng;

    /**
     * @param bodies The bodies to be inserted into the tree
     * @param theta The threshold value for when nodes are considered "far enough" to be considered as a combined
     *              unit rather than considering each body individually. Smaller theta is more accurate but more
     *              computationally intensive, and vice versa
     * @param rng The random number generator to be used
     */
    public QuadtreeGravityModelDistribution(List<Body2D<T>> bodies, double theta, Random rng) {
        if (bodies.isEmpty()) {
            throw new IllegalArgumentException("No bodies");
        }
        if (theta < 0 || Utils.isInvalidArg(theta)) {
            throw new IllegalArgumentException("Invalid theta: " + theta);
        }

        this.theta = theta;
        this.rng = rng;
        Square bounds = this.getBoundingSquare(bodies);
        this.root = new Node<>(bodies.get(0), bounds, rng);
        for (Body2D<T> b: bodies.subList(1, bodies.size())) {
            this.root.insert(b);
        }
    }

    public QuadtreeGravityModelDistribution(List<Body2D<T>> bodies, double theta) {
        this(bodies, theta, new Random());
    }

    @Override
    public Body2D<T> getRandomBody(Body2D<T> ref) {
        return this.root.getRandomBody(ref.x, ref.y, this.theta);
    }

    private Square getBoundingSquare(List<Body2D<T>> bodies) {
        double minX = bodies.stream().map(b -> b.x).min(Double::compare).orElseThrow();
        double maxX = bodies.stream().map(b -> b.x).max(Double::compare).orElseThrow();
        double minY = bodies.stream().map(b -> b.y).min(Double::compare).orElseThrow();
        double maxY = bodies.stream().map(b -> b.y).max(Double::compare).orElseThrow();
        double lenX = maxX - minX;
        double lenY = maxY - minY;
        double midX = minX + lenX / 2;
        double midY = minY + lenY / 2;
        return new Square(midX, midY, Double.max(lenX, lenY));
    }
}
