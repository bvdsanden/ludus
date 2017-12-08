package org.ludus.backend.games.algorithms;

/**
 * @author Bram van der Sanden
 * <p>
 * Based on the code given at
 * {@code http://stackoverflow.com/questions/356807/java-double-comparison-epsilon}.
 * </p>
 */
public class DoubleFunctions {

    public final static double EPSILON = 10E-5;

    public final static double DELTA = 10E-5;


    public final static double MACHINE_PRECISION = 10E-14;

    /**
     * Returns true if two doubles are considered equal. Tests if the absolute
     * difference between two doubles has a difference less then .00001.
     *
     * @param a double to compare.
     * @param b double to compare.
     * @return true true if two doubles are considered equal.
     */
    public static boolean equalTo(double a, double b) {
        return a == b || Math.abs(a - b) < EPSILON;
    }

    /**
     * Returns true if two doubles are considered equal. Tests if the absolute
     * difference between the two doubles has a difference less then a given
     * double (epsilon).
     *
     * @param a       double to compare.
     * @param b       double to compare
     * @param epsilon double which is compared to the absolute difference of two
     *                doubles to determine if they are equal.
     * @return true if a is considered equal to b.
     */
    public static boolean equalTo(double a, double b, double epsilon) {
        return a == b || Math.abs(a - b) < epsilon;
        //return nearlyEqual(a,b,epsilon);
    }

    public static boolean nearlyEqual(double a, double b, double epsilon) {
        final double absA = Math.abs(a);
        final double absB = Math.abs(b);
        final double diff = Math.abs(a - b);

        if (a == b) { // shortcut, handles infinities
            return true;
        } else if (a == 0 || b == 0 || diff < Double.MIN_NORMAL) {
            // a or b is zero or both are extremely close to it
            // relative error is less meaningful here
            return diff < (epsilon * Double.MIN_NORMAL);
        } else { // use relative error
            return diff / Math.min((absA + absB), Double.MAX_VALUE) < epsilon;
        }
    }

    /**
     * Returns true if the first double is considered greater than the second
     * double. Test if the difference of first minus second is greater then
     * .00001.
     *
     * @param a first double
     * @param b second double
     * @return true if the first double is considered greater than the second
     * double
     */
    public static boolean greaterThan(double a, double b) {
        return greaterThan(a, b, EPSILON);
    }

    /**
     * Returns true if the first double is considered greater than the second
     * double. Test if the difference of first minus second is greater then a
     * given double (epsilon).
     *
     * @param a first double
     * @param b second double
     * @return true if the first double is considered greater than the second
     * double
     */
    public static boolean greaterThan(double a, double b, double epsilon) {
        return a - b > epsilon;
    }

    /**
     * Returns true if the first double is considered less than the second
     * double. Test if the difference of second minus first is greater then
     * {@code EPSILON}.
     *
     * @param a first double
     * @param b second double
     * @return true if the first double is considered less than the second
     * double
     */
    public static boolean lessThan(double a, double b) {
        return lessThan(a, b, EPSILON);
    }

    /**
     * Returns true if the first double is considered less than the second
     * double. Test if the difference of second minus first is greater then a
     * given double (epsilon).
     *
     * @param a first double
     * @param b second double
     * @return true if the first double is considered less than the second
     * double
     */
    public static boolean lessThan(double a, double b, double epsilon) {
        return b - a > epsilon;
    }
}
