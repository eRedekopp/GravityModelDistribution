package com.github.eRedekopp.GravityModelDistribution;

import java.util.List;
import java.util.Random;

public class Simple1DGravityModelDistribution<T> extends SimpleGravityModelDistribution<T, Body1D<T>> {
    public Simple1DGravityModelDistribution(List<Body1D<T>> bodies, Random rng) {
        super(bodies, rng);
    }

    public Simple1DGravityModelDistribution(List<Body1D<T>> bodies) {
        super(bodies);
    }
}
