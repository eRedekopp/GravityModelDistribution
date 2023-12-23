package com.github.eRedekopp.GravityModelDistribution;

import java.util.List;
import java.util.Random;

public class Simple3DGravityModelDistribution<T> extends SimpleGravityModelDistribution<T, Body3D<T>> {
    public Simple3DGravityModelDistribution(List<Body3D<T>> bodies, Random rng) {
        super(bodies, rng);
    }

    public Simple3DGravityModelDistribution(List<Body3D<T>> bodies) {
        super(bodies);
    }
}
