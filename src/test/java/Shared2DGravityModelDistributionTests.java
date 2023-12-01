import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

public abstract class Shared2DGravityModelDistributionTests {

    protected static final int NUM_ITERATIONS = (int) 1E7;

    protected static final double EPSILON = 0.001;

    protected abstract <T> GravityModelDistribution<T, Body2D<T>> makeDistribution(List<Body2D<T>> bodies);

    Map<Integer, Double> performIterations(List<Body2D<Integer>> bodies, double x, double y) {
        // GMD is threadsafe so we can do these in parallel
        GravityModelDistribution<Integer, Body2D<Integer>> dist = this.makeDistribution(bodies);
        final Body2D<Integer> ref = new Body2D<>(1, x, y, -1);
        return IntStream
                .range(0, NUM_ITERATIONS)
                .parallel()
                .map(i -> dist.getRandomBody(ref).value)
                .boxed()
                .collect(Collectors.groupingByConcurrent(i -> i, Collectors.counting()))
                .entrySet()
                .parallelStream()
                .map(e -> new AbstractMap.SimpleEntry<>(e.getKey(), ((double) e.getValue()) / NUM_ITERATIONS))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Test
    void testReturnsOnlyItemWhenOnlyOne() {
        Map<Integer, Double> percentages = performIterations(
                List.of(new Body2D<>(10, 0, 0, 0)),
                1,
                1
        );
        assertEquals(1, percentages.size());
        assertNotNull(percentages.get(0));
        assertEquals(1.0, percentages.get(0));
    }

    @Test
    void testConvergesToEvenProbabilitiesForPointHalfwayBetweenTwoBodiesOfSameMass() {
        Map<Integer, Double> percentages = performIterations(
                List.of(
                        new Body2D<>(1000, -10, -10, 0),
                        new Body2D<>(1000, 10, 10, 1)
                ),
                0,
                0
        );
        assertEquals(2, percentages.size());
        for (int i : List.of(0, 1)) {
            assertNotNull(percentages.get(i));
            assertEquals(0.5, percentages.get(i), EPSILON);
        }
    }

    @Test
    void testConvergesToEvenProbabilitiesForMiddleOfSquareWithEqualMassesOnCorners() {
        Map<Integer, Double> percentages = performIterations(
                List.of(
                        new Body2D<>(50, 10, 10, 0),
                        new Body2D<>(50, 10, -10, 1),
                        new Body2D<>(50, -10, 10, 2),
                        new Body2D<>(50, -10, -10, 3)
                ),
                0,
                0
        );
        assertEquals(4, percentages.size());
        for (int i : List.of(0, 1, 2, 3)) {
            assertNotNull(percentages.get(i));
            assertEquals(
                    0.25,
                    percentages.get(i),
                    EPSILON,
                    "Probabilities were " + percentages.entrySet()
            );
        }
    }

    @Test
    void testConvergesToEvenProbabilitiesForPointsAroundCircleWithRefAtCentre() {
        double mass = 10000;
        double radius = 1000;
        double refX = -350, refY = 210;
        int numBodies = 1000;
        double angleIncr = 360.0 / numBodies;
        List<Body2D<Integer>> bodies = new ArrayList<>(numBodies);
        // Create N bodies around the radius of a circle centred at our reference point
        for (int i = 0; i < numBodies; i++) {
            double angleRads = i * angleIncr * Math.PI / 180;
            double x = refX + Math.cos(angleRads) * radius;
            double y = refY + Math.sin(angleRads) * radius;
            bodies.add(new Body2D<>(mass, x, y, i));
        }

        Map<Integer, Double> percentages = performIterations(bodies, refX, refY);
        for (double p : percentages.values()) {
            assertEquals(1.0 / numBodies, p, EPSILON);
        }
    }

    @Nested
    class TestMoreComplexTree {

        // The tree has 20 bodies total, with a cluster in each quadrant and a few extra bodies scattered
        // throughout. You'll have to calculate the gravity for all 20 bodies on each reference point tested
        // which will be a little bit painful
        private final List<Body2D<Integer>> bodies = List.of(
                        // Southwest cluster
                        new Body2D<>(2500, -500, -500, 0),
                        new Body2D<>(3000, -550, -500, 1),
                        new Body2D<>(3200, -550, -550, 2),
                        new Body2D<>(1000, -600, -600, 3),
                        new Body2D<>(100, -450, -450, 4),
                        // Northwest cluster
                        new Body2D<>(1200, -1000, 1100, 5),
                        new Body2D<>(10, -900, 900, 6),
                        new Body2D<>(5000, -1000, 1000, 7),
                        new Body2D<>(3200, -1100, 950, 8),
                        // Northeast cluster
                        new Body2D<>(1000, 50, 50, 9),
                        new Body2D<>(800, 51, 51, 10),
                        new Body2D<>(1200, 75, 60, 11),
                        new Body2D<>(1100, 70, 45, 12),
                        new Body2D<>(120, 45, 45, 13),
                        // Southeast cluster
                        new Body2D<>(1000, 700, -500, 14),
                        new Body2D<>(2000, 700, -510, 15),
                        new Body2D<>(3000, 750, -600, 16),
                        // Extras
                        new Body2D<>(10000, 10000, -5000, 17),
                        new Body2D<>(2, 0, 6, 18),
                        new Body2D<>(500, 360, -200, 19)
        );

        @Test
        void testReferencePointAtOriginConvergesToExpectedValue() {
            double[] gravityValues = {
                    // southwest cluster
                    0.004999999999999999,
                    0.005429864253393667,
                    0.005289256198347108,
                    0.001388888888888889,
                    0.00024691358024691353,
                    // northwest cluster
                    0.0005429864253393667,
                    6.172839506172838e-06,
                    0.0024999999999999996,
                    0.001514792899408284,
                    // northeast cluster
                    0.20,
                    0.15378700499807765,
                    0.13008130081300814,
                    0.1588447653429603,
                    0.029629629629629627,
                    // southeast cluster
                    0.0013513513513513514,
                    0.0022219753360737697,
                    0.0032520325203252032,
                    // extras
                    7.999999999999999e-05,
                    0.05555555555555555,
                    0.002948113207547169
            };
            double sumForces = Arrays.stream(gravityValues).reduce(0, Double::sum);
            double[] normedForces = Arrays.stream(gravityValues).map(d -> d / sumForces).toArray();

            Map<Integer, Double> percentages = performIterations(bodies, 0, 0);
            for (int i = 0; i < percentages.size(); i++) {
                assertEquals(normedForces[i], percentages.get(i), EPSILON);
            }
        }
    }
}
