package org.ludus.backend.games.meanpayoff.solvers.policy;

import org.ludus.backend.datastructures.tuple.Tuple;
import org.ludus.backend.games.StrategyVector;
import org.ludus.backend.games.algorithms.DoubleFunctions;
import org.ludus.backend.games.ratio.solvers.policy.RatioGamePolicyIteration;

import java.util.Map;

/**
 * Policy Iteration Algorithm to solve mean-payoff games.
 * <p>
 * The algorithm start with random strategies for both players, and then starts
 * improving the strategies until convergence. When the algorithm terminates,
 * both strategies are optimal.
 * <p>
 * The algorithm has been adapted from the MDG algorithm by Chaloupka, see also
 * Chaloupka, 2011, Algorithms for Mean-Payoff and Energy Games (PhD thesis).
 *
 * @author Bram van der Sanden
 */
public class PolicyIterationDouble {

    /**
     * Find the values of the vertices and an optimal strategy for the given
     * ratio game.
     *
     * @param <V>  vertex type
     * @param <E>  edge type
     * @param game game graph
     * @return tuple with map containing value of each vertex and the optimal strategy vector
     */
    public static <V, E> Tuple<Map<V, Double>, StrategyVector<V, E>> solve(
            MeanPayoffGamePolicyIteration<V, E, Double> game) {
        return solve(game, DoubleFunctions.EPSILON);
    }

    /**
     * Find the values of the vertices and an optimal strategy for the given
     * ratio game.
     *
     * @param <V>     vertex type
     * @param <E>     edge type
     * @param game    game graph
     * @param epsilon epsilon value for equality on real numbers
     * @return value for each vertex and optimal strategy vector
     */
    public static <V, E> Tuple<Map<V, Double>, StrategyVector<V, E>> solve(
            MeanPayoffGamePolicyIteration<V, E, Double> game, Double epsilon) {
        // Convert the given mean payoff game to a ratio game.
        RatioGamePolicyIteration<V, E, Double> converted = new RatioGameAdapter<>(game);

        // Solve the mean-payoff game using the ratio game solver.
        return org.ludus.backend.games.ratio.solvers.policy.PolicyIterationDouble.solve(converted, epsilon);
    }
}
