import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import static org.junit.jupiter.api.Assertions.*;

public class Body2DTest {

    static final Body2D<Object> ORIGIN_EMPTY = new Body2D<>(0, 0, 0, null);

    @Nested
    class TestComputeGravity {

        @Test
        void testReturns0WhenSameBody() {
            Body2D<Object> body = new Body2D<>(1, 100, 100, new Object());
            assertEquals(0.0, body.computeGravForce(body));
        }

        @Test
        void testReturns0WhenBodyInSameLocation() {
            Body2D<Object> b1 = new Body2D<>(1000, 50, 100, new Object());
            Body2D<Object> b2 = new Body2D<>(5555, 50, 100, new Object());
            assertEquals(0.0, b1.computeGravForce(b2));
            assertEquals(0.0, b2.computeGravForce(b1));
        }

        @Test
        void testReturns1WhenTwoBodiesHave1MassAnd1UnitApart() {
            Body2D<Object> b1 = new Body2D<>(1, 1, 0, new Object());
            Body2D<Object> b2 = new Body2D<>(1, 1, -1, new Object());
            assertEquals(1.0, b1.computeGravForce(b2));
            assertEquals(1.0, b2.computeGravForce(b1));
        }

        @Test
        void testReturnsMWhenTwoBodies1UnitApartAndOneHas1Mass() {
            double m = 125;
            Body2D<Object> b1 = new Body2D<>(1, 1, 0, new Object());
            Body2D<Object> b2 = new Body2D<>(m, 1, -1, new Object());
            assertEquals(m, b1.computeGravForce(b2));
            assertEquals(m, b2.computeGravForce(b1));
        }

        @Test
        void testReturns0WhenOneBodyHas0Mass() {
            Body2D<Object> b1 = new Body2D<>(1, -4, 10.33, new Object());
            Body2D<Object> b2 = new Body2D<>(0, 121, -102.1, new Object());
            assertEquals(0.0, b1.computeGravForce(b2));
            assertEquals(0.0, b2.computeGravForce(b1));
        }

        @Test
        void testReturns0WhenBothBodiesHave0Mass() {
            Body2D<Object> b1 = new Body2D<>(0, -40, 1.33, new Object());
            Body2D<Object> b2 = new Body2D<>(0, -1210, -10992.1, new Object());
            assertEquals(0.0, b1.computeGravForce(b2));
            assertEquals(0.0, b2.computeGravForce(b1));
        }

        @Test
        void testReturnsCorrectValueForPointsInSameQuadrant() {
            double expectedForce = 6.1728395061728385;
            Body2D<Object> b1 = new Body2D<>(100, 100, 100, new Object());
            Body2D<Object> b2 = new Body2D<>(1000, 10, 10, new Object());
            assertEquals(expectedForce, b1.computeGravForce(b2));
            assertEquals(expectedForce, b2.computeGravForce(b1));
        }

        @Test
        void testReturnsCorrectValueForPointsInDifferentQuadrants() {
            double expectedForce = 6.1728395061728385;
            Body2D<Object> b1 = new Body2D<>(100, 1, -1, new Object());
            Body2D<Object> b2 = new Body2D<>(1000, -89, -91, new Object());
            assertEquals(expectedForce, b1.computeGravForce(b2));
            assertEquals(expectedForce, b2.computeGravForce(b1));
        }
    }

    @Nested
    class TestDistanceTo {
        @Test
        void testZeroDistanceAtOrigin() {
            assertEquals(0, ORIGIN_EMPTY.distanceTo(ORIGIN_EMPTY));
        }

        @Test
        void testZeroDistanceAtOtherPoint() {
            Body2D<Object> body = new Body2D<>(10, 100, -100, new Object());
            assertEquals(0, body.distanceTo(body));
            assertEquals(0, body.distanceTo(body.plus(ORIGIN_EMPTY)));
        }

        @Test
        void testLongerDistanceAlongXAxis() {
            double distance = 1000;
            Body2D<Object> body = new Body2D<>(10, distance, 0, new Object());
            assertEquals(distance, body.distanceTo(ORIGIN_EMPTY));
            assertEquals(distance, ORIGIN_EMPTY.distanceTo(body));
        }

        @Test
        void testLongerDistanceAlongYAxis() {
            double distance = 50000;
            Body2D<Object> body = new Body2D<>(10, 0, distance, new Object());
            assertEquals(distance, body.distanceTo(ORIGIN_EMPTY));
            assertEquals(distance, ORIGIN_EMPTY.distanceTo(body));
        }

        @Test
        void testOriginToOtherPoint() {
            Body2D<Object> body = new Body2D<>(10, 10, -10, new Object());
            double distance = Math.sqrt(200);
            assertEquals(distance, body.distanceTo(ORIGIN_EMPTY));
            assertEquals(distance, ORIGIN_EMPTY.distanceTo(body));
        }

        @Test
        void testTwoPointsInSameQuadrant() {
            Body2D<Object> body1 = new Body2D<>(10, -10, -100, new Object());
            Body2D<Object> body2 = new Body2D<>(10, -100, -190, new Object());
            double distance = Math.sqrt(2 * 90 * 90);
            assertEquals(distance, body1.distanceTo(body2));
            assertEquals(distance, body2.distanceTo(body1));
        }

        @Test
        void testTwoPointsInDifferentQuadrants() {
            Body2D<Object> body1 = new Body2D<>(10, -210, 100, new Object());
            Body2D<Object> body2 = new Body2D<>(10, 105, -1900, new Object());
            double distance = Math.sqrt(315 * 315 + 2000 * 2000);
            assertEquals(distance, body1.distanceTo(body2));
            assertEquals(distance, body2.distanceTo(body1));
        }
    }

    @Nested
    class TestPlus {
        private void doOneTest(double expectedX, double expectedY, double expectedMass, Body2D<Object> a, Body2D<Object> b) {
            Body2D<Object> result = a.plus(b);
            assertEquals(expectedX, result.x);
            assertEquals(expectedY, result.y);
            assertEquals(expectedMass, result.mass);
            assertNull(result.value);
        }

        @Test
        void testAddBothIn1stQuadrant() {
            double mass1 = 100, x1 = 500, y1 = 21, mass2 = 300, x2 = 30, y2 = 300;
            Body2D<Object> body1 = new Body2D<>(mass1, x1, y1, new Object());
            Body2D<Object> body2 = new Body2D<>(mass2, x2, y2, new Object());
            doOneTest(147.5, 230.25, 400, body1, body2);
        }

        @Test
        void testAddBothIn2ndQuadrant() {
            double mass1 = 10000, x1 = -350000, y1 = 100000, mass2 = 100000, x2 = -1000000, y2 = 30000;
            Body2D<Object> body1 = new Body2D<>(mass1, x1, y1, new Object());
            Body2D<Object> body2 = new Body2D<>(mass2, x2, y2, new Object());
            doOneTest(-940909.0909090909, 36363.63636363636,110000, body1, body2);
        }

        @Test
        void testAddBothIn3rdQuadrant() {
            double mass1 = 100, x1 = -100, y1 = -100, mass2 = 500, x2 = -500, y2 = -500;
            Body2D<Object> body1 = new Body2D<>(mass1, x1, y1, new Object());
            Body2D<Object> body2 = new Body2D<>(mass2, x2, y2, new Object());
            doOneTest(-433.3333333333333, -433.3333333333333, 600, body1, body2);
        }

        @Test
        void testAddBothIn4thQuadrant() {
            double mass1 = 300, x1 = 250, y1 = -400, mass2 = 300, x2 = 100, y2 = -100;
            Body2D<Object> body1 = new Body2D<>(mass1, x1, y1, new Object());
            Body2D<Object> body2 = new Body2D<>(mass2, x2, y2, new Object());
            doOneTest(175, -250, 600, body1, body2);
        }

        @Test
        void testAdd1stAnd3rdQuadrant() {
            double mass1 = 3200, x1 = -2500, y1 = -400, mass2 = 3030, x2 = 1000, y2 = 10000;
            Body2D<Object> body1 = new Body2D<>(mass1, x1, y1, new Object());
            Body2D<Object> body2 = new Body2D<>(mass2, x2, y2, new Object());
            doOneTest(-797.7528089887641, 4658.105939004816, 6230, body1, body2);
        }

        @Test
        void testAddOneAtOrigin() {
            double mass1 = 100000, x1 = 0, y1 = 0, mass2 = 3000, x2 = 1000, y2 = -2300;
            Body2D<Object> body1 = new Body2D<>(mass1, x1, y1, new Object());
            Body2D<Object> body2 = new Body2D<>(mass2, x2, y2, new Object());
            doOneTest(29.12621359223301, -66.99029126213593, 103000, body1, body2);
        }

        @Test
        void testAddBothInSameLocation() {
            double mass1 = 1000, mass2 = 3000, x = -2000, y = -150;
            Body2D<Object> body1 = new Body2D<>(mass1, x, y, new Object());
            Body2D<Object> body2 = new Body2D<>(mass2, x, y, new Object());
            doOneTest(x, y, 4000,  body1, body2);
        }

        @Test
        void testZeroBodyPlusZeroBodyEqualsZero() {
            doOneTest(0, 0, 0, ORIGIN_EMPTY, ORIGIN_EMPTY);
        }

        @Test
        void testNonZeroBodyPlusZeroBodyEqualsNonZeroBody() {
            double myMass = 100, myX = -30, myY = 100;
            Body2D<Object> nonZero = new Body2D<>(myMass, myX, myY, new Object());
            doOneTest(myX, myY, myMass, nonZero, ORIGIN_EMPTY);
        }

        @Test
        void testZeroBodyPlusNonZeroBodyEqualsNonZeroBody() {
            double myMass = 100, myX = 30, myY = 100;
            Body2D<Object> nonZero = new Body2D<>(myMass, myX, myY, new Object());
            doOneTest(myX, myY, myMass, ORIGIN_EMPTY, nonZero);
        }

        @Test
        void testTwoNonOriginZeroMassBodiesEqualsOrigin() {
            double myX1 = 30, myY1 = 100, myX2 = 1002, myY2 = 10;
            Body2D<Object> body1 = new Body2D<>(0, myX1, myY1, new Object());
            Body2D<Object> body2 = new Body2D<>(0, myX2, myY2, new Object());
            doOneTest(0, 0, 0, body1, body2);
        }
    }

    @Nested
    class TestIllegalArguments {

        private void doInvalidConstructorArgsTest(double mass, double x, double y) {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> new Body2D<>(mass, x, y, new Object())
            );
        }

        @ParameterizedTest
        @ArgumentsSource(InfiniteAndNaNAndNegativeDoubleArgsProvider.class)
        void testConstructorThrowsForInvalidMass(double mass) {
            doInvalidConstructorArgsTest(mass, 100, -100);
        }

        @ParameterizedTest
        @ArgumentsSource(InfiniteAndNaNDoubleArgsProvider.class)
        void testConstructorThrowsForInvalidX(double x) {
            doInvalidConstructorArgsTest(120, x, 100);
        }

        @ParameterizedTest
        @ArgumentsSource(InfiniteAndNaNDoubleArgsProvider.class)
        void testConstructorThrowsForInvalidY(double y) {
            doInvalidConstructorArgsTest(120, 100, y);
        }
    }
}
