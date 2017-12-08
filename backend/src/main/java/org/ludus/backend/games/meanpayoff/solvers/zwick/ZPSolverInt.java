package org.ludus.backend.games.meanpayoff.solvers.zwick;

import org.ludus.backend.datastructures.tuple.Triple;
import org.ludus.backend.games.meanpayoff.MeanPayoffGame;
import org.apache.commons.math3.fraction.Fraction;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Solve the problem instances using the Zwick-Paterson algorithm.
 *
 * @author Bram van der Sanden
 */
public class ZPSolverInt {

    /**
     * Calculate a three-way partition on the values of the vertices based on
     * the given payoff.
     *
     * @param <V>         vertex type
     * @param <E>         edge type
     * @param game        mean-payoff game
     * @param payoffValue payoff value
     * @return three-way partition on the values based on the given payoff
     */
    public static <V, E> Triple<Set<V>, Set<V>, Set<V>>
    getThreeWayPartition(MeanPayoffGame<V, E, Integer> game, Fraction payoffValue) {
        Map<V, Fraction> estimateMap = ZwickPatersonInt.computeEstimate(game);

        Set<V> smaller = new HashSet<>();
        Set<V> equal = new HashSet<>();
        Set<V> larger = new HashSet<>();

        game.getVertices().forEach((v) -> {
            // The vertex value is an estimate. If it is within a distance of
            // delta from the payoffValue, payoffValue is the closest rational.
            Fraction vertexValue = estimateMap.get(v);
            Integer N = game.getVertices().size();
            Fraction delta;
            if (N <= 1) {
                // Avoid division by zero.
                delta = Fraction.ZERO;
            } else {
                delta = new Fraction(1, 2 * N * (N - 1));
            }

            Fraction leftEqualBound = payoffValue.subtract(delta);
            Fraction rightEqualBound = payoffValue.add(delta);

            if (vertexValue.compareTo(leftEqualBound) < 0) {
                // vertexValue < payoffValue - delta
                smaller.add(v);
            } else if (vertexValue.compareTo(rightEqualBound) > 0) {
                // vertexValue > payoffValue + delta
                larger.add(v);
            } else {
                // payoffValue - delta <= vertexValue <= payoffValue + delta
                equal.add(v);
            }
        });
        return Triple.of(smaller, equal, larger);
    }

    /**
     * Get the exact payoff value of each vertex in the game graph.
     *
     * @param <V>  vertex type
     * @param <E>  edge type
     * @param game mean-payoff game
     * @return a map containing for each vertex the unique payoff value
     */
    public static <V, E> Map<V, Fraction> getValues(MeanPayoffGame<V, E, Integer> game) {
        return ZwickPatersonInt.solve(game);
    }

}
