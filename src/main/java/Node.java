import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
     * the given (x, y).
     * @param x The x value of the reference point
     * @param y The y value of the reference point
     * @param theta A tunable parameter deciding the balance between performance and accuracy. Smaller theta is more
     *              accurate and larger theta is more performant.
     */
    public Body<T> getRandomBody(double x, double y, double theta) {
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
        if (this.isLeafNode() || (this.area.sideLength / this.body.distanceTo(x, y)) < theta) {
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

    /**
     * Choose a random node from the list weighted by their gravity on point (x,y). This is implemented
     * by designating certain ranges within [0,1] as belonging to each node based on their weight. Whichever one of
     * these ranges `rand` falls into is the node that is returned.
     *
     * <p/>
     * For example:
     * <tt>
     * [[_____n1_____][__n2__][____________n3____________]]
     * 0========================.5========================1
     * </tt>
     * <ul>
     *     <li>Rand = 0.1 -> return n1</li>
     *     <li>Rand = 0.4 -> return n2</li>
     *     <li>Rand = 0.7 -> return n3</li>
     * </ul>
     *
     * @param x The x value of the reference point
     * @param y The y value of the reference point
     * @param candidates The nodes being considered
     * @param rand A uniform random double in [0,1)
     * @return A randomly selected node weighted by each one's gravity on (x,y)
     */
    private Node<T> chooseRandomGravityWeightedNode(double x, double y, List<Node<T>> candidates, double rand) {
        List<Double> forces = candidates.stream()
                .map(b -> this.computeGravityWeight(x, y, b.body))
                .collect(Collectors.toUnmodifiableList());
        double sumForces = forces.stream().reduce(Double::sum).orElseThrow();
        if (sumForces == 0) throw new IllegalArgumentException("No candidates produce any force on the given point");
        List<Double> normalizedForces = forces.stream()
                .map(f -> f / sumForces)
                .collect(Collectors.toUnmodifiableList());
        List<Double> cumSum = this.cumSum(normalizedForces);
        for (int i = 0; i < cumSum.size(); i++) {
            if (rand < cumSum.get(i)) {
                return candidates.get(i);
            }
        }
        throw new RuntimeException("Did not select a random node -- this should be unreachable");
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
        double r = body.distanceTo(x, y);
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
