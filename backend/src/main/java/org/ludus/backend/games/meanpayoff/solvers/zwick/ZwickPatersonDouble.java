package org.ludus.backend.games.meanpayoff.solvers.zwick;

import org.ludus.backend.games.meanpayoff.MeanPayoffGame;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Bram van der Sanden
 */
public class ZwickPatersonDouble {

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
    protected static <V, E> Map<V, Double> solve(MeanPayoffGame<V, E, Double> game, Double epsilon) {
        // We choose k such that the estimate lies within the epsilon bound of the actual value.
        return computeEstimate(game, epsilon);
    }

    /**
     * Compute an estimate on the value of each vertex in the graph.
     *
     * @param <V> vertex type
     * @param <E> edge type
     * @param game mean-payoff game
     * @return value estimate of each vertex
     */
    protected static <V, E> Map<V, Double> computeEstimate(MeanPayoffGame<V, E, Double> game, Double epsilon) {
        Map<V, Double> valueMap = computePathValues(game, epsilon);

        Map<V, Double> estimateMap = new HashMap<>();

        Integer n = game.getVertices().size();
        Double k = (2.0 * n * game.getMaxAbsValue()) / epsilon;

        for (V v : valueMap.keySet()) {
            estimateMap.put(v, valueMap.get(v) / k);
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
    private static <V, E> Map<V, Double> computePathValues(MeanPayoffGame<V, E, Double> game, Double epsilon) {
        Map<V, Double> valueMap = new HashMap<>();

        // Initialize the v_k value for each vertex.
        for (V v : game.getVertices()) {
            valueMap.put(v, 0.0);
        }

        // Determine the value of each vertex.        
        Integer n = game.getVertices().size();
        Double k = (2.0 * n * game.getMaxAbsValue()) / epsilon;
        for (int i = 1; i <= Math.round(k); i++) {
            // Store the old vector.
            Map<V, Double> oldVector = valueMap;

            // Compute new vector.
            Map<V, Double> newVector = new HashMap<>();
            for (V v : game.getVertices()) {
                Double newValue;
                if (game.getV0().contains(v)) {
                    // Update the value.
                    Double maxValue = Double.NEGATIVE_INFINITY;
                    for (E e : game.outgoingEdgesOf(v)) {
                        V u = game.getEdgeTarget(e);
                        maxValue = Math.max(maxValue, oldVector.get(u) + game.getWeight(e));
                    }
                    newValue = maxValue;
                } else {
                    // Update the value.            
                    Double minValue = Double.POSITIVE_INFINITY;
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
