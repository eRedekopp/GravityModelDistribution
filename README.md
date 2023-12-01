# GravityModelDistribution

A Gravity Model probability distribution implementation in Java. The GravityModelDistribution is
initialized by providing one or more bodies to consider. A body has an item which it represents, 
a mass, and a spatial location in N-Dimensional space. Given a reference body at some location, the 
GravityModelDistribution implements a weighted discrete random distribution over the bodies, where 
the weights for each body are determined by the amount of gravitational force they exert on the 
reference body. If the reference point has an identical location to a body in the distribution
(or if it simply is one of the bodies in the distribution), the body has a probability of 0 of 
being selected.

The bodies need not represent physical bodies such as planetary bodies, but can represent any object
that has a location in real or abstract space, and some abstract concept of "mass". For example, the
motivation for this code was selecting a random city where the "mass" is a city's population.

## Provided Implementations

This repository includes only bodies in 2D space, and comes with two implementations of the GravityModelDistribution 
interface. 

SimpleGravityModelDistribution is a simple O(N) computation over all bodies, parallelized with Streams for quick execution. 
This implementation could be easily extended by implementing a new Body type for your use case, such as computing the gravity
model in higher dimensions. 

QuadtreeGravityModelDistribution uses a Quadtree and a variation on the Barnes-Hut algorithm to approximate the distribution and 
perform better with a large number of bodies. Due to the nature of Quadtrees, this implementation only works in 2D space. 
So far, test results have been disappointing and it has served as a poor approximation of the distribution unless the parameter theta 
is effectively zero. The performance tends to be far worse than SimpleGravityModelDistribution in all cases tested, and it is highly
recommended to avoid this implementation.
