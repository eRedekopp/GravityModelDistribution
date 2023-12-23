package com.github.eRedekopp.GravityModelDistribution;

import java.util.List;
import java.util.Random;

public class Simple2DGravityModelDistribution<T> extends SimpleGravityModelDistribution<T, Body2D<T>>{
    public Simple2DGravityModelDistribution(List<Body2D<T>> bodies, Random rng) {
        super(bodies, rng);
    }

    public Simple2DGravityModelDistribution(List<Body2D<T>> bodies) {
        super(bodies);
    }
}
