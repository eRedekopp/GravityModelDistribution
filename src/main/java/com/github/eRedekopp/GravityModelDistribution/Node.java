package com.github.eRedekopp.GravityModelDistribution;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class Node<T> {

    /**
     * The child nodes of this node, if they exist.
     */
    protected final Map<Quadrant, Node<T>> children;

    /**
     * A Body representing the centre of mass of the entire subtree headed at this node. If this is a leaf node,
     * the Body will contain a T containing the value associated with the body, otherwise it is null
     */
    private Body2D<T> body;

    /**
     * The RNG used for getRandomBody
     */
    private final Random rng;

    /**
     * The area represented by the entire subtree headed at this node
     */
    private final Square area;

    /**
     * @param body A Body containing the initial object that will be represented by this Node while it is a leaf node.
     *             After subsequent additions, the Body contained in this node will be the accumulation of many bodies.
     * @param area The area represented by this node and its entire subtree
     */
    public Node(Body2D<T> body, Square area) {
        this(body, area, new Random());
    }

    /**
     * @param body A Body containing the initial object that will be represented by this Node while it is a leaf node.
     *             After subsequent additions, the Body contained in this node will be the accumulation of many bodies.
     * @param area The area represented by this node and its entire subtree
     * @param rng The RNG for use in getRandomBody
     */
    public Node(Body2D<T> body, Square area, Random rng) {
        if (body == null) throw new IllegalArgumentException("Null body");
        if (area == null) throw new IllegalArgumentException("Null area");
        if (!area.contains(body.x, body.y)) throw new IllegalArgumentException(
                String.format(
                        "Body at (%f, %f) is not contained within this node's area %s", body.x, body.y, area
                )
        );

        this.body = body;
        this.area = area;
        this.children = new HashMap<>(4);
        this.rng = rng;
    }

    /**
     * @return A body representing the centre of mass of the entire subtree headed at this node
     */
    public Body2D<T> getCentreMass() {
        return this.body;
    }

    /**
     * Get a random Body from one of the nodes within the subtree headed at this node. The likelihood
     * of getting each body is weighted by the amount of gravitational force it exerts on a point mass at
     * the given (x, y).
     * @param x The x value of the reference point
     * @param y The y value of the reference point
     * @param theta A tunable parameter deciding the balance between performance and accuracy. Smaller theta is more
     *              accurate and larger theta is more performant.
     */
    public Body2D<T> getRandomBody(double x, double y, double theta) {
        Node<T> selected = this;
        double rand = this.rng.nextDouble();
        do {
            List<Node<T>> candidateNodes = selected.getCandidateNodes(x, y, theta);
            selected = this.chooseRandomGravityWeightedNode(x, y, candidateNodes, rand);
            theta /= 2;
        } while (!selected.isLeafNode());
        return selected.body;
    }

    private List<Node<T>> getCandidateNodes(double x, double y, double theta) {
        // This node is either a leaf, or the combined centre of mass is far enough that we consider it all together
        if (this.isLeafNode()
                || (this.area.sideLength / this.body.distanceTo(new Body2D<>(1, x, y, null))) < theta
        ) {
            return List.of(this);
        }
        else {
            return this.children.values().stream()
                    .map(subtree -> subtree.getCandidateNodes(x, y, theta))
                    .reduce(
                            List.of(),
                            (l1, l2) -> Stream.concat(l1.stream(), l2.stream()).collect(Collectors.toUnmodifiableList())
                    );
        }
    }

    private Node<T> chooseRandomGravityWeightedNode(double x, double y, List<Node<T>> candidates, double rand) {
        Body2D<T> ref = new Body2D<>(1, x, y, null);
        double[] forces = candidates.stream()
                .mapToDouble(b -> b.body.computeGravForce(ref))
                .toArray();
        int i = Utils.chooseRandomIndexByWeight(forces,  rand);
        return candidates.get(i);
    }

    /**
     * Insert a Body into the subtree headed at this node.
     */
    public void insert(Body2D<T> b) {
        // If this is a leaf node, then our Body actually contains a real value.
        // Insert that Body into the subtree first and then insert b.
        if (this.isLeafNode()) {
            this.putBody(this.body);
        }
        this.body = this.body.plus(b);
        this.putBody(b);
    }

    /**
     * @return Is this a leaf node? If yes, its Body contains an actual value instead of an accumulated value.
     */
    public boolean isLeafNode() {
        return this.children.isEmpty();
    }

    /**
     * Add a body into the appropriate child node given its location
     */
    private void putBody(Body2D<T> b) {
        Quadrant quadrant = this.area.getQuadrant(b.x, b.y);
        if (!this.children.containsKey(quadrant)) {
            this.children.put(quadrant, this.makeNewNode(b, this.area.getSubSquare(quadrant)));
        }
        else {
            this.children.get(quadrant).insert(b);
        }
    }

    protected Node<T> makeNewNode(Body2D<T> body, Square area) {
        return new Node<>(body, area, this.rng);
    }

    @Override
    public String toString() {
        return "Node{" +
                "body=" + body +
                ", area=" + area +
                ", children= " + children.keySet() +
                '}';
    }
}
