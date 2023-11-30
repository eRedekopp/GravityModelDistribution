import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class NodeTest {

    // Makes the children visible
    private static class TestNode<T> extends Node<T> {
        public TestNode(Body<T> body, Square area) {
            super(body, area);
        }

        public Map<Quadrant, Node<T>> getChildren() {
            return this.children;
        }

        protected TestNode<T> makeNewNode(Body<T> body, Square square) {
            return new TestNode<>(body, square);
        }
    }

    @Nested
    class TestGetRandomNode {
        private void doTestHalfwayBetweenTwoEqualMassBodies(double randDouble) {
            Random rng = Mockito.mock(Random.class);
            Mockito.when(rng.nextDouble()).thenReturn(randDouble);
            double mass = 1000;
            Node<Integer> root = new Node<>(
                    new Body<>(mass, -10, -10, 0),
                    new Square(0, 0, 100),
                    rng
            );
            root.insert(new Body<>(mass, 10, 10, 1));
            Body<Integer> result = root.getRandomBody(0, 0, 0.5);
            assertNotNull(result);
            assertTrue(List.of(0, 1).contains(result.value));
            assertEquals(mass, result.mass);
        }

        @Test
        void returnsValidValueWhenHalfwayBetweenTwoEqualMassBodiesRngReturnsNear100pct() {
            // Random.nextDouble returns a number in [0.0,1.0), so just get as close to 1.0 as possible
            // Largest number <1.0 in 64-bit IEEE754 is 1.0-2^-53, which is roughly the number given below
            doTestHalfwayBetweenTwoEqualMassBodies(0.9999999999999998889);
        }

        @Test
        void returnsValidValueWhenHalfwayBetweenTwoEqualMassBodiesRngReturns0pct() {
            doTestHalfwayBetweenTwoEqualMassBodies(0.0);
        }

        @Test
        void testReturnsOnlyNonZeroChildWhenOnlyOneExistsAndPointInSameQuad() {
            // Only sw has any mass and depth is only 1, reference point is in same quad as sw
            Body<Integer> ne = new Body<>(0, 1, 1, 0);
            Body<Integer> nw = new Body<>(0, -1, 1, 1);
            Body<Integer> se = new Body<>(0, 1, -1, 2);
            Body<Integer> sw = new Body<>(1, -1, -1, 3);
            Node<Integer> node = new Node<>(
                    ne,
                    new Square(0, 0, 10)
            );
            List.of(nw, se, sw).forEach(node::insert);
            Body<Integer> result = node.getRandomBody(-2, -2, 0.5);
            assertEquals(3, result.value);
            assertEquals(-1, result.x);
            assertEquals(-1, result.y);
            assertEquals(1, result.mass);
        }

        @Test
        void testReturnsOnlyNonZeroChildWhenOnlyOneExistsAndPointInDifferentQuad() {
            // Only ne2 has any mass but depth is deeper. Reference point is in a totally different quad
            Body<Integer> ne1 = new Body<>(0, 1, 1, 0);
            Body<Integer> ne2 = new Body<>(10, 0.001, 0.002, 4);
            Body<Integer> nw = new Body<>(0, -1, 1, 1);
            Body<Integer> se = new Body<>(0, 1, -1, 2);
            Body<Integer> sw = new Body<>(0, -1, -1, 3);
            Node<Integer> node = new Node<>(
                    ne1,
                    new Square(0, 0, 1000)
            );
            List.of(ne2, nw, se, sw).forEach(node::insert);
            Body<Integer> result = node.getRandomBody(-400, -200, 0.5);
            assertEquals(4, result.value);
            assertEquals(0.001, result.x);
            assertEquals(0.002, result.y);
            assertEquals(10, result.mass);
        }

        @Test
        void testReturnsNodeBodyWhenNoOthersExist() {
            Body<Integer> body = new Body<>(100, 1, 1, 1);
            Node<Integer> node = new Node<>(
                    body,
                    new Square(0, 0, 10)
            );
            Body<Integer> result = node.getRandomBody(10, 10, 0.5);
            assertEquals(body, result);
        }

        @Test
        void testReturnsOtherWhenBodyHasZeroMass() {
            Body<Integer> other = new Body<>(100, 10, 10, 10);
            Node<Integer> node = new Node<>(
                    new Body<>(0, 0, 0, 0),
                    new Square(0, 0, 100)
            );
            node.insert(other);
            Body<Integer> result = node.getRandomBody(5, 5, 0.5);
            assertEquals(other, result);
        }

        @Test
        void testThrowsWhenZeroMassTotal() {
            List<Body<Integer>> others = List.of(
                    new Body<>(0, 10, 10, 10),
                    new Body<>(0, -1, -1, -1),
                    new Body<>(0, -11, -1, -1),
                    new Body<>(0, -105, -100, -1)
            );
            Node<Integer> node = new Node<>(
                    new Body<>(0, 0, 0, 0),
                    new Square(0, 0, 1000)
            );
            others.forEach(node::insert);
            assertThrows(
                    IllegalArgumentException.class,
                    () -> node.getRandomBody(1, 10, 0.5)
            );
        }
    }

    @Nested
    class TestConstructor {
        @Test
        void testNewNodeStartsAtCorrectInitialState() {
            TestNode<Integer> node = new TestNode<>(
                    new Body<>(100, 10, 10, 1),
                    new Square(1, 1, 100)
            );
            assertEquals(10, node.getCentreMass().x);
            assertEquals(10, node.getCentreMass().y);
            assertEquals(100, node.getCentreMass().mass);
            assertEquals(1, node.getCentreMass().value);
            assertEquals(0, node.getChildren().size());
        }

        @Test
        void testThrowsIfNullBody() {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> new Node<>(
                            null,
                            new Square(0, 0, 100)
                    )
            );
        }

        @Test
        void testThrowsIfNullSquare() {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> new Node<>(
                            new Body<>(1000, 10, 10, new Object()),
                            null
                    )
            );
        }

        @Test
        void testThrowsIfInitialBodyOutOfRange() {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> new Node<>(
                            new Body<>(1000, 10, 10, new Object()),
                            new Square(0, 0, 0.5)
                    )
            );
        }
    }

    @Nested
    class TestInsert {
        @Test
        void testThrowsIfBodyOutOfRange() {
            TestNode<Integer> root = new TestNode<>(
                    new Body<>(1000, 100, 100, 0),
                    new Square(0, 0, 300)
            );
            assertThrows(
                    IllegalArgumentException.class,
                    () -> root.insert(new Body<>(100, -1000, -1000, 1))
            );
        }

        @Test
        void testThrowsIfIdenticalLocations() {
            TestNode<Integer> root = new TestNode<>(
                    new Body<>(1000, 100, 100, 0),
                    new Square(0, 0, 300)
            );
            assertThrows(
                    IllegalArgumentException.class,
                    () -> root.insert(new Body<>(100, 100, 100, 1))
            );
        }

        @Test
        void testSplitIntoTwoTopLevelNodes() {
            // If we have a leaf node with the object in the very northwest corner, and insert another node
            // into the very southwest corner, we should split into a northwest and southwest node beneath
            // the original node
            double[] masses = {10000, 23300};
            double[] xs = {-100, -100};
            double[] ys = {98, -100};
            TestNode<Integer> root = new TestNode<>(
                    new Body<>(masses[0], xs[0], ys[0], 0),
                    new Square(-1, -1, 200)
            );
            root.insert(new Body<>(masses[1], xs[1], ys[1], 1));

            // Make sure the original node has updated correctly
            assertEquals(masses[0] + masses[1], root.getCentreMass().mass);
            assertNull(root.getCentreMass().value);
            assertEquals(2, root.getChildren().size());

            // Make sure the southwest corner is there and contains the new Body
            TestNode<Integer> sw = (TestNode<Integer>) root.getChildren().get(Quadrant.SOUTHWEST);
            assertEquals(masses[1], sw.getCentreMass().mass);
            assertEquals(xs[1], sw.getCentreMass().x);
            assertEquals(ys[1], sw.getCentreMass().y);
            assertEquals(1, sw.getCentreMass().value);
            assertEquals(0, sw.getChildren().size());

            // Make sure the northwest corner is there and contains the original Body
            TestNode<Integer> nw = (TestNode<Integer>) root.getChildren().get(Quadrant.NORTHWEST);
            assertEquals(masses[0], nw.getCentreMass().mass);
            assertEquals(xs[0], nw.getCentreMass().x);
            assertEquals(ys[0], nw.getCentreMass().y);
            assertEquals(0, nw.getCentreMass().value);
            assertEquals(0, nw.getChildren().size());
        }

        @Test
        void testDoubleSplit() {
            // If we have a leaf node containing a node in the very southeast corner, and add a new node just to the
            // southeast of the centre, then it should split the southeast corner. In the new split node, there should
            // be a northwest and southeast corner containing the new node and the original node, respectively
            double[] masses = {100, 200};
            double[] xs = {199, 101};
            double[] ys = {1, 99};
            TestNode<Integer> root = new TestNode<>(
                    new Body<>(masses[0], xs[0], ys[0], 0),
                    new Square(100, 100, 200)
            );
            root.insert(new Body<>(masses[1], xs[1], ys[1], 1));

            // Make sure the original node has updated correctly
            assertEquals(masses[0] + masses[1], root.getCentreMass().mass);
            assertNull(root.getCentreMass().value);
            assertEquals(1, root.getChildren().size());

            // Make sure the southeast corner exists and has split into 2 portions
            TestNode<Integer> se1 = (TestNode<Integer>) root.getChildren().get(Quadrant.SOUTHEAST);
            assertNotNull(se1);
            assertEquals(masses[0] + masses[1], se1.getCentreMass().mass);
            assertNull(se1.getCentreMass().value);
            assertEquals(2, se1.getChildren().size());

            // Make sure that the southeast corner exists and contains the original node
            TestNode<Integer> se2 = (TestNode<Integer>) se1.getChildren().get(Quadrant.SOUTHEAST);
            assertNotNull(se2);
            assertEquals(masses[0], se2.getCentreMass().mass);
            assertEquals(0, se2.getCentreMass().value);
            assertEquals(xs[0], se2.getCentreMass().x);
            assertEquals(ys[0], se2.getCentreMass().y);
            assertEquals(0, se2.getChildren().size());

            // Make sure that the northwest corner exists and contains the new node
            TestNode<Integer> nw = (TestNode<Integer>) se1.getChildren().get(Quadrant.NORTHWEST);
            assertNotNull(nw);
            assertEquals(masses[1], nw.getCentreMass().mass);
            assertEquals(1, nw.getCentreMass().value);
            assertEquals(xs[1], nw.getCentreMass().x);
            assertEquals(ys[1], nw.getCentreMass().y);
            assertEquals(0, nw.getChildren().size());
        }

        @Test
        void testMultiSplit() {
            // If we insert objects very close to each other, we might end up needing to split many times. In this case,
            // we will have a node centred on (0,0) with side length of 1000. The original node will be located at
            // (1, 1) and we will insert a node at (0.5, 0.5). This should result in the northeast corner splitting
            // into side length 500, 250, 125, 62.5, 31.25, 15.625, 7.8125, 3.90625, and finally 1.953125. At that
            // point, the centre line will be at 0.9765625 and will split (1, 1) into the northeast corner, and
            // (0.5, 0.5) into the southwest corner.
            double[] masses = {500, 800};
            double[] xs = {1, 0.5};
            double[] ys = {1, 0.5};
            double combinedCentreMassX = (xs[0] * masses[0] + xs[1] * masses[1]) / (masses[0] + masses[1]);
            double combinedCentreMassY = (ys[0] * masses[0] + ys[1] * masses[1]) / (masses[0] + masses[1]);
            TestNode<Integer> root = new TestNode<>(
                    new Body<>(masses[0], xs[0], ys[0], 0),
                    new Square(0, 0, 1000)
            );
            root.insert(new Body<>(masses[1], xs[1], ys[1], 1));
            assertNotNull(root);
            assertEquals(masses[0] + masses[1], root.getCentreMass().mass);
            assertNull(root.getCentreMass().value);
            assertEquals(combinedCentreMassX, root.getCentreMass().x);
            assertEquals(combinedCentreMassY, root.getCentreMass().y);
            assertEquals(1, root.getChildren().size());

            // We will have the root node, 8 intermediate splits, the last non-leaf node, then finally the 2 leaf nodes
            TestNode<Integer> node = (TestNode<Integer>) root.getChildren().get(Quadrant.NORTHEAST);
            for (int i = 0; i < 8; i++) {
                assertNotNull(node);
                assertEquals(masses[0] + masses[1], node.getCentreMass().mass);
                assertNull(node.getCentreMass().value);
                assertEquals(combinedCentreMassX, node.getCentreMass().x);
                assertEquals(combinedCentreMassY, node.getCentreMass().y);
                assertEquals(1, node.getChildren().size());
                node = (TestNode<Integer>) node.getChildren().get(Quadrant.SOUTHWEST);
            }

            // node now equals the last non-leaf. Check its values first
            assertNotNull(node);
            assertEquals(masses[0] + masses[1], node.getCentreMass().mass);
            assertNull(node.getCentreMass().value);
            assertEquals(combinedCentreMassX, node.getCentreMass().x);
            assertEquals(combinedCentreMassY, node.getCentreMass().y);
            assertEquals(2, node.getChildren().size());

            // Check that the northeast node contains the original Body
            TestNode<Integer> ne = (TestNode<Integer>) node.getChildren().get(Quadrant.NORTHEAST);
            assertEquals(masses[0], ne.getCentreMass().mass);
            assertEquals(0, ne.getCentreMass().value);
            assertEquals(xs[0], ne.getCentreMass().x);
            assertEquals(ys[0], ne.getCentreMass().y);
            assertEquals(0, ne.getChildren().size());

            // Check that the southwest node contains the new Body
            TestNode<Integer> sw = (TestNode<Integer>) node.getChildren().get(Quadrant.SOUTHWEST);
            assertEquals(masses[1], sw.getCentreMass().mass);
            assertEquals(1, sw.getCentreMass().value);
            assertEquals(xs[1], sw.getCentreMass().x);
            assertEquals(ys[1], sw.getCentreMass().y);
            assertEquals(0, sw.getChildren().size());
        }

        // With apologies to all of my programming teachers; lots of copy-paste in here
        @Test
        void testLongerSequence() {
            double[] masses = {100, 120, 500, 150, 900, 8800};

            // Create the root node in the northeast corner of the total area
            TestNode<Integer> root = new TestNode<>(
                    new Body<>(masses[0], 1, 1, 0),
                    new Square(0, 0, 1000)
            );
            assertEquals(masses[0], root.getCentreMass().mass);
            assertEquals(1, root.getCentreMass().x);
            assertEquals(1, root.getCentreMass().y);
            assertEquals(0, root.getCentreMass().value);
            assertEquals(0, root.getChildren().size());

            // Add a new node in the northwest corner. This should result in the initial Body being
            // inserted into the northeast quadrant and the new one into the northwest quadrant
            root.insert(new Body<>(masses[1], -100, 100, 1));
            assertEquals(masses[0] + masses[1], root.getCentreMass().mass);
            assertNull(root.getCentreMass().value);
            assertEquals(2, root.getChildren().size());
            TestNode<Integer> neCorner1 = (TestNode<Integer>) root.getChildren().get(Quadrant.NORTHEAST);
            assertNotNull(neCorner1);
            assertEquals(0, neCorner1.getChildren().size());
            assertEquals(masses[0], neCorner1.getCentreMass().mass);
            assertEquals(0, neCorner1.getCentreMass().value);
            TestNode<Integer> nwCorner1 = (TestNode<Integer>) root.getChildren().get(Quadrant.NORTHWEST);
            assertNotNull(nwCorner1);
            assertEquals(0, nwCorner1.getChildren().size());
            assertEquals(masses[1], nwCorner1.getCentreMass().mass);
            assertEquals(1, nwCorner1.getCentreMass().value);

            // Add another node into the northeast corner of the northeast corner. This should result in the
            // original node going into the southwest quadrant and the new node going northeast. Also make
            // sure that nothing else changes
            root.insert(new Body<>(masses[2], 400, 400, 2));
            assertEquals(masses[0] + masses[1] + masses[2], root.getCentreMass().mass);
            assertNull(root.getCentreMass().value);
            assertEquals(2, root.getChildren().size());
            neCorner1 = (TestNode<Integer>) root.getChildren().get(Quadrant.NORTHEAST);
            assertEquals(masses[0] + masses[2], neCorner1.getCentreMass().mass);
            assertNull(neCorner1.getCentreMass().value);
            assertEquals(2, neCorner1.getChildren().size());
            TestNode<Integer> neCorner2 = (TestNode<Integer>) neCorner1.getChildren().get(Quadrant.NORTHEAST);
            assertEquals(0, neCorner2.getChildren().size());
            assertEquals(masses[2], neCorner2.getCentreMass().mass);
            assertEquals(2, neCorner2.getCentreMass().value);
            TestNode<Integer> swCorner1 = (TestNode<Integer>) neCorner1.getChildren().get(Quadrant.SOUTHWEST);
            assertEquals(0, swCorner1.getChildren().size());
            assertEquals(masses[0], swCorner1.getCentreMass().mass);
            assertEquals(0, swCorner1.getCentreMass().value);
            nwCorner1 = (TestNode<Integer>) root.getChildren().get(Quadrant.NORTHWEST);
            assertNotNull(nwCorner1);
            assertEquals(0, nwCorner1.getChildren().size());
            assertEquals(masses[1], nwCorner1.getCentreMass().mass);
            assertEquals(1, nwCorner1.getCentreMass().value);

            // Add a node into the southwest corner. Make sure it's added correctly and nothing else changes
            root.insert(new Body<>(masses[3], -1, -1, 3));
            assertEquals(masses[0] + masses[1] + masses[2] + masses[3], root.getCentreMass().mass);
            assertNull(root.getCentreMass().value);
            assertEquals(3, root.getChildren().size());
            TestNode<Integer> swCorner2 = (TestNode<Integer>) root.getChildren().get(Quadrant.SOUTHWEST);
            assertEquals(0, swCorner2.getChildren().size());
            assertEquals(masses[3], swCorner2.getCentreMass().mass);
            assertEquals(3, swCorner2.getCentreMass().value);
            neCorner1 = (TestNode<Integer>) root.getChildren().get(Quadrant.NORTHEAST);
            assertEquals(masses[0] + masses[2], neCorner1.getCentreMass().mass);
            assertNull(neCorner1.getCentreMass().value);
            assertEquals(2, neCorner1.getChildren().size());
            neCorner2 = (TestNode<Integer>) neCorner1.getChildren().get(Quadrant.NORTHEAST);
            assertEquals(0, neCorner2.getChildren().size());
            assertEquals(masses[2], neCorner2.getCentreMass().mass);
            assertEquals(2, neCorner2.getCentreMass().value);
            swCorner1 = (TestNode<Integer>) neCorner1.getChildren().get(Quadrant.SOUTHWEST);
            assertEquals(0, swCorner1.getChildren().size());
            assertEquals(masses[0], swCorner1.getCentreMass().mass);
            assertEquals(0, swCorner1.getCentreMass().value);
            nwCorner1 = (TestNode<Integer>) root.getChildren().get(Quadrant.NORTHWEST);
            assertNotNull(nwCorner1);
            assertEquals(0, nwCorner1.getChildren().size());
            assertEquals(masses[1], nwCorner1.getCentreMass().mass);
            assertEquals(1, nwCorner1.getCentreMass().value);

            // Add a node into the southeast corner. Make sure it's added correctly and nothing else changes
            root.insert(new Body<>(masses[4], 200, -350, 4));
            assertEquals(masses[0] + masses[1] + masses[2] + masses[3] + masses[4], root.getCentreMass().mass);
            assertNull(root.getCentreMass().value);
            assertEquals(4, root.getChildren().size());
            TestNode<Integer> seCorner1 = (TestNode<Integer>) root.getChildren().get(Quadrant.SOUTHEAST);
            assertEquals(0, seCorner1.getChildren().size());
            assertEquals(masses[4], seCorner1.getCentreMass().mass);
            assertEquals(4, seCorner1.getCentreMass().value);
            swCorner2 = (TestNode<Integer>) root.getChildren().get(Quadrant.SOUTHWEST);
            assertEquals(0, swCorner2.getChildren().size());
            assertEquals(masses[3], swCorner2.getCentreMass().mass);
            assertEquals(3, swCorner2.getCentreMass().value);
            neCorner1 = (TestNode<Integer>) root.getChildren().get(Quadrant.NORTHEAST);
            assertEquals(masses[0] + masses[2], neCorner1.getCentreMass().mass);
            assertNull(neCorner1.getCentreMass().value);
            assertEquals(2, neCorner1.getChildren().size());
            neCorner2 = (TestNode<Integer>) neCorner1.getChildren().get(Quadrant.NORTHEAST);
            assertEquals(0, neCorner2.getChildren().size());
            assertEquals(masses[2], neCorner2.getCentreMass().mass);
            assertEquals(2, neCorner2.getCentreMass().value);
            swCorner1 = (TestNode<Integer>) neCorner1.getChildren().get(Quadrant.SOUTHWEST);
            assertEquals(0, swCorner1.getChildren().size());
            assertEquals(masses[0], swCorner1.getCentreMass().mass);
            assertEquals(0, swCorner1.getCentreMass().value);
            nwCorner1 = (TestNode<Integer>) root.getChildren().get(Quadrant.NORTHWEST);
            assertNotNull(nwCorner1);
            assertEquals(0, nwCorner1.getChildren().size());
            assertEquals(masses[1], nwCorner1.getCentreMass().mass);
            assertEquals(1, nwCorner1.getCentreMass().value);

            // Add one more node into the northeast corner of the northeast corner. This should result in the northeast
            // node of the northeast node splitting twice, its original node going into the southwest corner, the new node
            // going into the northeast corner, and nothing else changing at the higher levels except the weight of the
            // inner nodes
            root.insert(new Body<>(masses[5], 450, 450, 5));
            assertEquals(
                    masses[0] + masses[1] + masses[2] + masses[3] + masses[4] + masses[5],
                    root.getCentreMass().mass
            );
            assertNull(root.getCentreMass().value);
            assertEquals(4, root.getChildren().size());
            neCorner1 = (TestNode<Integer>) root.getChildren().get(Quadrant.NORTHEAST);
            assertEquals(masses[0] + masses[2] + masses[5], neCorner1.getCentreMass().mass);
            assertNull(neCorner1.getCentreMass().value);
            assertEquals(2, neCorner1.getChildren().size());
            assertNotNull(neCorner1.getChildren().get(Quadrant.SOUTHWEST));
            neCorner2 = (TestNode<Integer>) neCorner1.getChildren().get(Quadrant.NORTHEAST);
            assertNotNull(neCorner2);
            assertEquals(masses[2] + masses[5], neCorner2.getCentreMass().mass);
            assertNull(neCorner2.getCentreMass().value);
            assertEquals(1, neCorner2.getChildren().size());
            TestNode<Integer> neCorner3 = (TestNode<Integer>) neCorner2.getChildren().get(Quadrant.NORTHEAST);
            assertNotNull(neCorner3);
            assertEquals(masses[2] + masses[5], neCorner3.getCentreMass().mass);
            assertNull(neCorner3.getCentreMass().value);
            assertEquals(2, neCorner3.getChildren().size());
            TestNode<Integer> neCorner4 = (TestNode<Integer>) neCorner3.getChildren().get(Quadrant.NORTHEAST);
            assertNotNull(neCorner4);
            assertEquals(0, neCorner4.getChildren().size());
            assertEquals(5, neCorner4.getCentreMass().value);
            assertEquals(masses[5], neCorner4.getCentreMass().mass);
            TestNode<Integer> swCorner3 = (TestNode<Integer>) neCorner3.getChildren().get(Quadrant.SOUTHWEST);
            assertNotNull(swCorner3);
            assertEquals(0, swCorner3.getChildren().size());
            assertEquals(2, swCorner3.getCentreMass().value);
            assertEquals(masses[2], swCorner3.getCentreMass().mass);
        }
    }
}
