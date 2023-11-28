import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SquareTest {

    @Nested
    class TestContains {

        @Test
        void testContainsOwnCentrePoint() {
            Square square = new Square(10, 20, 1000);
            assertTrue(square.contains(10, 20));
        }

        @Test
        void testContainsPointsOnBoundaries1() {
            Square square = new Square(0, 0, 100);
            assertTrue(square.contains(-50, 50));
        }

        @Test
        void testContainsPointsOnBoundaries2() {
            Square square = new Square(0, 0, 100);
            assertTrue(square.contains(50, -50));
        }

        @Test
        void testContainsNorthwest() {
            Square square = new Square(-10, -10, 40);
            assertTrue(square.contains(-14, 10));
        }

        @Test
        void testContainsSoutheast() {
            Square square = new Square(50, -100, 40);
            assertTrue(square.contains(60, -110));
        }

        @Test
        void testFalseOnlyXOutOfBounds() {
            Square square = new Square(-100, -20, 100);
            assertFalse(square.contains(1, 1));
        }

        @Test
        void testFalseOnlyYOutOfBounds() {
            Square square = new Square(10, 10, 10);
            assertFalse(square.contains(9, 1000));
        }

        @Test
        void testFalseBothOutOfBounds() {
            Square square = new Square(-10, 10, 10);
            assertFalse(square.contains(9, -1000));
        }
    }

    @Nested
    class TestGetQuadrant {
        @Test
        void testCentreCountsAsNortheast() {
            Square square = new Square(0, 0, 10);
            assertEquals(Quadrant.NORTHEAST, square.getQuadrant(0, 0));
        }

        @Test
        void testNorthwest1() {
            Square square = new Square(0, 0, 100);
            assertEquals(Quadrant.NORTHWEST, square.getQuadrant(-20, 98));
        }

        @Test
        void testNorthwest2() {
            Square square = new Square(-100, 20, 10);
            assertEquals(Quadrant.NORTHWEST, square.getQuadrant(-105, 24.99));
        }

        @Test
        void testNortheast1() {
            Square square = new Square(10, 10, 50);
            assertEquals(Quadrant.NORTHEAST, square.getQuadrant(33, 30));
        }

        @Test
        void testNortheast2() {
            Square square = new Square(30, -100, 50);
            assertEquals(Quadrant.NORTHEAST, square.getQuadrant(33, -70));
        }

        @Test
        void testSoutheast1() {
            Square square = new Square(-199, -199, 20);
            assertEquals(Quadrant.SOUTHEAST, square.getQuadrant(-190, -200));
        }

        @Test
        void testSoutheast2() {
            Square square = new Square(300, 120, 100);
            assertEquals(Quadrant.SOUTHEAST, square.getQuadrant(330, 30));
        }

        @Test
        void testSouthwest1() {
            Square square = new Square(-1000, 2000, 150);
            assertEquals(Quadrant.SOUTHWEST, square.getQuadrant(-1100, 1900));
        }

        @Test
        void testSouthwest2() {
            Square square = new Square(100, -120, 10);
            assertEquals(Quadrant.SOUTHWEST, square.getQuadrant(95, -122));
        }
    }

    @Nested
    class TestGetSubSquare {
        @Test
        void testNorthwest1() {
            Square square = new Square(0, 0, 100);
            Square subSquare = new Square(-25, 25, 50);
            assertEquals(subSquare, square.getSubSquare(Quadrant.NORTHWEST));
        }

        @Test
        void testNorthwest2() {
            Square square = new Square(-120, 1000, 200);
            Square subSquare = new Square(-170, 1050, 100);
            assertEquals(subSquare, square.getSubSquare(Quadrant.NORTHWEST));
        }

        @Test
        void testNortheast1() {
            Square square = new Square(0, 0, 100);
            Square subSquare = new Square(25, 25, 50);
            assertEquals(subSquare, square.getSubSquare(Quadrant.NORTHEAST));
        }

        @Test
        void testNortheast2() {
            Square square = new Square(100, 120, 80);
            Square subSquare = new Square(120, 140, 40);
            assertEquals(subSquare, square.getSubSquare(Quadrant.NORTHEAST));
        }

        @Test
        void testSoutheast1() {
            Square square = new Square(0, 0, 100);
            Square subSquare = new Square(25, -25, 50);
            assertEquals(subSquare, square.getSubSquare(Quadrant.SOUTHEAST));
        }

        @Test
        void testSoutheast2() {
            Square square = new Square(-100, -120, 400);
            Square subSquare = new Square(0, -220, 200);
            assertEquals(subSquare, square.getSubSquare(Quadrant.SOUTHEAST));
        }

        @Test
        void testSouthwest1() {
            Square square = new Square(0, 0, 100);
            Square subSquare = new Square(-25, -25, 50);
            assertEquals(subSquare, square.getSubSquare(Quadrant.SOUTHWEST));
        }

        @Test
        void testSouthwest2() {
            Square square = new Square(200, -200, 800);
            Square subSquare = new Square(0, -400, 400);
            assertEquals(subSquare, square.getSubSquare(Quadrant.SOUTHWEST));
        }
    }

    @Nested
    class TestIllegalArguments {
        @Test
        void testConstructor() {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> new Square(0, 0, -0.5)
            );
            assertThrows(
                    IllegalArgumentException.class,
                    () -> new Square(0, 0, Double.POSITIVE_INFINITY)
            );
            assertThrows(
                    IllegalArgumentException.class,
                    () -> new Square(0, 0, Double.NEGATIVE_INFINITY)
            );
            assertThrows(
                    IllegalArgumentException.class,
                    () -> new Square(0, 0, Double.NaN)
            );
            assertThrows(
                    IllegalArgumentException.class,
                    () -> new Square(Double.NEGATIVE_INFINITY, 0, 100)
            );
            assertThrows(
                    IllegalArgumentException.class,
                    () -> new Square(Double.POSITIVE_INFINITY, 0, 100)
            );
            assertThrows(
                    IllegalArgumentException.class,
                    () -> new Square(Double.NaN, 0, 100)
            );
            assertThrows(
                    IllegalArgumentException.class,
                    () -> new Square(0,  Double.NEGATIVE_INFINITY, 100)
            );
            assertThrows(
                    IllegalArgumentException.class,
                    () -> new Square(0, Double.POSITIVE_INFINITY, 100)
            );
            assertThrows(
                    IllegalArgumentException.class,
                    () -> new Square(0, Double.NaN, 100)
            );
        }

        @Test
        void testContains() {
            Square square = new Square(0, 0, 10);

            assertThrows(
                    IllegalArgumentException.class,
                    () -> square.contains(Double.POSITIVE_INFINITY, 0)
            );
            assertThrows(
                    IllegalArgumentException.class,
                    () -> square.contains(Double.NEGATIVE_INFINITY, 0)
            );
            assertThrows(
                    IllegalArgumentException.class,
                    () -> square.contains(Double.NaN, 0)
            );
            assertThrows(
                    IllegalArgumentException.class,
                    () -> square.contains(0, Double.POSITIVE_INFINITY)
            );
            assertThrows(
                    IllegalArgumentException.class,
                    () -> square.contains(0, Double.NEGATIVE_INFINITY)
            );
            assertThrows(
                    IllegalArgumentException.class,
                    () -> square.contains(0, Double.NaN)
            );
        }

        @Test
        void testGetQuadrant() {
            Square square = new Square(0, 0, 10);

            assertThrows(
                    IllegalArgumentException.class,
                    () -> square.getQuadrant(Double.POSITIVE_INFINITY, 0)
            );
            assertThrows(
                    IllegalArgumentException.class,
                    () -> square.getQuadrant(Double.NEGATIVE_INFINITY, 0)
            );
            assertThrows(
                    IllegalArgumentException.class,
                    () -> square.getQuadrant(Double.NaN, 0)
            );
            assertThrows(
                    IllegalArgumentException.class,
                    () -> square.getQuadrant(0, Double.POSITIVE_INFINITY)
            );
            assertThrows(
                    IllegalArgumentException.class,
                    () -> square.getQuadrant(0, Double.NEGATIVE_INFINITY)
            );
            assertThrows(
                    IllegalArgumentException.class,
                    () -> square.getQuadrant(0, Double.NaN)
            );
        }
    }
}
