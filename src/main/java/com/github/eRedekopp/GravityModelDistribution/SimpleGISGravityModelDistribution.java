package com.github.eRedekopp.GravityModelDistribution;

import java.util.List;
import java.util.Random;

public class SimpleGISGravityModelDistribution<T> extends SimpleGravityModelDistribution<T, GISBody<T>> {
    public SimpleGISGravityModelDistribution(List<GISBody<T>> bodies, Random rng) {
        super(bodies, rng);
    }

    public SimpleGISGravityModelDistribution(List<GISBody<T>> bodies) {
        super(bodies);
    }
}
