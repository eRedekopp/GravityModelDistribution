import java.util.*;
import java.util.stream.Collectors;

/**
 * The nodes that make up the quadtree or a subtree thereof. To extend this class, override the computeGravityWeight
 * and makeNewNode functions, and define your own custom subclass of Body
 * @param <T> The type of data stored in the Bodies used in this node
 */
public class Node<T> {

    /**
     * A Body with zero mass that exerts no gravitational force
     */
    private final Body<T> EMPTY_BODY = new Body<>(0, 0, 0, null);

    /**
     * The child nodes of this node, if they exist.
     */
    protected final Map<Quadrant, Node<T>> children;

    /**
     * A Body representing the centre of mass of the entire subtree headed at this node. If this is a leaf node,
     * the Body will contain a T containing the value associated with the body, otherwise it is null
     */
    private Body<T> body;

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
    public Node(Body<T> body, Square area) {
        this(body, area, new Random());
    }

    /**
     * @param body A Body containing the initial object that will be represented by this Node while it is a leaf node.
     *             After subsequent additions, the Body contained in this node will be the accumulation of many bodies.
     * @param area The area represented by this node and its entire subtree
     * @param rng The RNG for use in getRandomBody
     */
    public Node(Body<T> body, Square area, Random rng) {
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
    public Body<T> getCentreMass() {
        return this.body;
    }

    /**
     * Get a random Body from one of the nodes within the subtree headed at this node. The likelihood
     * of getting each body is weighted by the amount of gravitational force it exerts on a point mass at
     * the given (x, y)
     */
    public Body<T> getRandomBody(double x, double y) {
        Body<T> result = this.getRandomBody(x, y, EMPTY_BODY, this.rng.nextDouble());
        assert result != null;
        assert result.value != null;
        return result;
    }

    /**
     * Internal method to get a random body. Recurse until we get to the node containing (x,y), then
     * either select the body for that node or one from the upper nodes.
     *
     * @param x The x value of the reference point
     * @param y The y value of the reference point
     * @param cum The cumulative centre of mass of all other bodies that are ABOVE this node in the tree
     * @param rand A uniform random double from 0 to 1, so that we only have to generate it once
     * @return A random body from this subtree, or null if we chose 'cum' instead
     */
     protected Body<T> getRandomBody(double x, double y, Body<T> cum, double rand) {
        Quadrant quadrant = this.area.getQuadrant(x, y);

        if (!this.children.containsKey(quadrant)) {
            // Base case: this is the bottommost node that contains (x,y)
            List<Body<T>> candidateList = List.of(this.body, cum);
            int resultIdx = this.chooseGravityWeightedRandomIndex(x, y, candidateList, rand);
            if (resultIdx == 0) {
                // We chose this node instead of cum
                if (this.isLeafNode()) {
                    // We have no children and this Node contains an actual value. Return our body.
                    return this.body;
                }
                else {
                    // We have one or more children that *don't* contain (x,y).
                    // Select the body from this node's subtree
                    List<Node<T>> nodes = this.children.values().stream().collect(Collectors.toUnmodifiableList());
                    candidateList = nodes.stream()
                            .map(Node::getCentreMass)
                            .collect(Collectors.toUnmodifiableList());
                    resultIdx = this.chooseGravityWeightedRandomIndex(x, y, candidateList, rand);
                    Node<T> selectedNode = nodes.get(resultIdx);
                    // Don't send a cumulative weight because we MUST select one from the subtree
                    Body<T> result = selectedNode.getRandomBody(x, y, EMPTY_BODY, rand);
                    assert result != null;
                    return result;
                }
            }
            else {
                // We chose cum instead of this node. Return null to indicate that to the caller.
                return null;
            }
        }
        else {
            // Recursive case: accumulate centre of mass and recurse down to the quadrant that contains x,y
            Node<T> childContainingPoint = this.children.get(quadrant);
            Body<T> newCum = this.children
                    .values()
                    .stream()
                    .filter(node -> node != childContainingPoint)
                    .map(Node::getCentreMass)
                    .reduce(EMPTY_BODY, Body::plus)
                    .plus(cum);
            Body<T> result = childContainingPoint.getRandomBody(x, y, newCum, rand);
            if (result == null) {
                // We didn't select one from the subtree. Choose between one of our other children and cum.
                List<Node<T>> otherChildren = this.children.keySet().stream()
                        .filter(k -> k != quadrant)
                        .map(this.children::get)
                        .collect(Collectors.toUnmodifiableList());
                List<Body<T>> candidates = otherChildren.stream()
                        .map(Node::getCentreMass)
                        .collect(Collectors.toList());
                int idxOfCum = candidates.size();
                candidates.add(idxOfCum, cum);
                if (candidates.stream().map(b -> b.mass).reduce(Double::sum).orElseThrow() == 0) return null;
                int resultIdx = this.chooseGravityWeightedRandomIndex(x, y, candidates, rand);
                if (resultIdx == idxOfCum) {
                    // We chose cum instead of one of our children. Return null to indicate that to the caller
                    return null;
                }
                else {
                    // We chose one of our children instead of cum. Get a random body from the subtree.
                    // Don't send a cumulative weight because we MUST select one from the subtree
                    Node<T> selectedNode = otherChildren.get(resultIdx);
                    result = selectedNode.getRandomBody(x, y, EMPTY_BODY, rand);
                    assert result != null;
                    return result;
                }
            }
            else {
                // We selected one from the subtree. Pass it up to the caller
                return result;
            }
        }
    }

    /**
     * Choose a random index from the list of bodies weighted by their gravity on point (x,y). This is implemented
     * by designating certain ranges within [0,1] as belonging to each body based on their weight. Whichever one of
     * these ranges `rand` falls into is the index that is returned.
     *
     * <p/>
     * For example:
     * <tt>
     * [[_____b1_____][__b2__][____________b3____________]]
     * 0========================.5========================1
     * </tt>
     * <ul>
     *     <li>Rand = 0.1 -> return b1</li>
     *     <li>Rand = 0.4 -> return b2</li>
     *     <li>Rand = 0.7 -> return b3</li>
     * </ul>
     *
     * @param x The x value of the reference point
     * @param y The y value of the reference point
     * @param bodies The bodies being considered
     * @param rand A uniform random double in [0,1]
     * @return A randomly selected index of `bodies` weighted by each one's gravity on (x,y)
     */
    private int chooseGravityWeightedRandomIndex(double x, double y, List<Body<T>> bodies, double rand) {
        // Normalize the gravity from each body to sum to 1
        List<Double> weights = bodies.stream()
                .map(b -> this.computeGravityWeight(x, y, b))
                .collect(Collectors.toUnmodifiableList());
        double sumWeights = weights.stream().reduce(Double::sum).orElseThrow();
        if (sumWeights == 0) throw new IllegalArgumentException("All items have zero mass");
        List<Double> normalizedWeights = weights.stream()
                .map(w -> w / sumWeights)
                .collect(Collectors.toUnmodifiableList());
        double sumNormedWeights = normalizedWeights.stream().reduce(Double::sum).orElseThrow();
        //if (Math.abs(1.0 - sumNormedWeights) > 0.0001) {
        if (sumNormedWeights != 1.0) {
            throw new RuntimeException("Normalized weights should sum to 1 but found " + sumNormedWeights);
        }

        // Find the first item in the cumsum that is larger than the random number
        List<Double> cumSum = this.cumSum(normalizedWeights);
        for (int i = 0; i < cumSum.size(); i++) {
            if (rand < cumSum.get(i)) {
                return i;
            }
        }
        throw new RuntimeException("Did not select a random index -- this should be unreachable");
    }

    private List<Double> cumSum(List<Double> list) {
        List<Double> out = new ArrayList<>(list.size());
        double cumSum = 0;
        for (double d : list) {
            cumSum += d;
            out.add(cumSum);
        }
        return out;
    }

    /**
     * Compute the amount of gravity (with G factored out) that the given body exerts on a point
     * mass centred at (x,y)
     */
    protected double computeGravityWeight(double x, double y, Body<T> body) {
        if (body.mass == 0) return 0.0;
        double dx = x - body.x;
        double dy = y - body.y;
        double r = Math.sqrt(dx*dx + dy*dy);
        if (r == 0) r = 1; // TODO does this make sense?
        return body.mass / (r*r);
    }

    /**
     * Insert a Body into the subtree headed at this node.
     */
    public void insert(Body<T> b) {
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
    private void putBody(Body<T> b) {
        Quadrant quadrant = this.area.getQuadrant(b.x, b.y);
        if (!this.children.containsKey(quadrant)) {
            this.children.put(quadrant, this.makeNewNode(b, this.area.getSubSquare(quadrant)));
        }
        else {
            this.children.get(quadrant).insert(b);
        }
    }

    protected Node<T> makeNewNode(Body<T> body, Square area) {
        return new Node<>(body, area);
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
