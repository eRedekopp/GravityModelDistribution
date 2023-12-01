import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class SimpleGravityModelDistributionTest extends SharedGravityModelDistributionTests {
    @Override
    protected <T> GravityModelDistribution<T> makeDistribution(List<Body<T>> bodies) {
        return new SimpleGravityModelDistribution<>(bodies);
    }

    @Test
    void testConstructorThrowsForEmptyBodies() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new SimpleGravityModelDistribution<>(List.of())
        );
    }
}
