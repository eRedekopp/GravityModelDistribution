package com.github.eRedekopp.GravityModelDistribution;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

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

        private void doConstructorTest(double x, double y, double sideLength) {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> new Square(x, y, sideLength)
            );
        }

        @ParameterizedTest
        @ArgumentsSource(InfiniteAndNaNDoubleArgsProvider.class)
        void testConstructorThrowsForInvalidX(double x) {
            doConstructorTest(x, 10.0, 10);
        }

        @ParameterizedTest
        @ArgumentsSource(InfiniteAndNaNDoubleArgsProvider.class)
        void testConstructorThrowsForInvalidY(double y) {
            doConstructorTest(-10.5, y, 100);
        }

        @ParameterizedTest
        @ArgumentsSource(InfiniteAndNaNAndNegativeDoubleArgsProvider.class)
        void testConstructorThrowsForInvalidSideLen(double sideLen) {
            doConstructorTest(-10.0, 10.0, sideLen);
        }

        private void doContainsTest(double x, double y) {
            Square square = new Square(0, 0, 10);
            assertThrows(
                    IllegalArgumentException.class,
                    () -> square.contains(x, y)
            );
        }

        @ParameterizedTest
        @ArgumentsSource(InfiniteAndNaNDoubleArgsProvider.class)
        void testContainsThrowsForInvalidX(double x) {
            doContainsTest(x, -100.15);
        }

        @ParameterizedTest
        @ArgumentsSource(InfiniteAndNaNDoubleArgsProvider.class)
        void testContainsThrowsForInvalidY(double y) {
            doContainsTest(100.01, y);
        }

        private void doGetQuadrantTest(double x, double y) {
            Square square = new Square(10, 10, 100);
            assertThrows(
                    IllegalArgumentException.class,
                    () -> square.getQuadrant(x, y)
            );
        }

        @ParameterizedTest
        @ArgumentsSource(InfiniteAndNaNDoubleArgsProvider.class)
        void testGetQuadrantThrowsForInvalidX(double x) {
            doGetQuadrantTest(x, -211.12);
        }

        @ParameterizedTest
        @ArgumentsSource(InfiniteAndNaNDoubleArgsProvider.class)
        void testGetQuadrantThrowsForInvalidY(double y) {
            doGetQuadrantTest(-123.456, y);
        }
    }
}
