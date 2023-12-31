package com.github.eRedekopp.GravityModelDistribution;

import java.util.List;
import java.util.Random;

public class SimpleGravityModelDistribution<T, B extends Body<T>> implements GravityModelDistribution<T, B> {

    private final List<B> bodies;

    private final Random rng;

    public SimpleGravityModelDistribution(List<B> bodies, Random rng) {
        if (bodies.isEmpty()) throw new IllegalArgumentException("No bodies");
        this.bodies = bodies;
        this.rng = rng;
    }

    public SimpleGravityModelDistribution(List<B> bodies) {
        this(bodies, new Random());
    }

    @Override
    public B getRandomBody(B ref) {
        if (ref.mass == 0) throw new IllegalArgumentException("Reference point cannot have 0 mass");
        double[] forces = this.bodies.parallelStream()
                .mapToDouble(b -> b.computeGravForce(ref))
                .toArray();
        int i = Utils.chooseRandomIndexByWeight(forces, rng.nextDouble());
        return this.bodies.get(i);
    }
}
