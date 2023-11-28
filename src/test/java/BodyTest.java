import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * The only real function in Body is 'plus' so this only tests that, probably more thoroughly than necessary
 */
public class BodyTest {

    static final Body<Object> ORIGIN_EMPTY = new Body<>(0, 0, 0, null);

    private void doOneTest(double expectedX, double expectedY, double expectedMass, Body<Object> a, Body<Object> b) {
        Body<Object> result = a.plus(b);
        assertEquals(expectedX, result.x);
        assertEquals(expectedY, result.y);
        assertEquals(expectedMass, result.mass);
        assertNull(result.value);
    }

    @Nested
    class TestPlusNonZero {
        @Test
        void testAddBothIn1stQuadrant() {
            double mass1 = 100, x1 = 500, y1 = 21, mass2 = 300, x2 = 30, y2 = 300;
            Body<Object> body1 = new Body<>(mass1, x1, y1, new Object());
            Body<Object> body2 = new Body<>(mass2, x2, y2, new Object());
            doOneTest(147.5, 230.25, 400, body1, body2);
        }

        @Test
        void testAddBothIn2ndQuadrant() {
            double mass1 = 10000, x1 = -350000, y1 = 100000, mass2 = 100000, x2 = -1000000, y2 = 30000;
            Body<Object> body1 = new Body<>(mass1, x1, y1, new Object());
            Body<Object> body2 = new Body<>(mass2, x2, y2, new Object());
            doOneTest(-940909.0909090909, 36363.63636363636,110000, body1, body2);
        }

        @Test
        void testAddBothIn3rdQuadrant() {
            double mass1 = 100, x1 = -100, y1 = -100, mass2 = 500, x2 = -500, y2 = -500;
            Body<Object> body1 = new Body<>(mass1, x1, y1, new Object());
            Body<Object> body2 = new Body<>(mass2, x2, y2, new Object());
            doOneTest(-433.3333333333333, -433.3333333333333, 600, body1, body2);
        }

        @Test
        void testAddBothIn4thQuadrant() {
            double mass1 = 300, x1 = 250, y1 = -400, mass2 = 300, x2 = 100, y2 = -100;
            Body<Object> body1 = new Body<>(mass1, x1, y1, new Object());
            Body<Object> body2 = new Body<>(mass2, x2, y2, new Object());
            doOneTest(175, -250, 600, body1, body2);
        }

        @Test
        void testAdd1stAnd3rdQuadrant() {
            double mass1 = 3200, x1 = -2500, y1 = -400, mass2 = 3030, x2 = 1000, y2 = 10000;
            Body<Object> body1 = new Body<>(mass1, x1, y1, new Object());
            Body<Object> body2 = new Body<>(mass2, x2, y2, new Object());
            doOneTest(-797.7528089887641, 4658.105939004816, 6230, body1, body2);
        }

        @Test
        void testAddOneAtOrigin() {
            double mass1 = 100000, x1 = 0, y1 = 0, mass2 = 3000, x2 = 1000, y2 = -2300;
            Body<Object> body1 = new Body<>(mass1, x1, y1, new Object());
            Body<Object> body2 = new Body<>(mass2, x2, y2, new Object());
            doOneTest(29.12621359223301, -66.99029126213593, 103000, body1, body2);
        }

        @Test
        void testAddBothInSameLocation() {
            double mass1 = 1000, mass2 = 3000, x = -2000, y = -150;
            Body<Object> body1 = new Body<>(mass1, x, y, new Object());
            Body<Object> body2 = new Body<>(mass2, x, y, new Object());
            doOneTest(x, y, 4000,  body1, body2);
        }
    }

    @Nested
    class TestAddZero {
        @Test
        void testZeroBodyPlusZeroBodyEqualsZero() {
            doOneTest(0, 0, 0, ORIGIN_EMPTY, ORIGIN_EMPTY);
        }

        @Test
        void testNonZeroBodyPlusZeroBodyEqualsNonZeroBody() {
            double myMass = 100, myX = -30, myY = 100;
            Body<Object> nonZero = new Body<>(myMass, myX, myY, new Object());
            doOneTest(myX, myY, myMass, nonZero, ORIGIN_EMPTY);
        }

        @Test
        void testZeroBodyPlusNonZeroBodyEqualsNonZeroBody() {
            double myMass = 100, myX = 30, myY = 100;
            Body<Object> nonZero = new Body<>(myMass, myX, myY, new Object());
            doOneTest(myX, myY, myMass, ORIGIN_EMPTY, nonZero);
        }

        @Test
        void testTwoNonOriginZeroMassBodiesEqualsOrigin() {
            double myX1 = 30, myY1 = 100, myX2 = 1002, myY2 = 10;
            Body<Object> body1 = new Body<>(0, myX1, myY1, new Object());
            Body<Object> body2 = new Body<>(0, myX2, myY2, new Object());
            doOneTest(0, 0, 0, body1, body2);
        }
    }

    @Nested
    class TestIllegalArguments {

        @Test
        void testThrowsExceptionForNegativeMass() {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> new Body<Object>(-1, 10, 200, new Object())
            );
        }

        @Test
        void testThrowsExceptionForNegativeInfiniteMass() {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> new Body<Object>(Double.NEGATIVE_INFINITY, 10, 200, new Object())
            );
        }

        @Test
        void testThrowsExceptionForInfiniteMass() {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> new Body<Object>(Double.POSITIVE_INFINITY, 10, 200, new Object())
            );
        }

        @Test
        void testThrowsExceptionForNaNMass() {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> new Body<Object>(Double.NaN, 10, 200, new Object())
            );
        }

        @Test
        void testThrowsExceptionForInfiniteX() {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> new Body<Object>(1000, Double.POSITIVE_INFINITY, 200, new Object())
            );
        }

        @Test
        void testThrowsExceptionForInfiniteY() {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> new Body<Object>(1000, 200, Double.POSITIVE_INFINITY, new Object())
            );
        }

        @Test
        void testThrowsExceptionForNegativeInfiniteX() {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> new Body<Object>(1000, Double.NEGATIVE_INFINITY, 200, new Object())
            );
        }

        @Test
        void testThrowsExceptionForNegativeInfiniteY() {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> new Body<Object>(1000, 200, Double.NEGATIVE_INFINITY, new Object())
            );
        }

        @Test
        void testThrowsExceptionForNaNX() {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> new Body<Object>(1000, Double.NaN, 200, new Object())
            );
        }

        @Test
        void testThrowsExceptionForNaNY() {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> new Body<Object>(1000, 200, Double.NaN, new Object())
            );
        }
    }
}
