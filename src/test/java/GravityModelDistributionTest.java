import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

public class GravityModelDistributionTest {

    private static final int NUM_ITERATIONS = (int) 1E6;

    private static final double EPSILON = 0.001;

    Map<Integer, Double> performIterations(List<Body<Integer>> bodies, double x, double y, double theta) {
        // GMD is threadsafe so we can do these in parallel
        GravityModelDistribution<Integer> dist = new GravityModelDistribution<>(bodies, theta);
        return IntStream
                .range(0, NUM_ITERATIONS)
                .parallel()
                .map(i -> dist.getRandomBody(x, y).value)
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
                List.of(new Body<>(10, 0, 0, 0)),
                1,
                1,
                0.5
        );
        assertEquals(1, percentages.size());
        assertNotNull(percentages.get(0));
        assertEquals(1.0, percentages.get(0));
    }

    @Test
    void testConvergesToEvenProbabilitiesForPointHalfwayBetweenTwoBodiesOfSameMass() {
        Map<Integer, Double> percentages = performIterations(
                List.of(
                        new Body<>(1000, -10, -10, 0),
                        new Body<>(1000, 10, 10, 1)
                ),
                0,
                0,
                0.5
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
                        new Body<>(50, 10, 10, 0),
                        new Body<>(50, 10, -10, 1),
                        new Body<>(50, -10, 10, 2),
                        new Body<>(50, -10, -10, 3)
                ),
                0,
                0,
                0.1
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
    void testConvergesToEvenProbabilitiesForPointsAroundCircleWithRefAtCentreAndZeroTheta() {
        double mass = 10000;
        double radius = 1000;
        double refX = -350, refY = 210;
        int numBodies = 1000;
        double angleIncr = 360.0 / numBodies;
        List<Body<Integer>> bodies = new ArrayList<>(numBodies);
        // Create N bodies around the radius of a circle centred at our reference point
        for (int i = 0; i < numBodies; i++) {
            double angleRads = i * angleIncr * Math.PI / 180;
            double x = refX + Math.cos(angleRads) * radius;
            double y = refY + Math.sin(angleRads) * radius;
            bodies.add(new Body<>(mass, x, y, i));
        }

        Map<Integer, Double> percentages = performIterations(bodies, refX, refY, 0.0);
        for (double p : percentages.values()) {
            assertEquals(1.0 / numBodies, p, EPSILON);
        }
    }

    @Nested
    class TestMoreComplexTree {

        // The tree has 20 bodies total, with a cluster in each quadrant and a few extra bodies scattered
        // throughout. You'll have to calculate the gravity for all 20 bodies on each reference point tested
        // which will be a little bit painful
        private final List<Body<Integer>> bodies = List.of(
                        // Southwest cluster
                        new Body<>(2500, -500, -500, 0),
                        new Body<>(3000, -550, -500, 1),
                        new Body<>(3200, -550, -550, 2),
                        new Body<>(1000, -600, -600, 3),
                        new Body<>(100, -450, -450, 4),
                        // Northwest cluster
                        new Body<>(1200, -1000, 1100, 5),
                        new Body<>(10, -900, 900, 6),
                        new Body<>(5000, -1000, 1000, 7),
                        new Body<>(3200, -1100, 950, 8),
                        // Northeast cluster
                        new Body<>(1000, 50, 50, 9),
                        new Body<>(800, 51, 51, 10),
                        new Body<>(1200, 75, 60, 11),
                        new Body<>(1100, 70, 45, 12),
                        new Body<>(120, 45, 45, 13),
                        // Southeast cluster
                        new Body<>(1000, 700, -500, 14),
                        new Body<>(2000, 700, -510, 15),
                        new Body<>(3000, 750, -600, 16),
                        // Extras
                        new Body<>(10000, 10000, -5000, 17),
                        new Body<>(2, 0, 6, 18),
                        new Body<>(500, 360, -200, 19)
        );

        @Test
        void testReferencePointAtOriginConvergesToExpectedValueWithZeroTheta() {
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

            Map<Integer, Double> percentages = performIterations(bodies, 0, 0, 0.0);
            for (int i = 0; i < percentages.size(); i++) {
                assertEquals(normedForces[i], percentages.get(i), EPSILON);
            }
        }
    }
    @Nested
    class TestInvalidArguments {
        private final Body<Object> A_BODY = new Body<>(10, 0, 0, new Object());

        private void doTestForConstructorArgs(List<Body<Object>> bodies, double theta) {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> new GravityModelDistribution<>(bodies, theta)
            );
        }

        @Test
        void testConstructorThrowsForEmptyBodies() {
            doTestForConstructorArgs(List.of(), 0.5);
        }

        @ParameterizedTest
        @ArgumentsSource(InfiniteAndNaNAndNegativeDoubleArgsProvider.class)
        void testConstructorThrowsForInvalidTheta(double theta) {
            doTestForConstructorArgs(List.of(A_BODY), theta);
        }

        private void doRandomBodyTest(double x, double y) {
            GravityModelDistribution<Object> dist = new GravityModelDistribution<>(List.of(A_BODY), 0.1);
            assertThrows(
                    IllegalArgumentException.class,
                    () -> dist.getRandomBody(x, y)
            );
        }

        @ParameterizedTest
        @ArgumentsSource(InfiniteAndNaNDoubleArgsProvider.class)
        void testRandomBodyThrowsForInvalidX(double x) {
            doRandomBodyTest(x, 10.0);
        }

        @ParameterizedTest
        @ArgumentsSource(InfiniteAndNaNDoubleArgsProvider.class)
        void testRandomBodyThrowsForInvalidY(double y) {
            doRandomBodyTest(20.0, y);
        }
    }
}
