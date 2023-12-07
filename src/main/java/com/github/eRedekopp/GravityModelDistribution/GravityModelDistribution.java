package com.github.eRedekopp.GravityModelDistribution;

public interface GravityModelDistribution<T, B extends Body<T>> {
    /**
     * @param ref The reference point used to compute the weights of all bodies in the distribution
     * @return A random body weighted by the amount of gravity that each body exerts on the reference point
     */
    B getRandomBody(B ref);
}
