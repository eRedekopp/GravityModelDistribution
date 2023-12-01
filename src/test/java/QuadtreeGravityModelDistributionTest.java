import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class QuadtreeGravityModelDistributionTest extends SharedGravityModelDistributionTests {
    @Override
    protected <T> GravityModelDistribution<T> makeDistribution(List<Body<T>> bodies) {
        // Run the shared tests with 0.0 theta, any tests with different theta should go below in this class
        return new QuadtreeGravityModelDistribution<>(bodies, 0.0);
    }

    private void doTestForConstructorArgs(List<Body<Object>> bodies, double theta) {
        assertThrows(
                IllegalArgumentException.class,
                () -> new QuadtreeGravityModelDistribution<>(bodies, theta)
        );
    }

    @Test
    void testConstructorThrowsForEmptyBodies() {
        doTestForConstructorArgs(List.of(), 0.5);
    }

    @ParameterizedTest
    @ArgumentsSource(InfiniteAndNaNAndNegativeDoubleArgsProvider.class)
    void testConstructorThrowsForInvalidTheta(double theta) {
        doTestForConstructorArgs(List.of(new Body<>(10, 0, 0, new Object())), theta);
    }
}
