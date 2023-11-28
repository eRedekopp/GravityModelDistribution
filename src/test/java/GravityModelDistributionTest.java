import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class GravityModelDistributionTest {

    private static final int NUM_ITERATIONS = (int) 1E6;

    private static final double EPSILON = 0.001;

    private Map<Integer, Integer> results;

    @BeforeEach
    void beforeEach() {
        this.results = new HashMap<>();
    }

    /**
     * @return The `results` map but with all entries set to a double from 0 to 1 indicating the proportion of
     * times the body with each int was chosen
     */
    private Map<Integer, Double> getResultsAsPercentage() {
        return results.entrySet().stream()
                .map(e -> new AbstractMap.SimpleImmutableEntry<>(
                        e.getKey(),
                        e.getValue().doubleValue() / NUM_ITERATIONS)
                ).collect(Collectors.toMap(
                        AbstractMap.SimpleImmutableEntry::getKey,
                        AbstractMap.SimpleImmutableEntry::getValue
                ));
    }

    Map<Integer, Double> performIterations(List<Body<Integer>> bodies, double x, double y) {
        GravityModelDistribution<Integer> dist = new GravityModelDistribution<>(bodies);
        for (int i = 0; i < NUM_ITERATIONS; i++) {
            Body<Integer> result = dist.getRandomBody(x, y);
            int currentNum = this.results.getOrDefault(result.value, 0);
            this.results.put(result.value, currentNum + 1);
        }
        return getResultsAsPercentage();
    }

    @Test
    void testReturnsOnlyItemWhenOnlyOne() {
        Map<Integer, Double> percentages = performIterations(
                List.of(new Body<>(10, 0, 0, 0)),
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
                        new Body<>(1000, -10, -10, 0),
                        new Body<>(1000, 10, 10, 1)
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
    void testConvergesToEvenProbabilitiesForPointHalfwayBetweenThreeBodiesOfSameMass() {
        Map<Integer, Double> percentages = performIterations(
                List.of(
                        new Body<>(50, -10, 0, 0),
                        new Body<>(50, 10, 0, 1),
                        new Body<>(50, 0, 10, 2)
                ),
                0,
                0
        );
        assertEquals(3, percentages.size());
        for (int i : List.of(0, 1, 2)) {
            assertNotNull(percentages.get(i));
            assertEquals(
                    1.0 / 3.0,
                    percentages.get(i),
                    EPSILON,
                    "Probabilities were " + percentages.entrySet()
            );
        }
    }

    @Test
    void testConvergesToEvenProbabilitiesForMidpointBetweenManyBodiesOfSameMass() {
        fail();
    }

    @Test
    void testConvergesToExpectedValueInSmallTree() {
        fail();
    }

    @Test
    void testConvergesToExpectedValueInMediumTree() {
        fail();
    }

    @Test
    void testConvergesToExpectedValueInLargeTree() {
        fail();
    }
}
