package org.ludus.backend.games.meanpayoff.solvers.zwick;

import org.ludus.backend.games.meanpayoff.MeanPayoffGame;
import org.apache.commons.math3.fraction.Fraction;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Bram van der Sanden
 */
public class ZwickPatersonInt {

    /**
     * Solve the given mean-payoff game using the Zwick-Paterson algorithm.
     * <p>
     * Note: the implementation will work for graphs up to a size of ~300.000
     * nodes. For larger graphs, a refinement is needed using BigInteger.
     *
     * @param <V>  vertex type
     * @param <E>  edge type
     * @param game mean-payoff game
     * @return value of each vertex
     */
    protected static <V, E> Map<V, Fraction> solve(MeanPayoffGame<V, E, Integer> game) {
        Map<V, Integer> valueMap = computePathValues(game);

        // Calculate the exact value for each vertex.
        Map<V, Fraction> mwMap = new HashMap<>();

        Integer n = game.getVertices().size();
        Integer k = 4 * n * n * n * game.getMaxAbsValue();
        Integer denominator = 2 * n * (n - 1);

        for (V v : game.getVertices()) {
            // Estimate of the vertex is v_k / k.
            double estimate = (valueMap.get(v) * 1.0f) / (k * 1.0f);
            double distance = 1.0f / (denominator * 1.0f);

            double leftBound = estimate - distance;
            double rightBound = estimate + distance;

            // Find unique rational number within interval.
            Fraction fraction = findUniqueRational(leftBound, rightBound, game.getMaxAbsValue(), n);
            mwMap.put(v, fraction);
        }

        return mwMap;
    }

    /**
     * Find the unique rational number that lies within the interval given by
     * [leftBound,rightBound].
     *
     * @param leftBound  left bound of the interval
     * @param rightBound right bound of the interval
     * @param W          maximum weight in the graph
     * @param N          number of nodes in the graph
     * @return the unique rational that lies within the interval given by
     * [leftBound,rightBound].
     */
    private static Fraction findUniqueRational(Double leftBound, Double rightBound, Integer W, Integer N) {
        for (int w = -N * W; w <= N * W; w++) {
            for (int n = 1; n <= N; n++) {
                double fraction = (w * 1.0f) / (n * 1.0f);
                if (leftBound <= fraction && fraction <= rightBound) {
                    return new Fraction(w, n);
                }
            }
        }
        assert (false);
        return null;
    }

    /**
     * Compute an estimate on the value of each vertex in the graph.
     *
     * @param <V> vertex type
     * @param <E> edge type
     * @param game game graph
     * @return map with estimate value for each vertex
     */
    protected static <V, E> Map<V, Fraction> computeEstimate(MeanPayoffGame<V, E, Integer> game) {
        Map<V, Integer> valueMap = computePathValues(game);

        Map<V, Fraction> estimateMap = new HashMap<>();

        Integer n = game.getVertices().size();
        Integer k = 4 * n * n * n * game.getMaxAbsValue();

        for (V v : valueMap.keySet()) {
            // Avoid division by zero.
            if (k != 0) {
                estimateMap.put(v, new Fraction(valueMap.get(v), k));
            } else {
                // All weights in the graph are zero. So value is also zero.
                estimateMap.put(v, Fraction.ZERO);
            }
        }

        return estimateMap;
    }

    /**
     * Compute the v_k value for each node in the game graph. This number
     * divided by k serves as an estimation for the exact rational value of the
     * each vertex.
     * <p>
     * The game is played for exactly k steps, and the weight of this path is
     * the outcome of the game.
     *
     * @param <V>  vertex type
     * @param <E>  edge type
     * @param game mean-payoff game
     * @return the v_k value of each vertex in the game graph.
     */
    private static <V, E> Map<V, Integer> computePathValues(MeanPayoffGame<V, E, Integer> game) {
        Map<V, Integer> valueMap = new HashMap<>();

        // Initialize the v_k value for each vertex.
        for (V v : game.getVertices()) {
            valueMap.put(v, 0);
        }

        // Determine the value of each vertex.        
        Integer n = game.getVertices().size();
        Integer k = 4 * n * n * n * game.getMaxAbsValue();
        for (int i = 1; i <= k; i++) {
            // Store the old vector.
            Map<V, Integer> oldVector = valueMap;

            // Compute new vector.
            Map<V, Integer> newVector = new HashMap<>();
            for (V v : game.getVertices()) {
                Integer newValue;
                if (game.getV0().contains(v)) {
                    // Update the value.
                    Integer maxValue = Integer.MIN_VALUE;
                    for (E e : game.outgoingEdgesOf(v)) {
                        V u = game.getEdgeTarget(e);
                        maxValue = Math.max(maxValue, oldVector.get(u) + game.getWeight(e));
                    }
                    newValue = maxValue;
                } else {
                    // Update the value.            
                    Integer minValue = Integer.MAX_VALUE;
                    for (E e : game.outgoingEdgesOf(v)) {
                        V u = game.getEdgeTarget(e);
                        minValue = Math.min(minValue, oldVector.get(u) + game.getWeight(e));
                    }
                    newValue = minValue;
                }
                newVector.put(v, newValue);
            }

            // Value map of this iteration completed.
            valueMap = newVector;
        }
        return valueMap;
    }

}
