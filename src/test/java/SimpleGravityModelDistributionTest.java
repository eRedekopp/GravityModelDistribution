import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class SimpleGravityModelDistributionTest extends Shared2DGravityModelDistributionTests {
    @Override
    protected <T> GravityModelDistribution<T, Body2D<T>> makeDistribution(List<Body2D<T>> bodies) {
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
