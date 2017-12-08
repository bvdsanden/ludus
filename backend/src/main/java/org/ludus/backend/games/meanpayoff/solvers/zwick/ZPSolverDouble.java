package org.ludus.backend.games.meanpayoff.solvers.zwick;

import org.ludus.backend.datastructures.tuple.Triple;
import org.ludus.backend.games.algorithms.DoubleFunctions;
import org.ludus.backend.games.meanpayoff.MeanPayoffGame;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.ludus.backend.games.algorithms.DoubleFunctions.greaterThan;
import static org.ludus.backend.games.algorithms.DoubleFunctions.lessThan;

/**
 * Solve the problem instances using the Zwick-Paterson algorithm.
 *
 * @author Bram van der Sanden
 */
public class ZPSolverDouble {

    /**
     * Calculate a three-way partition on the values of the vertices based on
     * the given payoff.
     *
     * @param <V>         vertex type
     * @param <E>         edge type
     * @param game        mean-payoff game
     * @param payoffValue payoff value
     * @return three-way partition on the values of the vertices given the payoff
     */
    public static <V, E> Triple<Set<V>, Set<V>, Set<V>>
    getThreeWayPartition(MeanPayoffGame<V, E, Double> game, Double payoffValue) {
        return getThreeWayPartition(game, payoffValue, DoubleFunctions.EPSILON);
    }

    /**
     * Calculate a three-way partition on the values of the vertices based on
     * the given payoff.
     *
     * @param <V>         vertex type
     * @param <E>         edge type
     * @param game        mean-payoff game
     * @param payoffValue payoff value
     * @param epsilon     epsilon value
     * @return three-way partition on the values of the vertices given the payoff
     */
    public static <V, E> Triple<Set<V>, Set<V>, Set<V>>
    getThreeWayPartition(MeanPayoffGame<V, E, Double> game, Double payoffValue, Double epsilon) {
        Map<V, Double> estimateMap = ZwickPatersonDouble.computeEstimate(game, epsilon);

        Set<V> smaller = new HashSet<>();
        Set<V> equal = new HashSet<>();
        Set<V> larger = new HashSet<>();

        game.getVertices().forEach((v) -> {
            // The vertex value is an estimate. If it is within a distance of
            // delta from the payoffValue, payoffValue is the closest rational.
            Double vertexValue = estimateMap.get(v);

            if (lessThan(vertexValue, payoffValue, epsilon)) {
                smaller.add(v);
            } else if (greaterThan(vertexValue, payoffValue, epsilon)) {
                larger.add(v);
            } else {
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
    public static <V, E> Map<V, Double> getValues(MeanPayoffGame<V, E, Double> game, Double epsilon) {
        return ZwickPatersonDouble.solve(game, epsilon);
    }

    /**
     * Get the exact payoff value of each vertex in the game graph.
     *
     * @param <V>  vertex type
     * @param <E>  edge type
     * @param game mean-payoff game
     * @return a map containing for each vertex the unique payoff value
     */
    public static <V, E> Map<V, Double> getValues(MeanPayoffGame<V, E, Double> game) {
        return ZwickPatersonDouble.solve(game, DoubleFunctions.EPSILON);
    }

}
